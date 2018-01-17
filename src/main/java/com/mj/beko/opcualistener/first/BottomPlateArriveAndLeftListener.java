package com.mj.beko.opcualistener.first;

import com.mj.beko.codeScanner.GetBarcode;
import com.mj.beko.domain.ConsumedParts;
import com.mj.beko.domain.MesToFlowTestRange;
import com.mj.beko.domain.Order;
import com.mj.beko.domain.StationCycleTime;
import com.mj.beko.httpclient.HttpTemplate;
import com.mj.beko.opcua.OpcUaClientException;
import com.mj.beko.opcua.OpcUaClientTemplate;
import com.mj.beko.opcua.OpcUaUtil;
import com.mj.beko.opcualistener.AsynPushDataToScreen;
import com.mj.beko.repository.ConsumedPartsRepository;
import com.mj.beko.repository.OrderRepository;
import com.mj.beko.service.*;
import com.prosysopc.ua.client.MonitoredDataItem;
import com.prosysopc.ua.client.MonitoredDataItemListener;
import com.prosysopc.ua.client.UaClient;
import lombok.extern.slf4j.Slf4j;
import org.opcfoundation.ua.builtintypes.DataValue;
import org.opcfoundation.ua.builtintypes.NodeId;
import org.opcfoundation.ua.builtintypes.UnsignedByte;
import org.opcfoundation.ua.builtintypes.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import javax.inject.Inject;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wanghb
 * 下底板工位监听器
 */
@Slf4j
@Component
public class BottomPlateArriveAndLeftListener implements MonitoredDataItemListener {

    @Inject
    private OpcUaClientTemplate opcUaClientTemplate;

    @Inject
    private SimpMessagingTemplate template;

    @Inject
    private RedisTemplate<String, Object> redisTemplate;

    @Inject
    private OrderService orderService;

    @Inject
    private GetBarcode getBarcode;

    @Inject
    private StationCycleTimeService stationCycleTimeService;

    @Inject
    private PalletService palletService;

    @Inject
    private TaskExecutor taskExecutor;

    @Inject
    private AsynPushDataToScreen asynPushDataToScreen;

    @Inject
    private ConsumedPartsRepository consumedPartsRepository;

    @Inject
    private MesToFlowTestService mesToFlowTestService;
    @Inject
    private OrderRepository orderRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(BottomPlateArriveAndLeftListener.class);

    public static final String BOTTOM_PLATE_CHECKER_RESULT = "/BottomPlateCheckerApi/api/Values/LeakageTestResult?dummyNo={0}";
    public static final String BOTTOM_PLATE_CHECKER_DATA = "/BottomPlateCheckerApi/api/Values/LeakageTestData?dummyNo={0}";

    @Inject
    private HttpTemplate httpTemplate;

    @Override
    public void onDataChange(MonitoredDataItem monitoredDataItem, DataValue oldValue, DataValue newValue) {
        LOGGER.info("进入监听器，变化前的值为：{}，变化后的值为：{}", new Object[]{oldValue, newValue});
        if (!OpcUaUtil.isNewNodeValueValid("PLC1", monitoredDataItem.getNodeId(), oldValue, newValue)) {
            return;
        }
        String node = monitoredDataItem.getNodeId().getValue().toString();
        //RFID信号触发
        if ("\"information\".\"Station1_ID\"".equals(node)) {
            String palletNo = ((UnsignedByte[])newValue.getValue().getValue())[0].intValue() + "";
            if ("0".equals(palletNo)) return;
            taskExecutor.execute(() -> doThingsWhenArrived(palletNo));
        } else if ("\"information\".\"Station1_leave\"".equals(node)){
            if (newValue.getValue().intValue() == 0) return;
            doThingWhenLeave();
        }
    }

