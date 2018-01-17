package com.mj.beko.opcualistener.second;

import com.mj.beko.domain.MesToFlowTestRange;
import com.mj.beko.domain.Order;
import com.mj.beko.opcua.OpcUaClientException;
import com.mj.beko.opcua.OpcUaClientTemplate;
import com.mj.beko.opcua.OpcUaUtil;
import com.mj.beko.opcualistener.AsynPushDataToScreen;
import com.mj.beko.service.*;
import com.prosysopc.ua.client.MonitoredDataItem;
import com.prosysopc.ua.client.MonitoredDataItemListener;
import com.prosysopc.ua.client.UaClient;
import org.opcfoundation.ua.builtintypes.DataValue;
import org.opcfoundation.ua.builtintypes.NodeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import javax.inject.Inject;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * @author wanghb
 * 二段7个工位的到位信号
 */
@Component
public class BurnerSupportAndCapArriveListener implements MonitoredDataItemListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(BurnerSupportAndCapArriveListener.class);

    private static final String BURNER_SUPPORT1_ARRIVE = "\"OPCOA\".\"1_Robot_PalletCode\"";
    private static final String BURNER_SUPPORT2_ARRIVE = "\"OPCOA\".\"2_Robot_PalletCode\"";
    private static final String BURNER_SUPPORT3_ARRIVE = "\"OPCOA\".\"3_Robot_PalletCode\"";
    private static final String BURNER_SUPPORT4_ARRIVE = "\"OPCOA\".\"4_Robot_PalletCode\"";

    private static final String KNOBS_RFID = "\"OPCOA\".\"KnobStation_PalletCode\"";

    private static final String BURNER_CAP1_ARRIVE = "\"OPCOA\".\"5_Robot_PalletCode\"";
    private static final String BURNER_CAP2_ARRIVE = "\"OPCOA\".\"6_Robot_PalletCode\"";

    private static final String BURNER_SUPPORT1_EMPTY = "\"OPCOA\".\"1BSProductionOn\"";
    private static final String BURNER_SUPPORT2_EMPTY = "\"OPCOA\".\"2BSProductionOn\"";
    private static final String BURNER_SUPPORT3_EMPTY = "\"OPCOA\".\"3BSProductionOn\"";
    private static final String BURNER_SUPPORT4_EMPTY = "\"OPCOA\".\"4BSProductionOn\"";

    private static final String BURNER_CAP1_EMPTY = "\"OPCOA\".\"1BCProductionOn\"";
    private static final String BURNER_CAP2_EMPTY = "\"OPCOA\".\"2BCProductionOn\"";

    @Inject
    private OpcUaClientTemplate opcUaClientTemplate;

    @Inject
    private RedisTemplate<String, Object> redisTemplate;

    @Inject
    private SimpMessagingTemplate template;

    @Inject
    private AutomaticStationTimesService automaticStationTimesService;

    @Inject
    private WorkstationService workstationService;

    @Inject
    private OperationService operationService;

    @Inject
    private OrderService orderService;

    @Inject
    private StationCycleTimeService stationCycleTimeService;

    @Inject
    private AsynPushDataToScreen asynPushDataToScreen;

    @Inject
    private PalletService palletService;

    @Inject
    private MesToFlowTestService mesToFlowTestService;

    @Override
    public void onDataChange(MonitoredDataItem monitoredDataItem, DataValue oldValue, DataValue newValue) {
        LOGGER.info("进入监听器，变化前的值为：{}，变化后的值为：{}", new Object[]{oldValue, newValue});
        if (!OpcUaUtil.isNewNodeValueValid("PLC2", monitoredDataItem.getNodeId(), oldValue, newValue)
                || newValue.getValue().intValue() == 0) {
            return;
        }
        String node = monitoredDataItem.getNodeId().getValue().toString();
        String palletNo = newValue.getValue().intValue() + "";
        switch (node) {
            case BURNER_SUPPORT1_ARRIVE:
                doThingWhenArrive(BURNER_SUPPORT1_EMPTY, "BurnerSupport1", palletNo);
                break;
            case BURNER_SUPPORT2_ARRIVE:
                doThingWhenArrive(BURNER_SUPPORT2_EMPTY, "BurnerSupport2", palletNo);
                break;
            case BURNER_SUPPORT3_ARRIVE:
                doThingWhenArrive(BURNER_SUPPORT3_EMPTY, "BurnerSupport3", palletNo);
                break;
            case BURNER_SUPPORT4_ARRIVE:
                doThingWhenArrive(BURNER_SUPPORT4_EMPTY, "BurnerSupport4", palletNo);
                break;
            case KNOBS_RFID:
                doThingWhenKnobArrive(palletNo, "Knobs");
                break;
            case BURNER_CAP1_ARRIVE:
                doThingWhenArrive(BURNER_CAP1_EMPTY, "BurnerCap1", palletNo);
                break;
            case BURNER_CAP2_ARRIVE:
                doThingWhenArrive(BURNER_CAP2_EMPTY, "BurnerCap2", palletNo);
                break;
            default:
                System.out.println("**********节点错误**********");
                break;
        }
    }

    private void doThingWhenArrive(String empNode, String stationName, String palletNo){
        //如果是空托盘什么都不做
        if (!redisTemplate.hasKey(palletNo)) return;
        HashOperations<String, String, String> cacheDatas = redisTemplate.opsForHash();
        String bottomPlaceCode = cacheDatas.get(palletNo, "bottomPlateBarcode");
        String currentOrderNo = cacheDatas.get(palletNo, "currentOrderNo");
        String currentProductNo = cacheDatas.get(palletNo, "currentProductNo");

        //如果缓存中有数据，读取plc空托盘节点，如果为空托盘即表示返修未修好直接下线的产品，
        // 则坏件数量加1，并清除缓存和绑定的数据；否则将产品类型写给plc，并将每个工位的生产数量加1
        UaClient uaClient = opcUaClientTemplate.getUaClientList().get(1);
        NodeId emptyNode = new NodeId(3, empNode);
        boolean emptyOrNot = false;
        try {
            emptyOrNot = opcUaClientTemplate.readNodeVariant(uaClient, emptyNode).booleanValue();
        } catch (OpcUaClientException e) {
            e.printStackTrace();
        }
        if (!emptyOrNot) {
            try {
                switch (stationName){
                    case "BurnerSupport1":
                        NodeId burnerSupport1 = new NodeId(3, "\"Test\".\"1\"");
                        opcUaClientTemplate.writeNodeValue(uaClient, burnerSupport1, 1);
                        break;
                    case "BurnerSupport2":
                        NodeId burnerSupport2 = new NodeId(3, "\"Test\".\"2\"");
                        opcUaClientTemplate.writeNodeValue(uaClient, burnerSupport2, 1);
                        break;
                    case "BurnerSupport3":
                        NodeId burnerSupport3 = new NodeId(3, "\"Test\".\"3\"");
                        opcUaClientTemplate.writeNodeValue(uaClient, burnerSupport3, 1);
                        break;
                    case "BurnerSupport4":
                        NodeId burnerSupport4 = new NodeId(3, "\"Test\".\"4\"");
                        opcUaClientTemplate.writeNodeValue(uaClient, burnerSupport4, 1);
                        break;
                    case "Knobs":
                        NodeId knobs = new NodeId(3, "\"Test\".\"knob\"");
                        opcUaClientTemplate.writeNodeValue(uaClient, knobs, 1);
                        break;
                    case "BurnerCap1":
                        NodeId burnerCap1 = new NodeId(3, "\"Test\".\"5\"");
                        opcUaClientTemplate.writeNodeValue(uaClient, burnerCap1, 1);
                        break;
                    case "BurnerCap2":
                        NodeId burnerCap2 = new NodeId(3, "\"Test\".\"6\"");
                        opcUaClientTemplate.writeNodeValue(uaClient, burnerCap2, 1);
                        break;
                }
            } catch (OpcUaClientException e) {
                e.printStackTrace();
            }

//            //根据orderNo修改坏件数量
//            orderService.updateBrokenNumByOrderNo(currentOrderNo);
//            //删除缓存数据
//            redisTemplate.delete(palletNo);
//            //清除托盘绑定的数据
//            palletService.clearPalletData(palletNo);
//            return;
        }

        MesToFlowTestRange flowStepRangeValue = mesToFlowTestService.getFlowStepRangeValue(currentProductNo);
        String[] plc2Robot = flowStepRangeValue.getPlc2Robot().split("_");
        int proType = Integer.parseInt(plc2Robot[0]);
        int material;
        NodeId proTypeNode;
        NodeId materialNode;
        ValueOperations dataOptions = redisTemplate.opsForValue();
        try {
            switch (stationName) {
                case "BurnerSupport1":
                    proTypeNode = new NodeId(3, "\"OPCOA_1\".\"Production_Model_Robot_1\"");
                    materialNode = new NodeId(3, "\"OPCOA_1\".\"Material_Model_Robot_1\"");
                    material = Integer.parseInt(plc2Robot[1]);
                    if (material == 0) return;
                    opcUaClientTemplate.writeNodeValue(uaClient, proTypeNode, proType);
                    opcUaClientTemplate.writeNodeValue(uaClient, materialNode, material);
                    if (redisTemplate.hasKey("BurnerSupport1")) {
                        List<Map<String, String>> burnerSupport1List = (List<Map<String, String>>)dataOptions.get("BurnerSupport1");
                        for (Map<String, String> map : burnerSupport1List) {
                            if (map.containsValue(currentOrderNo)){
                                int number = Integer.parseInt(map.get("number")) + 1;
                                map.put("number", String.valueOf(number));
                                break;
                            }
                        }
                        dataOptions.set("BurnerSupport1", burnerSupport1List);
                    }
                    break;
                case "BurnerSupport2":
                    proTypeNode = new NodeId(3, "\"OPCOA_1\".\"Production_Model_Robot_2\"");
                    materialNode = new NodeId(3, "\"OPCOA_1\".\"Material_Model_Robot_2\"");
                    material = Integer.parseInt(plc2Robot[2]);
                    if (material == 0) return;
                    opcUaClientTemplate.writeNodeValue(uaClient, proTypeNode, proType);
                    opcUaClientTemplate.writeNodeValue(uaClient, materialNode, material);
                    if (redisTemplate.hasKey("BurnerSupport2")) {
                        List<Map<String, String>> burnerSupport2List = (List<Map<String, String>>)dataOptions.get("BurnerSupport2");
                        for (Map<String, String> map : burnerSupport2List) {
                            if (map.containsValue(currentOrderNo)){
                                int number = Integer.parseInt(map.get("number")) + 1;
                                map.put("number", String.valueOf(number));
                                break;
                            }
                        }
                        dataOptions.set("BurnerSupport2", burnerSupport2List);
                    }
                    break;
                case "BurnerSupport3":
                    proTypeNode = new NodeId(3, "\"OPCOA_1\".\"Production_Model_Robot_3\"");
                    materialNode = new NodeId(3, "\"OPCOA_1\".\"Material_Model_Robot_3\"");
                    material = Integer.parseInt(plc2Robot[2]);
                    if (material == 0) return;
                    opcUaClientTemplate.writeNodeValue(uaClient, proTypeNode, proType);
                    opcUaClientTemplate.writeNodeValue(uaClient, materialNode, material);
                    if (redisTemplate.hasKey("BurnerSupport3")) {
                        List<Map<String, String>> burnerSupport3List = (List<Map<String, String>>)dataOptions.get("BurnerSupport3");
                        for (Map<String, String> map : burnerSupport3List) {
                            if (map.containsValue(currentOrderNo)){
                                int number = Integer.parseInt(map.get("number")) + 1;
                                map.put("number", String.valueOf(number));
                                break;
                            }
                        }
                        dataOptions.set("BurnerSupport3", burnerSupport3List);
                    }
                    break;
                case "BurnerSupport4":
                    proTypeNode = new NodeId(3, "\"OPCOA_1\".\"Production_Model_Robot_4\"");
                    materialNode = new NodeId(3, "\"OPCOA_1\".\"Material_Model_Robot_4\"");
                    material = Integer.parseInt(plc2Robot[3]);
                    if (material == 0) return;
                    opcUaClientTemplate.writeNodeValue(uaClient, proTypeNode, proType);
                    opcUaClientTemplate.writeNodeValue(uaClient, materialNode, material);
                    if (redisTemplate.hasKey("BurnerSupport4")) {
                        List<Map<String, String>> burnerSupport4List = (List<Map<String, String>>)dataOptions.get("BurnerSupport4");
                        for (Map<String, String> map : burnerSupport4List) {
                            if (map.containsValue(currentOrderNo)){
                                int number = Integer.parseInt(map.get("number")) + 1;
                                map.put("number", String.valueOf(number));
                                break;
                            }
                        }
                        dataOptions.set("BurnerSupport4", burnerSupport4List);
                    }
                    break;
                case "BurnerCap1":
                    proTypeNode = new NodeId(3, "\"OPCOA_1\".\"Production_Model_Robot_5\"");
                    materialNode = new NodeId(3, "\"OPCOA_1\".\"Material_Model_Robot_5\"");
                    material = Integer.parseInt(plc2Robot[4]);
                    if (material == 0) return;
                    opcUaClientTemplate.writeNodeValue(uaClient, proTypeNode, proType);
                    opcUaClientTemplate.writeNodeValue(uaClient, materialNode, material);
                    if (redisTemplate.hasKey("BurnerCap1")) {
                        List<Map<String, String>> burnerCap1List = (List<Map<String, String>>)dataOptions.get("BurnerCap1");
                        for (Map<String, String> map : burnerCap1List) {
                            if (map.containsValue(currentOrderNo)){
                                int number = Integer.parseInt(map.get("number")) + 1;
                                map.put("number", String.valueOf(number));
                                break;
                            }
                        }
                        dataOptions.set("BurnerCap1", burnerCap1List);
                    }
                    break;
                case "BurnerCap2":
                    proTypeNode = new NodeId(3, "\"OPCOA_1\".\"Production_Model_Robot_6\"");
                    materialNode = new NodeId(3, "\"OPCOA_1\".\"Material_Model_Robot_6\"");
                    material = Integer.parseInt(plc2Robot[5]);
                    if (material == 0) return;
                    opcUaClientTemplate.writeNodeValue(uaClient, proTypeNode, proType);
                    opcUaClientTemplate.writeNodeValue(uaClient, materialNode, material);
                    if (redisTemplate.hasKey("BurnerCap2")) {
                        List<Map<String, String>> burnerCap2List = (List<Map<String, String>>)dataOptions.get("BurnerCap2");
                        for (Map<String, String> map : burnerCap2List) {
                            if (map.containsValue(currentOrderNo)){
                                int number = Integer.parseInt(map.get("number")) + 1;
                                map.put("number", String.valueOf(number));
                                break;
                            }
                        }
                        dataOptions.set("BurnerCap2", burnerCap2List);
                    }
                    break;
                default:
                    break;
            }
        } catch (OpcUaClientException e) {
            e.printStackTrace();
        }

//        //4、如果不是空托盘，则记录到位时间点
//        if (emptyOrNot) {
//            AutomaticStationTimes automaticStationTimes = new AutomaticStationTimes();
//            automaticStationTimes.setStationName(stationName);
//            automaticStationTimes.setArriveTime(arrivedTime);
//            automaticStationTimesService.save(automaticStationTimes);
//        }
    }

    private void doThingWhenKnobArrive(String palletNo, String stationName){
        //1、记录到位时间点
        Timestamp arrivedTime = Timestamp.from(Instant.now());

        //2、如果是空托盘什么都不做
        if (!redisTemplate.hasKey(palletNo)) return;

        //3、读异步推送工单、工艺信息并保存到位时间节点
        HashOperations<String, String, String> cacheDatas = redisTemplate.opsForHash();
        String currentOrderNo = cacheDatas.get(palletNo, "currentOrderNo");
        String nextOrderNo = cacheDatas.get(palletNo, "nextOrderNo");
        String bottomPlateBarcode = cacheDatas.get(palletNo, "bottomPlateBarcode");
        //记录到位时间点
        stationCycleTimeService.updateKnobStationArriveTimeByBarcode(arrivedTime, bottomPlateBarcode);
        //根据订单号查询当前工单和下一工单信息
        List<Order> orders = orderService.getOrderListByOrderNos(currentOrderNo, nextOrderNo);
        if (orders == null || orders.size() <= 0) return;
        Order currentOrder = orders.get(0);
        Order nextOrder = orders.size() == 2 ? orders.get(1) : null;
        //异步推送工艺信息到旋钮显示
        asynPushDataToScreen.queryOperationsAndPush(currentOrder, nextOrder, stationName, palletNo);
    }
}