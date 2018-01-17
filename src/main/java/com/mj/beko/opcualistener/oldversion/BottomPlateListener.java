package com.mj.beko.opcualistener.oldversion;

import com.mj.beko.codeScanner.GetBarcode;
import com.mj.beko.domain.Operation;
import com.mj.beko.domain.Order;
import com.mj.beko.domain.StationCycleTime;
import com.mj.beko.domain.Workstation;
import com.mj.beko.httpclient.HttpTemplate;
import com.mj.beko.opcua.OpcUaClientException;
import com.mj.beko.opcua.OpcUaClientTemplate;
import com.mj.beko.opcua.OpcUaUtil;
import com.mj.beko.service.*;
import com.mj.beko.web.websoket.Greeting;
import com.prosysopc.ua.client.MonitoredDataItem;
import com.prosysopc.ua.client.MonitoredDataItemListener;
import com.prosysopc.ua.client.UaClient;
import org.opcfoundation.ua.builtintypes.DataValue;
import org.opcfoundation.ua.builtintypes.NodeId;
import org.opcfoundation.ua.builtintypes.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import javax.inject.Inject;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wanghb
 * 下底板工位监听器
 */
@Component
public class BottomPlateListener implements MonitoredDataItemListener {

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
    private WorkstationService workstationService;

    @Inject
    private OperationService operationService;

    @Inject
    private PalletService palletService;

    @Inject
    private TaskExecutor taskExecutor;

    private static final Logger LOGGER = LoggerFactory.getLogger(BottomPlateListener.class);

    private String palletNo = "";

    public static final String BOTTOM_PLATE_CHECKER_RESULT = "/BottomPlateCheckerApi/api/Values/LeakageTestResult?dummyNo={0}";
    public static final String BOTTOM_PLATE_CHECKER_DATA = "/BottomPlateCheckerApi/api/Values/LeakageTestData?dummyNo={0}";

    @Inject
    private HttpTemplate httpTemplate;

    @Override
    public void onDataChange(MonitoredDataItem monitoredDataItem, DataValue oldValue, DataValue newValue) {
        LOGGER.info("进入监听器，变化前的值为：{}，变化后的值为：{}", new Object[]{oldValue, newValue});
        if (!OpcUaUtil.isNewNodeValueValid("PLC1", monitoredDataItem.getNodeId(), oldValue, newValue)
                || newValue.getValue().intValue() == 0) {
            return;
        }
        String node = monitoredDataItem.getNodeId().getValue().toString();
        //下底盘工位到位信号触发
        if ("BekoOpcua.BottomPlate.arrive".equals(node)) {
            doThingsWhenArrived();
        } else if ("BekoOpcua.BottomPlate.leave".equals(node)){
            doThingWhenLeave();
        }
    }

