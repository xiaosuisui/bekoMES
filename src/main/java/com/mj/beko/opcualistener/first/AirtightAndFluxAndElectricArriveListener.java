package com.mj.beko.opcualistener.first;

import com.mj.beko.domain.MesToFlowTestRange;
import com.mj.beko.domain.Order;
import com.mj.beko.domain.Pallet;
import com.mj.beko.httpclient.HttpTemplate;
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
import org.opcfoundation.ua.builtintypes.UnsignedByte;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import javax.inject.Inject;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.List;

/**
 * @author wanghb
 * 气密测试、流量测试和电测试的到位信号监听
 */
@Component
public class AirtightAndFluxAndElectricArriveListener implements MonitoredDataItemListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AirtightAndFluxAndElectricArriveListener.class);

    private static final String LEAKAGE_TEST1_RFID = "\"information\".\"Station_airtight1_ID\"";
    private static final String LEAKAGE_TEST1_EMPTY = "\"information\".\"Station_airtight1_empty\"";

    private static final String LEAKAGE_TEST2_RFID = "\"information\".\"Station_airtight2_ID\"";
    private static final String LEAKAGE_TEST2_EMPTY = "\"information\".\"Station_airtight2_empty\"";

    private static final String FLOW_TEST1_RFID = "\"information\".\"Station_flux1_ID\"";
    private static final String FLOW_TEST1_EMPTY = "\"information\".\"Station_flux1_empty\"";

    private static final String FLOW_TEST2_RFID = "\"information\".\"Station_flux2_ID\"";
    private static final String FLOW_TEST2_EMPTY = "\"information\".\"Station_flux2_empty\"";

    private static final String ELECTRIC_TEST_RFID = "\"information\".\"Station_electric_ID\"";
    private static final String ELECTRIC_TEST_EMPTY = "\"information\".\"Station_electric_empty\"";

    @Inject
    private OpcUaClientTemplate opcUaClientTemplate;

    @Inject
    private RedisTemplate<String, Object> redisTemplate;

    @Inject
    private StationCycleTimeService stationCycleTimeService;

    @Inject
    private TestStationDataService testStationDataService;

    @Inject
    private PalletService palletService;

    @Inject
    private OrderService orderService;

    @Inject
    private AsynPushDataToScreen asynPushDataToScreen;

    @Inject
    private MesToFlowTestService mesToFlowTestService;

    @Inject
    private ProductRepairService productRepairService;

    @Inject
    private HttpTemplate httpTemplate;

    private static String SEND_BARCODE_TO_CCD = "/api/getFileNameFromMes?content={0}";

    @Override
    public void onDataChange(MonitoredDataItem monitoredDataItem, DataValue oldValue, DataValue newValue) {
        LOGGER.info("进入监听器，变化前的值为：{}，变化后的值为：{}", new Object[]{oldValue, newValue});
        if (!OpcUaUtil.isNewNodeValueValid("PLC1", monitoredDataItem.getNodeId(), oldValue, newValue)) {
            return;
        }
        String palletNo = ((UnsignedByte[])newValue.getValue().getValue())[0].intValue() + "";
        if ("0".equals(palletNo)) return;
        int screwRes = ((UnsignedByte[])newValue.getValue().getValue())[1].intValue();
        String node = monitoredDataItem.getNodeId().getValue().toString();
        switch (node) {
            case LEAKAGE_TEST1_RFID:
                doThingWhenArrive(palletNo, LEAKAGE_TEST1_EMPTY, "LeakageTest", screwRes);
                break;
            case LEAKAGE_TEST2_RFID:
                doThingWhenArrive(palletNo, LEAKAGE_TEST2_EMPTY, "LeakageTest", screwRes);
                break;
            case FLOW_TEST1_RFID:
                doThingWhenArrive(palletNo, FLOW_TEST1_EMPTY, "FlowTest", screwRes);
                break;
            case FLOW_TEST2_RFID:
                doThingWhenArrive(palletNo, FLOW_TEST2_EMPTY, "FlowTest", screwRes);
                break;
            case ELECTRIC_TEST_RFID:
                doThingWhenArrive(palletNo, ELECTRIC_TEST_EMPTY, "ElectricTest", screwRes);
                break;
            default:
                System.out.println("***************ERROR****************");
                break;
        }
    }

    private void doThingWhenArrive(String palletNo, String empNode, String stationName, int screwRes){
        //1、记录到位时间点
        Timestamp arrivedTime = Timestamp.from(Instant.now());

        //2、如果是空托盘什么都不做
        if (!redisTemplate.hasKey(palletNo)) return;

        HashOperations<String, String, String> cacheDatas = redisTemplate.opsForHash();
        String bottomPlaceCode = cacheDatas.get(palletNo, "bottomPlateBarcode");
        String currentOrderNo = cacheDatas.get(palletNo, "currentOrderNo");

        //3、如果缓存中有数据，读取plc空托盘节点，如果为空托盘即表示返修未修好直接下线的产品，
        // 则坏件数量加1，并清除缓存和绑定的数据；否则将到位时间点保存到数据库中
        UaClient uaClient = opcUaClientTemplate.getUaClientList().get(0);
//        NodeId emptyNode = new NodeId(3, empNode);
//        boolean emptyOrNot = true;
//        try {
//            emptyOrNot = opcUaClientTemplate.readNodeVariant(uaClient, emptyNode).booleanValue();
//        } catch (OpcUaClientException e) {
//            e.printStackTrace();
//        }
//        if (!emptyOrNot) {
//            try {
//                switch (empNode) {
//                    case LEAKAGE_TEST1_EMPTY:
//                        NodeId air1 = new NodeId(3, "\"Test\".\"L1\"");
//                        opcUaClientTemplate.writeNodeValue(uaClient, air1, 1);
//                        break;
//                    case LEAKAGE_TEST2_EMPTY:
//                        NodeId air2 = new NodeId(3, "\"Test\".\"L2\"");
//                        opcUaClientTemplate.writeNodeValue(uaClient, air2, 1);
//                        break;
//                    case FLOW_TEST1_EMPTY:
//                        NodeId flow1 = new NodeId(3, "\"Test\".\"F1\"");
//                        opcUaClientTemplate.writeNodeValue(uaClient, flow1, 1);
//                        break;
//                    case FLOW_TEST2_EMPTY:
//                        NodeId flow2 = new NodeId(3, "\"Test\".\"F2\"");
//                        opcUaClientTemplate.writeNodeValue(uaClient, flow2, 1);
//                        break;
//                    case ELECTRIC_TEST_EMPTY:
//                        NodeId ele = new NodeId(3, "\"Test\".\"E\"");
//                        opcUaClientTemplate.writeNodeValue(uaClient, ele, 1);
//                        break;
//                }
//            } catch (OpcUaClientException e) {
//                e.printStackTrace();
//            }
//            //根据orderNo修改坏件数量
//            orderService.updateBrokenNumByOrderNo(currentOrderNo);
//            //删除缓存数据
//            redisTemplate.delete(palletNo);
//            //清除托盘绑定的数据
//            palletService.clearPalletData(palletNo);
//            return;
//        }
        //先判断有没有进过返修，如果有，则进行逻辑判断前一个测试工位的检测结果；
        //如果没有，则当打螺丝存在不合格时不进行测试工位测试并不记录到位时间
        int repairCount = productRepairService.getCountByBarvodeAndState(bottomPlaceCode, "repair01");
//        int screwNokCount = 0;
//        if (repairCount <= 0 && screwRes == 1) {
//            screwNokCount = testStationDataService.getScrewsNokCountByBottomPlaceCode(bottomPlaceCode);
//        }
//        if (screwNokCount > 0) return;
        if (repairCount <= 0 && screwRes == 1) return;
        switch (stationName) {
            case "LeakageTest":
                stationCycleTimeService.updateAirtightStationArriveTimeByBarcode(arrivedTime, bottomPlaceCode);
                break;
            case "FlowTest":
                //根据下底盘条码获取最近一次的气密检测结果为"NOK"的数量
                int count = testStationDataService.getAirtightNokCountByBottomPlaceCode(bottomPlaceCode);
                //如果count大于0，则不记录到位时间点
                if (count > 0) return;
                //将流量范围值写给PLC：根据产品类型查询流量范围值，处理后写给plc
                String productNo = cacheDatas.get(palletNo, "currentProductNo");
                MesToFlowTestRange flowStepRangeValue = mesToFlowTestService.getFlowStepRangeValue(productNo);
                Float[] data = handlerFlowData(flowStepRangeValue);
                NodeId flowRangeNode = new NodeId(3, "\"information\".\"Flow_Range\"");
                try {
                    boolean flag = opcUaClientTemplate.writeNodeValue(uaClient, flowRangeNode, data);
                } catch (OpcUaClientException e) {
                    e.printStackTrace();
                }
                stationCycleTimeService.updateFluxStationArriveTimeByBarcode(arrivedTime, bottomPlaceCode);
                break;
            case "ElectricTest":
                //通过托盘号查找productId 和下底盘条码
                Pallet pallet=palletService.findPalletByPalletNo(palletNo);
                //组装写给CCD的值
                String ccdFileName=pallet.getProductNo()+pallet.getBottomPlaceCode();
                //将下底盘条码写给CCD,
                ResponseEntity<String> result = httpTemplate.getForEntity("http://10.114.21.180:8088"
                        + MessageFormat.format(SEND_BARCODE_TO_CCD, ccdFileName), String.class);
                System.out.println("------------------写给CCD成功：" + result.getBody().toString());
                //先判断最近一次气密检测结果是否OK，如果不OK，则不进行电测试；
                //如果OK，则判断最新一次流量检测结果。如果最新一次流量检测结果不OK，则不进行电测试
                int airtightNokCount = testStationDataService.getAirtightNokCountByBottomPlaceCode(bottomPlaceCode);
                if (airtightNokCount > 0) return;
                int fluxNokCount = testStationDataService.getFluxNokCountByBottomPlaceCode(bottomPlaceCode);
                if (fluxNokCount > 0) return;
                stationCycleTimeService.updateElectricStationArriveTimeByBarcode(arrivedTime, bottomPlaceCode);
                //根据订单号查询当前工单和下一工单信息
                String nextOrderNo = cacheDatas.get(palletNo, "nextOrderNo");
                List<Order> orders = orderService.getOrderListByOrderNos(currentOrderNo, nextOrderNo);
                if (orders == null || orders.size() <= 0) return;
                Order currentOrder = orders.get(0);
                Order nextOrder = orders.size() == 2 ? orders.get(1) : null;
                //异步推送当前工单和下一工单到电测试工位
                asynPushDataToScreen.pushCurrentAndNextOrder(currentOrder, nextOrder, stationName);
                break;
            default:
                System.out.println("***************ERROR****************");
                break;
        }
    }

    private Float[] handlerFlowData(MesToFlowTestRange flowStepRangeValue){
        String[] step1 = flowStepRangeValue.getStep01Rnage().split("_");
        String[] step2 = flowStepRangeValue.getStep02Range().split("_");
        String[] step3 = flowStepRangeValue.getStep03Range().split("_");
        String[] step4 = flowStepRangeValue.getStep04Range().split("_");
        float step1Min = Float.parseFloat(step1[0]);
        float step1Max = Float.parseFloat(step1[1]);
        float step2Min = Float.parseFloat(step2[0]);
        float step2Max = Float.parseFloat(step2[1]);
        float step3Min = Float.parseFloat(step3[0]);
        float step3Max = Float.parseFloat(step3[1]);
        float step4Min = Float.parseFloat(step4[0]);
        float step4Max = Float.parseFloat(step4[1]);
        Float[] data = {step1Min, step1Max, step2Min, step2Max, step3Min, step3Max, step4Min, step4Max};
        return data;
    }
}