    /**
     * 下底盘工位到位时的业务逻辑
     */
    @Async("taskExecutor")
    public void doThingsWhenArrived (String palletNo) {
        //1、记录到位时间点
        Timestamp arrivedTime = Timestamp.from(Instant.now());

        //2、查询当前工单和下一工单信息
        Map<String, Order> currentOrderAndNextOrder = orderService.getCurrentOrderAndNextOrder();
        Order currentOrder = currentOrderAndNextOrder.get("currentOrder");
        Order nextOrder = currentOrderAndNextOrder.get("nextOrder");
        if (currentOrder == null) return;

        //将产品类型写给一段plc
        MesToFlowTestRange flowStepRangeValue = mesToFlowTestService.getFlowStepRangeValue(currentOrder.getProductNo());
        String[] proTypeAndTorque = flowStepRangeValue.getScrews().split("_");
        byte screwType = Byte.parseByte(proTypeAndTorque[0]);
        byte torque = Byte.parseByte(proTypeAndTorque[1]);
        int proType = Integer.parseInt(proTypeAndTorque[2]);
        UaClient uaClient = opcUaClientTemplate.getUaClientList().get(0);
        NodeId screwTypeNode = new NodeId(3, "\"information\".\"Products_Screw_Robot\"");
        NodeId torqueNode = new NodeId(3, "\"information\".\"Torque_Choose\"");
        NodeId proTypeNode = new NodeId(3, "\"information\".\"ProductsA_or_B\"");
        try {
            opcUaClientTemplate.writeNodeValue(uaClient, screwTypeNode, screwType);
            opcUaClientTemplate.writeNodeValue(uaClient, torqueNode, torque);
            opcUaClientTemplate.writeNodeValue(uaClient, proTypeNode, proType);
        } catch (OpcUaClientException e) {
            e.printStackTrace();
            LOGGER.info("给一段PLC写产品类型失败");
        }

        //3、前工单和下一工单信息异步推送到前台，以及异步查询工艺信息并推送到前台
        taskExecutor.execute(() -> asynPushDataToScreen.queryOperationsAndPush(currentOrder, nextOrder, "BottomPlate", palletNo));

        //4、调用扫描枪，读取下底盘条码
        String barcode = getBarcode.getBarcode();
        if (barcode == null){
            log.info("cant get barCode for palletNo,{}",palletNo);
            return;
        }

        //5、调用beko http API验证下底盘信息
//        boolean result = httpTemplate.getForObject(httpTemplate.getBekoApiHttpSchemeHierarchical()
//                + MessageFormat.format(BOTTOM_PLATE_CHECKER_RESULT, barcode), Boolean.class);
        //测试使用
        boolean result = true;
        Map<String, String> barcodeRes = new HashMap<>();
        barcodeRes.put("barcode", barcode);
        if (result) {
            //6、调用另一个beko http API获取下底盘信息
//            BottomPlateDataList<BottomPlateData> bottomPlateDataList =  httpTemplate.getForObject(httpTemplate.getBekoApiHttpSchemeHierarchical()
//                    + MessageFormat.format(BOTTOM_PLATE_CHECKER_DATA, barcode), BottomPlateDataListVo.class);
//            String partNo = bottomPlateDataList.getBottomPlateDataList().get(0).getPartNo();
            String partNo = null;
            boolean flag = false;
            if (partNo != null) {
                //根据产品类型和工位获取ComsumerPart
                List<ConsumedParts> consumedPartList = consumedPartsRepository.getConsumPartsByProducntNoAndStation(currentOrder.getProductNo(), "BottomPlate");
                for (ConsumedParts cp : consumedPartList) {
                    if (cp != null && partNo.equals(cp.getPartId())) {
                        flag = true;
                        break;
                    }
                }
            }

            //下底盘匹配通过
            //测试使用
            if (!flag) {
//            if (flag) {
                barcodeRes.put("result", "OK");

                //如果订单状态为0，则改为1，并将订单开始时间存入数据库中
                if ("0".equals(currentOrder.getStatus())) {
                    //将订单状态改为1，并将订单开始时间存入数据库中
                    orderService.updateOrderStatusByOrderNo(currentOrder.getOrderNo(), "1", Timestamp.from(Instant.now()));
                }

                //7、将当前工单编号、当前产品类型和下一工单编号绑定到托盘中并放入到缓存
                HashOperations<String, String, String> cacheDatas = redisTemplate.opsForHash();
                String currentOrderNo = currentOrder.getOrderNo();
                //判断第一工位如果存在该托盘的缓存,则清除托盘的信息
                if(redisTemplate.hasKey(palletNo)){
                    redisTemplate.delete(palletNo);
                }
                if (!redisTemplate.hasKey(palletNo)) {
                    cacheDatas.put(palletNo, "currentOrderNo", currentOrderNo);  //当前订单编号
                    cacheDatas.put(palletNo, "currentProductNo", currentOrder.getProductNo()); //当前产品类型编号
                    cacheDatas.put(palletNo, "nextOrderNo", nextOrder.getOrderNo()); //下一订单编号

                    //8、将下底盘条码绑定到托盘放入缓存并保存到数据库中
                    cacheDatas.put(palletNo, "bottomPlateBarcode", barcode);  //下底盘条码
                    palletService.palletBindingProInfo(palletNo, currentOrderNo, currentOrder.getProductNo(), barcode);

                    //9、初始化redis中工单在每个工位中的数量，先查询有没有，有则不初始化，无则初始化
                    if (!redisTemplate.hasKey(currentOrderNo)) {
                        cacheDatas.put(currentOrderNo, "BottomPlate", "0");
                        cacheDatas.put(currentOrderNo, "TopPlate", "0");
                        cacheDatas.put(currentOrderNo, "Knobs", "0");
                        cacheDatas.put(currentOrderNo, "FireTest1", "0");
                        cacheDatas.put(currentOrderNo, "FireTest2", "0");
                        cacheDatas.put(currentOrderNo, "VisionControl", "0");
                        cacheDatas.put(currentOrderNo, "RemoveElectricAndGas", "0");
                    }
                    //将第二段所有工位的数量初始化并放入到缓存
                    ValueOperations dataOptions = redisTemplate.opsForValue();
                    if (!redisTemplate.hasKey("BurnerSupport1")) {
                        List<Map<String, String>> burnerSupport1List = new ArrayList<>();
                        List<Map<String, String>> burnerSupport2List = new ArrayList<>();
                        List<Map<String, String>> burnerSupport3List = new ArrayList<>();
                        List<Map<String, String>> burnerSupport4List = new ArrayList<>();
                        List<Map<String, String>> burnerCap1List = new ArrayList<>();
                        List<Map<String, String>> burnerCap2List = new ArrayList<>();
                        dataOptions.set("BurnerSupport1", burnerSupport1List);
                        dataOptions.set("BurnerSupport2", burnerSupport2List);
                        dataOptions.set("BurnerSupport3", burnerSupport3List);
                        dataOptions.set("BurnerSupport4", burnerSupport4List);
                        dataOptions.set("BurnerCap1", burnerCap1List);
                        dataOptions.set("BurnerCap2", burnerCap2List);
                    }
                    List<Map<String, String>> burnerSupport1List = (List<Map<String, String>>)dataOptions.get("BurnerSupport1");
                    boolean flag1 = false;  //用来判断是否包含当前工单
                    for (Map<String, String> map : burnerSupport1List) {
                        if (map.containsValue(currentOrderNo)){
                            flag1 = true;
                            break;
                        }
                    }
                    //如果不包含，则依次将二段所有机器人工位初始化数据，并放入到缓存中
                    if (!flag1){
                        Map<String, String> burnerSupport1Map = new HashMap<>();
                        burnerSupport1Map.put("orderNo", currentOrderNo);
                        burnerSupport1Map.put("number", "0");
                        burnerSupport1List.add(burnerSupport1Map);
                        List<Map<String, String>> burnerSupport2List = (List<Map<String, String>>)dataOptions.get("BurnerSupport2");
                        Map<String, String> burnerSupport2Map = new HashMap<>();
                        burnerSupport2Map.put("orderNo", currentOrderNo);
                        burnerSupport2Map.put("number", "0");
                        burnerSupport2List.add(burnerSupport2Map);
                        List<Map<String, String>> burnerSupport3List = (List<Map<String, String>>)dataOptions.get("BurnerSupport3");
                        Map<String, String> burnerSupport3Map = new HashMap<>();
                        burnerSupport3Map.put("orderNo", currentOrderNo);
                        burnerSupport3Map.put("number", "0");
                        burnerSupport3List.add(burnerSupport3Map);
                        List<Map<String, String>> burnerSupport4List = (List<Map<String, String>>)dataOptions.get("BurnerSupport4");
                        Map<String, String> burnerSupport4Map = new HashMap<>();
                        burnerSupport4Map.put("orderNo", currentOrderNo);
                        burnerSupport4Map.put("number", "0");
                        burnerSupport4List.add(burnerSupport4Map);
                        List<Map<String, String>> burnerCap1List = (List<Map<String, String>>)dataOptions.get("BurnerCap1");
                        Map<String, String> burnerCap1Map = new HashMap<>();
                        burnerCap1Map.put("orderNo", currentOrderNo);
                        burnerCap1Map.put("number", "0");
                        burnerCap1List.add(burnerCap1Map);
                        List<Map<String, String>> burnerCap2List = (List<Map<String, String>>)dataOptions.get("BurnerCap2");
                        Map<String, String> burnerCap2Map = new HashMap<>();
                        burnerCap2Map.put("orderNo", currentOrderNo);
                        burnerCap2Map.put("number", "0");
                        burnerCap2List.add(burnerCap2Map);
                        dataOptions.set("BurnerSupport1", burnerSupport1List);
                        dataOptions.set("BurnerSupport2", burnerSupport2List);
                        dataOptions.set("BurnerSupport3", burnerSupport3List);
                        dataOptions.set("BurnerSupport4", burnerSupport4List);
                        dataOptions.set("BurnerCap1", burnerCap1List);
                        dataOptions.set("BurnerCap2", burnerCap2List);
                    }

                    //10、将订单的上线数量加1
                    orderService.updateOnlineNumByOrderNo(currentOrderNo);
                    Order nowOrder =orderService.getOneOrderByOrderNo(currentOrderNo);
                    //如果上线数量达到的计划数量,则把订单状态更新为5。(表示当前订单的产品已经完全上线)
                    if(nowOrder.getOnlineNumber()==nowOrder.getQuantity()){
                       nowOrder.setStatus("5");
                       orderRepository.saveAndFlush(nowOrder);
                       //推送当前的订单已经全部上线
                       template.convertAndSend("/topic/lineLeaderScreen/noWorkOrderInLine",nowOrder.getOrderNo());
                    }
                    //查到该记录为空的时候，创建stationcycletime
                    List<StationCycleTime> stationCycleTimes=stationCycleTimeService.getOneStationCycleTimeByBottomPlateStation(barcode);
                    if(stationCycleTimes==null||stationCycleTimes.size()==0){
                        //11、将到位时间点保存到数据库中
                        StationCycleTime stationCycleTime = new StationCycleTime();
                        stationCycleTime.setBottomPlaceCode(barcode);
                        stationCycleTime.setOrderNo(currentOrderNo);
                        stationCycleTime.setProductNo(currentOrder.getProductNo());
                        stationCycleTime.setBottomPlateStationStart(arrivedTime);
                        stationCycleTimeService.save(stationCycleTime);
                    }

                } else {
                    //根据下底盘条码修改下底盘到位时间点
                    stationCycleTimeService.updateBottomPlateStationArriveTimeByBarcode(arrivedTime, barcode);
                }
                //将下底盘匹配通过的信息推送到一体机
                template.convertAndSend("/topic/BottomPlate/barcodeMatch", barcodeRes);
                //控制不是空托盘时PLC放行
                NodeId fxNode = new NodeId(3, "\"information\".\"Scan_OK\"");
                try {
                    opcUaClientTemplate.writeNodeValue(uaClient, fxNode, 1);
                } catch (OpcUaClientException e) {
                    e.printStackTrace();
                }
            } else {
                barcodeRes.put("result", "NO");
            }
        } else {
            barcodeRes.put("result", "NO");
        }
    }