    /**
     * 下底盘工位到位时的业务逻辑
     */
    private void doThingsWhenArrived () {
        //记录到位时间点
        Timestamp arrivedTime = Timestamp.from(Instant.now());

        //1、通过RFID读取托盘编号
        UaClient uaClient = opcUaClientTemplate.getUaClientList().get(0);
        NodeId rfidPallet = new NodeId(2, "BekoOpcua.BottomPlate.rfidPallet");
        Variant readNodeVariant;
        try {
            readNodeVariant = opcUaClientTemplate.readNodeVariant(uaClient, rfidPallet);
            palletNo = (String) readNodeVariant.getValue();
        } catch (OpcUaClientException e) {
            e.printStackTrace();
            return;
        }

        //2、查询当前工单和下一工单信息
        Map<String, Order> currentOrderAndNextOrder = orderService.getCurrentOrderAndNextOrder();
        Order currentOrder = currentOrderAndNextOrder.get("currentOrder");
        Order nextOrder = currentOrderAndNextOrder.get("nextOrder");

        //3、前工单和下一工单信息异步推送到前台，以及异步查询工艺信息并推送到前台
        taskExecutor.execute(() -> queryOperationsAndPush(currentOrder, nextOrder));

        //4、调用扫描枪，读取下底盘条码
        String barcode = getBarcode.getBarcode();
        if (barcode == null) return;

        //5、调用beko http API查询下底盘的信息
        boolean matchResult = httpTemplate.getForObject(httpTemplate.getBekoApiHttpSchemeHierarchical()
                + MessageFormat.format(BOTTOM_PLATE_CHECKER_RESULT, barcode), Boolean.class);
        if (matchResult) {
            ////////////////////等与客户确认好下底盘条码信息API之后编码/////////////////////
        }
        //6、比较下底盘是否为当前工单的物料
        if ("1".equals(matchResult)) {
//        if (bottomPlaceInfo.contains(currentOrder.getOrderNo())) {
            //将下底盘匹配通过的信息推送到一体机
            Greeting greeting = new Greeting(barcode);
            template.convertAndSend("/topic/station01/barcodeRight", greeting);

            //如果订单状态为0，则改为1，并将订单开始时间存入数据库中
            if ("0".equals(currentOrder.getStatus())) {
                //将订单状态改为1，并将订单开始时间存入数据库中
                orderService.updateOrderStatusByOrderNo(currentOrder.getOrderNo(), "1", Timestamp.from(Instant.now()));
            }

            //7、将当前工单编号、当前产品类型和下一工单编号绑定到托盘中并放入到缓存
            if (!redisTemplate.hasKey(palletNo)) {
                HashOperations<String, String, String> cacheDatas = redisTemplate.opsForHash();
                String currentOrderNo = currentOrder.getOrderNo();
                cacheDatas.put(palletNo, "currentOrderNo", currentOrderNo);  //当前订单编号
                cacheDatas.put(palletNo, "currentProductNo", currentOrder.getProductNo()); //当前产品类型编号
                cacheDatas.put(palletNo, "nextOrderNo", nextOrder.getOrderNo()); //下一订单编号

                //8、将下底盘条码绑定到托盘放入缓存并保存到数据库中
                cacheDatas.put(palletNo, "bottomPlateBarcode", barcode);  //下底盘条码
                palletService.palletBindingProInfo(palletNo, currentOrderNo, currentOrder.getProductNo(), barcode);

                //9、初始化redis中工单在每个工位中的数量，先查询有没有，有则不初始化，无则初始化
                if (!redisTemplate.hasKey(currentOrderNo)) {
                    cacheDatas.put(currentOrderNo, "bottomPlate", "0");
                    cacheDatas.put(currentOrderNo, "topPlate", "0");
                    //所有需要显示当前工单在当前工位生产的数量的地方
                    //
                    //
                }

                //10、将到位时间点保存到数据库中
                StationCycleTime stationCycleTime = new StationCycleTime();
                stationCycleTime.setBottomPlaceCode(barcode);
                stationCycleTime.setOrderNo(currentOrderNo);
                stationCycleTime.setProductNo(currentOrder.getProductNo());
                stationCycleTime.setBottomPlateStationStart(arrivedTime);
                stationCycleTimeService.save(stationCycleTime);

                //11、将订单的上线数量加1
                orderService.updateOnlineNumByOrderNo(currentOrderNo);
            }
        } else {
            //如果下底盘不是当前工单的物料，则推送错料信息到前台
            Greeting greeting = new Greeting("下底盘与当前工单不匹配");
            template.convertAndSend("/topic/station01/wrongMaterial", greeting);
        }
    }

    /**
     * 下底盘工位放行时的业务逻辑
     */
    private void doThingWhenLeave () {
        //记录放行的时间点
        Timestamp leaveTime = Timestamp.from(Instant.now());
        //将下底盘工位的产品数量加1
        HashOperations<String, String, String> cacheDatas = redisTemplate.opsForHash();
        String orderNo = cacheDatas.get(palletNo, "currentOrderNo");
        String count = cacheDatas.get(orderNo, "station01");
        count = Integer.parseInt(count) + 1 + "";
        cacheDatas.put(orderNo, "bottomPlate", count);
        String bottomPlateBarcode = cacheDatas.get(palletNo, "bottomPlateBarcode");

        //将放行时间点记录到数据库中
        stationCycleTimeService.updateBottomPlateStationCycleTimeByBarcode(leaveTime, bottomPlateBarcode);
        //异步将当前工位完成数量和平均时间推送到前台
        Map<String, String> countAndAverageTime = new HashMap<>();
        countAndAverageTime.put("currentFinished", count);
        //计算当前工单在下底盘工位的平均时间
        String averageTime = stationCycleTimeService.getBottomPlateAverageTime(orderNo);
        countAndAverageTime.put("averageTime", averageTime);
        template.convertAndSend("/topic/station01/countAndAverageTime", countAndAverageTime);
    }

    /**
     * 异步推送工艺信息到工位1显示
     */
    @Async("taskExecutor")
    private void queryOperationsAndPush (Order currentOrder, Order nextOrder) {
        //根据工位名获取工位信息
        Workstation workstation = workstationService.getWorkstationByStationId("station01");
        List<Operation> operationList = operationService.getOperationByProductNoAndWorkstationId(currentOrder.getProductNo(), workstation.getId());
        template.convertAndSend("/topic/station01/operation", operationList);
        //推送当前工单和下一工单信息
        template.convertAndSend("/topic/station01/currentOrder", currentOrder);
        template.convertAndSend("/topic/station01/nextOrder", nextOrder);
    }
}