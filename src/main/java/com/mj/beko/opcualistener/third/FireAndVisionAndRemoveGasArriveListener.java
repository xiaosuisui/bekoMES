package com.mj.beko.opcualistener.third;

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
import io.swagger.models.auth.In;
import org.opcfoundation.ua.builtintypes.DataValue;
import org.opcfoundation.ua.builtintypes.NodeId;
import org.opcfoundation.ua.builtintypes.UnsignedByte;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import javax.inject.Inject;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

/**
 * @author wanghb
 * 3段燃烧、终检和拔气到位信号监听器
 */
@Component
public class FireAndVisionAndRemoveGasArriveListener implements MonitoredDataItemListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(FireAndVisionAndRemoveGasArriveListener.class);

    private static final String FIRE_TEST1_RFID = "\"ITread\".\"ranshao_ID\"";
    private static final String FIRE_TEST2_RFID = "\"ITread\".\"ranshao2_ID\"";
    private static final String VISION_CONTROL_RFID = "\"ITread\".\"zhongjian_ID\"";
    private static final String REMOVE_ELECTRIC_GAS_RFID = "\"ITread\".\"baxian_ID\"";

    private static final String FIRE_TEST1_EMPTY = "\"ITread\".\"ranshao_empty\"";
    private static final String FIRE_TEST2_EMPTY = "\"ITread\".\"ranshao2_empty\"";
    private static final String VISION_CONTROL_EMPTY = "\"ITread\".\"zhongjian_empty\"";
    private static final String REMOVE_ELECTRIC_GAS_EMPTY = "\"ITread\".\"baxian_empty\"";

    private static final String FIRE1_RECEIVE = "\"ITread\".\"Fire1judgment_receive\"";
    private static final String FIRE2_RECEIVE = "\"ITread\".\"Fire2judgment_receive\"";

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
    private FailureReasonDataService failureReasonDataService;

    @Inject
    private PalletService palletService;

    @Inject
    private ProductRepairService productRepairService;

    @Inject
    private MesToFlowTestService mesToFlowTestService;

    @Override
    public void onDataChange(MonitoredDataItem monitoredDataItem, DataValue oldValue, DataValue newValue) {
        LOGGER.info("进入监听器，变化前的值为：{}，变化后的值为：{}", new Object[]{oldValue, newValue});
        if (!OpcUaUtil.isNewNodeValueValid("PLC3", monitoredDataItem.getNodeId(), oldValue, newValue)) {
            return;
        }
        String node = monitoredDataItem.getNodeId().getValue().toString();
        String palletNo = ((UnsignedByte[]) newValue.getValue().getValue())[0].intValue() + "";
        if ("0".equals(palletNo)) return;
        switch (node) {
            case FIRE_TEST1_RFID:
                doThingWhenArrive(FIRE_TEST1_EMPTY, "FireTest1", palletNo, FIRE1_RECEIVE);
                break;
            case FIRE_TEST2_RFID:
                doThingWhenArrive(FIRE_TEST2_EMPTY, "FireTest2", palletNo, FIRE2_RECEIVE);
                break;
            case VISION_CONTROL_RFID:
                doThingWhenArrive(VISION_CONTROL_EMPTY, "VisionControl", palletNo, null);
                break;
            case REMOVE_ELECTRIC_GAS_RFID:
                doThingWhenArrive(REMOVE_ELECTRIC_GAS_EMPTY, "RemoveElectricAndGas", palletNo, null);
                break;
            default:
                System.out.println("**********节点错误**********");
                break;
        }
    }

    private void doThingWhenArrive(String empNode, String stationName, String palletNo, String fireReceiveNode){
        //1、记录到位时间点
        Timestamp arrivedTime = Timestamp.from(Instant.now());

        //2、如果是空托盘什么都不做
        if (!redisTemplate.hasKey(palletNo)) return;
        HashOperations<String, String, String> cacheDatas = redisTemplate.opsForHash();
        String currentOrderNo = cacheDatas.get(palletNo, "currentOrderNo");
        String nextOrderNo = cacheDatas.get(palletNo, "nextOrderNo");
        String bottomPlateBarcode = cacheDatas.get(palletNo, "bottomPlateBarcode");
        boolean pushFlag = true;

        //3、如果缓存中有数据，读取plc空托盘节点，如果为空托盘即表示返修未修好直接下线的产品，
        // 则坏件数量加1，并清除缓存和绑定的数据；否则将到位时间点保存到数据库中并异步推送工单、工艺信息
        UaClient uaClient = opcUaClientTemplate.getUaClientList().get(2);
//        NodeId emptyNode = new NodeId(3, empNode);
//        boolean emptyOrNot = false;
//        try {
//            emptyOrNot = opcUaClientTemplate.readNodeVariant(uaClient, emptyNode).booleanValue();
//        } catch (OpcUaClientException e) {
//            e.printStackTrace();
//        }
//        if (emptyOrNot) {
//            //根据orderNo修改坏件数量
//            orderService.updateBrokenNumByOrderNo(currentOrderNo);
//            //删除缓存数据
//            redisTemplate.delete(palletNo);
//            //清除托盘绑定的数据
//            palletService.clearPalletData(palletNo);
//            return;
//        }

        //根据订单号查询当前工单和下一工单信息
        List<Order> orders = orderService.getOrderListByOrderNos(currentOrderNo, nextOrderNo);
        if (orders == null || orders.size() <= 0) return;
        Order currentOrder = orders.get(0);
        Order nextOrder = orders.size() == 2 ? orders.get(1) : null;
        switch(stationName){
            case "FireTest1":
            case "FireTest2":
                //将产品类型和燃气流量写给plc
                MesToFlowTestRange flowStepRangeValue = mesToFlowTestService.getFlowStepRangeValue(currentOrder.getProductNo());
                String[] proTypeAndFires = flowStepRangeValue.getFires().split("_");
                int proType = Integer.parseInt(proTypeAndFires[0]);
                int gas = Integer.parseInt(proTypeAndFires[1]);
                NodeId proTypeNode;
                NodeId gasNode;
                if ("FireTest1".equals(stationName)){
                    proTypeNode = new NodeId(3, "\"ITread\".\"Fire1_product\"");
                    gasNode = new NodeId(3, "\"ITread\".\"Fire1_gas\"");
                } else {
                    proTypeNode = new NodeId(3, "\"ITread\".\"Fire2_product\"");
                    gasNode = new NodeId(3, "\"ITread\".\"Fire2_gas\"");
                }
                try {
                    opcUaClientTemplate.writeNodeValue(uaClient, proTypeNode, proType);
                    opcUaClientTemplate.writeNodeValue(uaClient, gasNode, gas);
                } catch (OpcUaClientException e) {
                    e.printStackTrace();
                }
                //先读取返修工位是否存在该产品的数据，如果存在则进行火焰测试，
                //如果不存在则读取旋钮工位结果并写给电气来控制是否进行火焰测试
                int repairCount = productRepairService.getCountByBarvodeAndState(bottomPlateBarcode, "repair02");
                if (repairCount <= 0) {
                    int knobCount = failureReasonDataService.getCountByBottomPlateBarcodeAndStation(bottomPlateBarcode, "Knobs");
                    if (knobCount > 0) {
                        NodeId fireReceive = new NodeId(3, fireReceiveNode);
                        try {
                            boolean flag = opcUaClientTemplate.writeNodeValue(uaClient, fireReceive, 1);
                            pushFlag = false;
                        } catch (OpcUaClientException e) {
                            e.printStackTrace();
                        }
                    }
                }
                //根据下底盘条码保存火焰测试的到位时间
                stationCycleTimeService.updateFireTestStationArriveTimeByBarcode(arrivedTime, bottomPlateBarcode);
                break;
            case "VisionControl":
                //根据下底盘条码保存视觉控制的到位时间
                stationCycleTimeService.updateVisionControlStationArriveTimeByBarcode(arrivedTime, bottomPlateBarcode);
                break;
            case "RemoveElectricAndGas":
                //根据下底盘条码保存拔电和气工位的到位时间
                stationCycleTimeService.updateRemoveElectricGasArriveTimeByBarcode(arrivedTime, bottomPlateBarcode);
                break;
            default:
                System.out.println("****************ERROR****************");
                break;
        }
        //异步推送工单、工艺信息
        if (pushFlag) {
            //异步推送工艺信息到旋钮显示
            asynPushDataToScreen.queryOperationsAndPush(currentOrder, nextOrder, stationName, palletNo);
        }
    }
}