    /**
     * 下底盘工位放行时的业务逻辑
     */
    private void doThingWhenLeave () {
        //记录放行的时间点
        Timestamp leaveTime = Timestamp.from(Instant.now());
        //通过RFID读取托盘编号
        UaClient uaClient = opcUaClientTemplate.getUaClientList().get(0);
        NodeId rfidPallet = new NodeId(3, "\"information\".\"Station1_ID\"");
        Variant readNodeVariant;
        String palletNo;
        try {
            readNodeVariant = opcUaClientTemplate.readNodeVariant(uaClient, rfidPallet);
            palletNo = ((UnsignedByte[])readNodeVariant.getValue())[0].intValue() + "";
        } catch (OpcUaClientException e) {
            System.out.println("********************没有读到RFID信息*************************");
            e.printStackTrace();
            return;
        }
        //如果是空托盘，直接放行
        if(!redisTemplate.hasKey(palletNo)) return;
        //将下底盘工位的产品数量加1
        HashOperations<String, String, String> cacheDatas = redisTemplate.opsForHash();
        String orderNo = cacheDatas.get(palletNo, "currentOrderNo");
        String bottomPlateBarcode = cacheDatas.get(palletNo, "bottomPlateBarcode");
        //根据下底盘条码获取下底盘工位放行时间点，如果不为空则不将工位完成数量加1
        Timestamp bottomPlateLeftTime = stationCycleTimeService.getbottomPlateLeftTimeByBottomPlateBarcode(bottomPlateBarcode);
        String count = cacheDatas.get(orderNo, "BottomPlate");
        if (bottomPlateLeftTime == null) {
            count = Integer.parseInt(count) + 1 + "";
            cacheDatas.put(orderNo, "BottomPlate", count);
        }
        //将放行时间点记录到数据库中
        stationCycleTimeService.updateBottomPlateStationCycleTimeByBarcode(leaveTime, bottomPlateBarcode);
        //将当前工位完成数量和平均时间推送到前台
        Map<String, String> countAndAverageTime = new HashMap<>();
        countAndAverageTime.put("currentFinished", count);
        //计算当前工单在下底盘工位的平均时间
        String averageTime = stationCycleTimeService.getBottomPlateAverageTime(orderNo);
        countAndAverageTime.put("averageTime", averageTime);
        template.convertAndSend("/topic/BottomPlate/countAndAverageTime", countAndAverageTime);
    }
}