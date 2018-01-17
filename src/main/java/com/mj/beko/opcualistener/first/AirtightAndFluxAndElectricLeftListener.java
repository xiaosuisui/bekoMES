package com.mj.beko.opcualistener.first;

import com.mj.beko.domain.MesToFlowTestRange;
import com.mj.beko.domain.TestStationData;
import com.mj.beko.opcua.OpcUaClientException;
import com.mj.beko.opcua.OpcUaClientTemplate;
import com.mj.beko.opcua.OpcUaUtil;
import com.mj.beko.service.MesToFlowTestService;
import com.mj.beko.service.ProductRepairService;
import com.mj.beko.service.StationCycleTimeService;
import com.mj.beko.service.TestStationDataService;
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
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import javax.inject.Inject;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wanghb
 * 气密测试、流量测试和电测试放行信号监听
 */
@Component
@Slf4j
public class AirtightAndFluxAndElectricLeftListener implements MonitoredDataItemListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AirtightAndFluxAndElectricLeftListener.class);

    private static final String LEAKAGE_TEST1_RFID = "\"information\".\"Station_airtight1_ID\"";
    private static final String LEAKAGE_TEST1_LEAVE = "\"information\".\"Station_airtight1_leave\"";

    private static final String LEAKAGE_TEST2_RFID = "\"information\".\"Station_airtight2_ID\"";
    private static final String LEAKAGE_TEST2_LEAVE = "\"information\".\"Station_airtight2_leave\"";

    private static final String FLOW_TEST1_RFID = "\"information\".\"Station_flux1_ID\"";
    private static final String FLOW_TEST1_LEAVE = "\"information\".\"Station_flux1_leave\"";

    private static final String FLOW_TEST2_RFID = "\"information\".\"Station_flux2_ID\"";
    private static final String FLOW_TEST2_LEAVE = "\"information\".\"Station_flux2_leave\"";

    private static final String ELECTRIC_TEST_RFID = "\"information\".\"Station_electric_ID\"";
    private static final String ELECTRIC_TEST_LEAVE = "\"information\".\"Station_electric_leave\"";
    //定义读到数据后反馈给你plc的节点
    //leakage01
    private static final String RESPONSE_LEAKAGE01 = "\"MESHaberlesmesi\".\"Sızdırmazlık1VeriAlındı\"";
    //leakage02
    private static final String RESPONSE_LEAKAGE02 = "\"MESHaberlesmesi\".\"Sızdırmazlık2VeriAlındı\"";
    private static final String RESPONSE_Flow01 = "\"MESHaberlesmesi\".\"Debi1VeriAlındı\"";
    private static final String RESPONSE_Flow02 = "\"MESHaberlesmesi\".\"Debi2VeriAlındı\"";
    private static final String RESPONSE_ELECTRIC = "\"MESHaberlesmesi\".\"ElektrikVeriAlındı\"";

    private static final List<String> LEAKAGE_TEST1_LIST = new ArrayList<>();
    private static final List<String> LEAKAGE_TEST2_LIST = new ArrayList<>();
    private static final List<String> FLOW_TEST1_LIST = new ArrayList<>();
    private static final List<String> FLOW_TEST2_LIST = new ArrayList<>();
    private static final List<String> ELECTRIC_TEST_LIST = new ArrayList<>();

    static {
        LEAKAGE_TEST1_LIST.add("\"information\".\"Station_airtight1_test1\"");
        LEAKAGE_TEST1_LIST.add("\"information\".\"Station_airtight1_test2\"");
        LEAKAGE_TEST1_LIST.add("\"information\".\"Station_airtight1_test3\"");

        LEAKAGE_TEST2_LIST.add("\"information\".\"Station_airtight2_test1\"");
        LEAKAGE_TEST2_LIST.add("\"information\".\"Station_airtight2_test2\"");
        LEAKAGE_TEST2_LIST.add("\"information\".\"Station_airtight2_test3\"");

        FLOW_TEST1_LIST.add("\"information\".\"Station_flux1_test1\"");
        FLOW_TEST1_LIST.add("\"information\".\"Station_flux1_test2\"");
        FLOW_TEST1_LIST.add("\"information\".\"Station_flux1_test3\"");
        FLOW_TEST1_LIST.add("\"information\".\"Station_flux1_test4\"");

        FLOW_TEST2_LIST.add("\"information\".\"Station_flux2_test1\"");
        FLOW_TEST2_LIST.add("\"information\".\"Station_flux2_test2\"");
        FLOW_TEST2_LIST.add("\"information\".\"Station_flux2_test3\"");
        FLOW_TEST2_LIST.add("\"information\".\"Station_flux2_test4\"");

        ELECTRIC_TEST_LIST.add("\"information\".\"Station_electric_test1\"");
        ELECTRIC_TEST_LIST.add("\"information\".\"Station_electric_test2\"");
        ELECTRIC_TEST_LIST.add("\"information\".\"Station_electric_test3\"");
//        ELECTRIC_TEST_LIST.add("\"information\".\"Station_electric_test4\"");
    }

    @Inject
    private OpcUaClientTemplate opcUaClientTemplate;

    @Inject
    private RedisTemplate<String, Object> redisTemplate;

    @Inject
    private StationCycleTimeService stationCycleTimeService;

    @Inject
    private TestStationDataService testStationDataService;

    @Inject
    private SimpMessagingTemplate template;

    @Inject
    private MesToFlowTestService mesToFlowTestService;

    @Inject
    private ProductRepairService productRepairService;

    @Override
    public void onDataChange(MonitoredDataItem monitoredDataItem, DataValue oldValue, DataValue newValue) {
        LOGGER.info("进入监听器，变化前的值为：{}，变化后的值为：{}", new Object[]{oldValue, newValue});
        if (!OpcUaUtil.isNewNodeValueValid("PLC1", monitoredDataItem.getNodeId(), oldValue, newValue)
                || newValue.getValue().intValue() == 0) {
            log.info("new Value,the result is,{}",newValue.getValue().intValue());
            log.info("come into listener,but return");
            return;
        }
        log.info("not return data, get new Value,{}",newValue.getValue().intValue());
        String node = monitoredDataItem.getNodeId().getValue().toString();
        log.info("****************LEAVE:::" + node + "**************************");
        //放行
        switch (node) {
            case LEAKAGE_TEST1_LEAVE:
                log.info("come into leakage1 first");
                doThingWhenLeft(LEAKAGE_TEST1_RFID, LEAKAGE_TEST1_LIST, "LeakageTest1");
                break;
            case LEAKAGE_TEST2_LEAVE:
                log.info("come into leakage2 first");
                doThingWhenLeft(LEAKAGE_TEST2_RFID, LEAKAGE_TEST2_LIST, "LeakageTest2");
                break;
            case FLOW_TEST1_LEAVE:
                log.info("come into flow1 first");
                doThingWhenLeft(FLOW_TEST1_RFID, FLOW_TEST1_LIST, "FlowTest1");
                break;
            case FLOW_TEST2_LEAVE:
                log.info("come into flow2 first");
                doThingWhenLeft(FLOW_TEST2_RFID, FLOW_TEST2_LIST, "FlowTest2");
                break;
            case ELECTRIC_TEST_LEAVE:
                log.info("come into ElectricTest first");
                doThingWhenLeft(ELECTRIC_TEST_RFID, ELECTRIC_TEST_LIST, "ElectricTest");
                break;
            default:
                System.out.println("***************ERROR****************");
                break;
        }
    }

    private void doThingWhenLeft(String rfidNode, List<String> testNodeList, String stationName){
        log.info("listen leave signial,start do something,stationName is {}",stationName);
        //1、记录放行时间点
        Timestamp leaveTime = Timestamp.from(Instant.now());
        //2、获取托盘号
        UaClient uaClient = opcUaClientTemplate.getUaClientList().get(0);
        NodeId rfidPallet = new NodeId(3, rfidNode);
        String palletNo;
        int screwRes = 0;
        try {
            Variant readNodeVariant = opcUaClientTemplate.readNodeVariant(uaClient, rfidPallet);
            palletNo = ((UnsignedByte[])readNodeVariant.getValue())[0].intValue() + "";
            screwRes = ((UnsignedByte[])readNodeVariant.getValue())[1].intValue();
        } catch (OpcUaClientException e) {
            System.out.println("********************没有读到RFID信息*************************");
            e.printStackTrace();
            return;
        }
        log.info("get palletNo,now palletNo is,{}",palletNo);
        //如果读到的托盘号是0,在读3次吧
        if("0".equals(palletNo)){
            log.info("now,read palletNo again,");
            boolean flag=true;
            for(int i=0;i<3;i++){
                if(flag){
                    try {
                        Thread.sleep(500);
                        Variant readNodeVariant = opcUaClientTemplate.readNodeVariant(uaClient, rfidPallet);
                        palletNo = ((UnsignedByte[])readNodeVariant.getValue())[0].intValue() + "";
                        log.info("read palletNo again value is,time is,{} value is{}",i,palletNo);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }catch (OpcUaClientException e) {
                        log.info("********************no rfid information*************************");
                        e.printStackTrace();
                        return;
                    }
                    if(!"0".equals(palletNo)){
                        log.info("end read palletNo,value is{}",palletNo);
                        flag=false;
                    }
                }
            }
        }
        //3、如果是空托盘,直接放行
        if (!redisTemplate.hasKey(palletNo)) return;

        HashOperations<String, String, String> cacheDatas = redisTemplate.opsForHash();
        String bottomPlaceCode = cacheDatas.get(palletNo, "bottomPlateBarcode");
        String currentOrderNo = cacheDatas.get(palletNo, "currentOrderNo");
        String currentProductNo = cacheDatas.get(palletNo, "currentProductNo");
        //先判断有没有进过返修，如果有，则进行逻辑判断前一个测试工位的检测结果；
        //如果没有，则当打螺丝存在不合格时不进行测试工位测试并不记录放行时间
        log.info("get bottomPlate Code {}",bottomPlaceCode);
        int repairCount = productRepairService.getCountByBarvodeAndState(bottomPlaceCode, "repair01");
        log.info("get repair station if have record,{},",bottomPlaceCode,"result is {}",repairCount);
        log.info("read screwRes result is {}",screwRes,"Bottom plate code is {}",screwRes);
//        int screwNokCount = 0;
//        if (repairCount <= 0 && screwRes == 1) {
//            screwNokCount = testStationDataService.getScrewsNokCountByBottomPlaceCode(bottomPlaceCode);
//        }
//        if (screwNokCount > 0) return;
        if (repairCount <= 0 && screwRes == 1) return;
        switch (stationName) {
            case "LeakageTest1":
                //将气密工位放行时间点和cycleTime保存到数据库中
                stationCycleTimeService.updateAirtightStationLeaveTimeByBarcode(leaveTime, bottomPlaceCode);
                break;
            case "LeakageTest2":
                //将气密工位放行时间点和cycleTime保存到数据库中
                stationCycleTimeService.updateAirtightStationLeaveTimeByBarcode(leaveTime, bottomPlaceCode);
                break;
            case "FlowTest1":
                //根据下底盘条码获取最近一次的气密检测结果为"NOK"的数量
                log.info("come into flowTest01,time is{}",leaveTime.toString());
                int count = testStationDataService.getAirtightNokCountByBottomPlaceCode(bottomPlaceCode);
                log.info("for flow1,search leakage NOK count,result is,{}",count);
                //如果count大于0，则不读取流量检测的数据
                if (count > 0) return;
                //将流量工位放行时间点和cycleTime保存到数据库中
                stationCycleTimeService.updateFluxStationLeaveTimeByBarcode(leaveTime, bottomPlaceCode);
                break;
            case "FlowTest2":
                //根据下底盘条码获取最近一次的气密检测结果为"NOK"的数量
                log.info("come into flowTest02,time is {}",leaveTime.toString());
                int count2 = testStationDataService.getAirtightNokCountByBottomPlaceCode(bottomPlaceCode);
                log.info("for flow2,search leakage NOK count,result is,{}",count2);
                //如果count大于0，则不读取流量检测的数据
                if (count2 > 0) return;
                //将流量工位放行时间点和cycleTime保存到数据库中
                stationCycleTimeService.updateFluxStationLeaveTimeByBarcode(leaveTime, bottomPlaceCode);
                break;
            case "ElectricTest":
                //先判断最近一次气密检测结果是否OK，如果不OK，则不进行电测试；
                //如果OK，则判断最新一次流量检测结果。如果最新一次流量检测结果不OK，则不进行电测试
                log.info("come into ElectricTest,time is {}",leaveTime.toString());
                int airtightNokCount = testStationDataService.getAirtightNokCountByBottomPlaceCode(bottomPlaceCode);
                if (airtightNokCount > 0) return;
                int fluxNokCount = testStationDataService.getFluxNokCountByBottomPlaceCode(bottomPlaceCode);
                if (fluxNokCount > 0) return;
                //将电测试工位放行时间点和cycleTime保存到数据库中
                stationCycleTimeService.updateElectricStationLeaveTimeByBarcode(leaveTime, bottomPlaceCode);
                break;
            default:
                System.out.println("**********ERROR************");
                break;
        }
        Float[] rangeData = null;
        if (stationName.contains("FlowTest")){
            MesToFlowTestRange flowStepRangeValue = mesToFlowTestService.getFlowStepRangeValue(currentProductNo);
            rangeData = handlerFlowData(flowStepRangeValue);
        }
        //6、读取测试数据
        String testData = "";
        for (int i = 0, j = 1; i < testNodeList.size(); i++, j++){
            NodeId testNode = new NodeId(3, testNodeList.get(i));
            try {
                Variant variant = opcUaClientTemplate.readNodeVariant(uaClient, testNode);
                UnsignedByte[] datas = (UnsignedByte[]) (variant.getValue());
                byte[] bytes = new byte[datas.length];
                for (int k = 0; k < datas.length; k++) {
                    bytes[k] = datas[k].byteValue();
                }
                testData = new String(bytes);
            } catch (OpcUaClientException e) {
                e.printStackTrace();
                continue;
            }
            log.info("start read testStation Data,station is{}",stationName);
            //保存测试数据到数据库中
            TestStationData testStationData = new TestStationData();
            testStationData.setBarCode(bottomPlaceCode);
            testStationData.setContentType(stationName);
            testStationData.setCreateTime(leaveTime);
            testStationData.setOrderNo(currentOrderNo);
            testStationData.setProductNo(currentProductNo);
            testStationData.setStep("Step" + j);
            switch (stationName) {
                case "LeakageTest1":
                    //将气密检测的测试数据保存到数据库中
                    log.info( "LeakageTest1 data get from plc,{}",testData);
                    if (testData != null && !"".equals(testData)) {
                        String[] results = testData.trim().split("\t");
                        if (results[1].trim().contains("OK")){
                            testStationData.setResult("OK");
                            testStationData.setValue(results[2].trim() + " ml/h");
                        } else {
                            testStationData.setResult("NOK");
                        }
                        writeResponseToPlcForTestStation(RESPONSE_LEAKAGE01);
                    }

                    break;
                case "LeakageTest2":
                    //将气密检测的测试数据保存到数据库中
                    log.info( "LeakageTest2 data get from plc,{}",testData);
                    if (testData != null && !"".equals(testData)) {
                        String[] results = testData.trim().split("\t");
                        if (results[1].trim().contains("OK")){
                            testStationData.setResult("OK");
                            testStationData.setValue(results[2].trim() + " ml/h");
                        } else {
                            testStationData.setResult("NOK");
                        }
                        writeResponseToPlcForTestStation(RESPONSE_LEAKAGE02);
                    }
                    break;
                case "FlowTest1":
                    //将流量检测的测试数据保存到数据库中
                    log.info( "flowTest1 data get from plc,{}",testData);
                    if (testData != null && !"".equals(testData)) {
                        String[] results = testData.trim().split(":");
//                        if (results[1].trim().contains("OK")){
//                            testStationData.setResult("OK");
//                        } else {
//                            testStationData.setResult("NOK");
//                        }
                        String valueStr = results[2].trim();
                        Float resVal = Float.parseFloat(valueStr.replace(" CAL", ""));
                        if (resVal >= rangeData[2 * (j - 1)] && resVal <= rangeData[2 * j - 1]) {
                            testStationData.setResult("OK");
                        } else {
                            testStationData.setResult("NOK");
                        }
                        testStationData.setValue(valueStr);
                        writeResponseToPlcForTestStation(RESPONSE_Flow01);
                    }
                    break;
                case "FlowTest2":
                    //将流量检测的测试数据保存到数据库中
                    log.info( "flowTest2 data get from plc,{}",testData);
                    if (testData != null && !"".equals(testData)) {
                        String[] results = testData.trim().split("\t");
//                        if (results[1].trim().contains("OK")){
//                            testStationData.setResult("OK");
//                        } else {
//                            testStationData.setResult("NOK");
//                        }
                        String valueStr = results[2].trim();
                        Float resVal = Float.parseFloat(valueStr);
                        if (resVal >= rangeData[2 * (j - 1)] && resVal <= rangeData[2 * j - 1]) {
                            testStationData.setResult("OK");
                        } else {
                            testStationData.setResult("NOK");
                        }
                        testStationData.setValue(valueStr + " CAL");
                        writeResponseToPlcForTestStation(RESPONSE_Flow02);
                    }
                    break;
                case "ElectricTest":
                    //将电测试的测试数据保存到数据库中
                    log.info( "ElectricTest data get from plc,{}",testData);
                    if (testData != null && !"".equals(testData)) {
                        String[] results = testData.trim().split(",");
                        if (results[2].trim().contains("PASS")){
                            testStationData.setResult("OK");
                        } else {
                            testStationData.setResult("NOK");
                        }
                        testStationData.setValue(results[4].trim());
                        writeResponseToPlcForTestStation(RESPONSE_ELECTRIC);
                    }
                    break;
                default:
                    System.out.println("**********ERROR************");
                    break;
            }
            testStationDataService.save(testStationData);
            template.convertAndSend("/topic/" + stationName + "/testStationData", testStationData);
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
    //write data to plc for test station response
    public void writeResponseToPlcForTestStation(String nodeName){
        log.info("start to write value to plc,node name is {}",nodeName);
        UaClient uaClient = opcUaClientTemplate.getUaClientList().get(0);
        //
        NodeId printer01Node = new NodeId(3, nodeName);
        try {
            //如果发成功了,则给plc信号,hi 你可以抓取了。往节点写1值
            //写2次
            boolean flag1 = opcUaClientTemplate.writeNodeValue(uaClient, printer01Node, 1);
            if(!flag1){
                boolean flag2 = opcUaClientTemplate.writeNodeValue(uaClient, printer01Node, 1);
            }
            log.info("finish to write value to plc,node name is {}",nodeName);
        }catch (Exception e){
            log.info("write to plc value failure,nodeName is {}",nodeName);
        }
    }
}