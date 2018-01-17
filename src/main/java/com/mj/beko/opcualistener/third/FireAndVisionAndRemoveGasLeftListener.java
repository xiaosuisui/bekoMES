package com.mj.beko.opcualistener.third;

import com.mj.beko.domain.ProductCode;
import com.mj.beko.domain.dto.PrintLabelDto;
import com.mj.beko.httpclient.HttpTemplate;
import com.mj.beko.opcua.OpcUaClientException;
import com.mj.beko.opcua.OpcUaClientTemplate;
import com.mj.beko.opcua.OpcUaUtil;
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
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import javax.inject.Inject;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

/**
 * @author wanghb
 * 3段燃烧、终检和拔气放行信号监听器
 */
@Slf4j
@Component
public class FireAndVisionAndRemoveGasLeftListener implements MonitoredDataItemListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(FireAndVisionAndRemoveGasLeftListener.class);

    private static final String FIRE_TEST1_LEAVE = "\"ITread\".\"ranshao_leave\"";
    private static final String FIRE_TEST2_LEAVE = "\"ITread\".\"ranshao2_leave\"";
    private static final String VISION_CONTROL_LEAVE = "\"ITread\".\"zhongjian_leave\"";
    private static final String REMOVE_ELECTRIC_GAS_LEAVE = "\"ITread\".\"baxian_leave\"";

    private static final String FIRE_TEST1_RFID = "\"ITread\".\"ranshao_ID\"";
    private static final String FIRE_TEST2_RFID = "\"ITread\".\"ranshao2_ID\"";
    private static final String VISION_CONTROL_RFID = "\"ITread\".\"zhongjian_ID\"";
    private static final String REMOVE_ELECTRIC_GAS_RFID = "\"ITread\".\"baxian_ID\"";
    private static final String EMPTY_POINT = "\"ITread\".\"baxian_empty\"";//空托盘节点

    private static final String PRINTLABEL = "/GasAutomationApi/api/Product/PrintProductLabel";

    @Inject
    private OpcUaClientTemplate opcUaClientTemplate;

    @Inject
    private RedisTemplate<String, Object> redisTemplate;

    @Inject
    private PalletService palletService;

    @Inject
    private StationCycleTimeService stationCycleTimeService;

    @Inject
    private SimpMessagingTemplate template;

    @Inject
    private HttpTemplate httpTemplate;

    @Inject
    private TaskExecutor taskExecutor;

    @Inject
    private ProductCodeService productCodeService;

    @Override
    public void onDataChange(MonitoredDataItem monitoredDataItem, DataValue oldValue, DataValue newValue) {
        LOGGER.info("进入监听器，变化前的值为：{}，变化后的值为：{}", new Object[]{oldValue, newValue});
        if (!OpcUaUtil.isNewNodeValueValid("PLC3", monitoredDataItem.getNodeId(), oldValue, newValue)
                || newValue.getValue().intValue() == 0) {
            return;
        }
        String node = monitoredDataItem.getNodeId().getValue().toString();
        //记录放行
        switch (node){
            case FIRE_TEST1_LEAVE:
                doThingWhenLeave(FIRE_TEST1_RFID, "FireTest1");
                break;
            case FIRE_TEST2_LEAVE:
                doThingWhenLeave(FIRE_TEST2_RFID, "FireTest2");
                break;
            case VISION_CONTROL_LEAVE:
                doThingWhenLeave(VISION_CONTROL_RFID, "VisionControl");
                break;
            case REMOVE_ELECTRIC_GAS_LEAVE:
                doThingWhenLeave(REMOVE_ELECTRIC_GAS_RFID, "RemoveElectricAndGas");
                break;
            default:
                break;
        }
    }

    private void doThingWhenLeave(String rfidNode, String stationName){
        //1、记录放行的时间点
        Timestamp leaveTime = Timestamp.from(Instant.now());
        //2、获取托盘号
        UaClient uaClient = opcUaClientTemplate.getUaClientList().get(2);
        NodeId rfidPallet = new NodeId(3, rfidNode);
        String palletNo;
        try {
            Variant readNodeVariant = opcUaClientTemplate.readNodeVariant(uaClient, rfidPallet);
            palletNo = ((UnsignedByte[])readNodeVariant.getValue())[0].intValue() + "";
        } catch (OpcUaClientException e) {
            LOGGER.error("********************cant read palletNo*************************");
            e.printStackTrace();
            return;
        }
        //3、如果为空托盘直接放行
        if (!redisTemplate.hasKey(palletNo)) return;
        HashOperations<String, String, String> cacheDatas = redisTemplate.opsForHash();
        String orderNo = cacheDatas.get(palletNo, "currentOrderNo");
        String bottomPlateBarcode = cacheDatas.get(palletNo, "bottomPlateBarcode");
        String productNo = cacheDatas.get(palletNo, "currentProductNo");
        Timestamp leaves = null;
        String averageTime = "0";
        switch (stationName){
            case "FireTest1":
            case "FireTest2":
                //根据下底盘条码获取火焰测试工位放行时间点，如果不为空则不将工位完成数量加1
                leaves = stationCycleTimeService.getFireTestLeftTimeByBottomPlateBarcode(bottomPlateBarcode);
                //根据下底盘条码保存火焰测试的放行时间和cycleTime
                stationCycleTimeService.updateFireTestStationLeftTimeByBarcode(leaveTime, bottomPlateBarcode);
                //计算当前工单在火焰测试工位的平均时间
                averageTime = stationCycleTimeService.getFireTestAverageTime(orderNo);
                break;
            case "VisionControl":
                //根据下底盘条码获取视觉控制工位放行时间点，如果不为空则不将工位完成数量加1
                leaves = stationCycleTimeService.getVisionControlLeftTimeByBottomPlateBarcode(bottomPlateBarcode);
                //根据下底盘条码保存视觉控制的放行时间和cycleTime
                stationCycleTimeService.updateVisionControlStationLeftTimeByBarcode(leaveTime, bottomPlateBarcode);
                //计算当前工单在视觉控制工位的平均时间
                averageTime = stationCycleTimeService.getVisionControlAverageTime(orderNo);
                break;
            case "RemoveElectricAndGas":
                //最后一个人工工位判断如果是空托盘，则清除托盘信息
                //直接读传感器的功能,判断是否为空托盘
                boolean result=false;
                UaClient uaClient1 = opcUaClientTemplate.getUaClientList().get(2);
                NodeId emptyPoint = new NodeId(3, EMPTY_POINT);
                try {
                    Variant readNodeVariant = opcUaClientTemplate.readNodeVariant(uaClient1, emptyPoint);
                    result= readNodeVariant.booleanValue();
                    log.info("get empty pallet Result {}",result);
                } catch (OpcUaClientException e) {
                    e.printStackTrace();
                }
                //true表示空托盘,则直接返回
                if(result){
                    log.info("haha,empty need to return，result {}",result);
                    //清除托盘所有的数据
                    redisTemplate.delete(palletNo);
                    //清除托盘绑定的数据
                    palletService.clearPalletData(palletNo);
                    return;
                }
                //将下底盘条码放到队列并保存到缓存中
/*                ValueOperations<String, Object> bottomBarcodeOperations = redisTemplate.opsForValue();
                if (bottomBarcodeOperations.get("bottomPlateBarcodeQueue") == null) {
                    bottomBarcodeOperations.set("bottomPlateBarcodeQueue", new LinkedList<String>());
                }
                Queue<String> bottomPlateBarcodeQueue = (Queue<String>) bottomBarcodeOperations.get("bottomPlateBarcodeQueue");
                if (!bottomPlateBarcodeQueue.contains(bottomPlateBarcode)) {
                    bottomPlateBarcodeQueue.add(bottomPlateBarcode);
                    bottomBarcodeOperations.set("bottomPlateBarcodeQueue", bottomPlateBarcodeQueue);
                }*/
/*
                //异步调用第一台打印机
                LOGGER.debug("*************开始调用第一台打印机，托盘号为：" + palletNo);
                taskExecutor.execute(() -> printer1(productNo, orderNo, bottomPlateBarcode));*/

/*                //清除托盘所有的数据
                redisTemplate.delete(palletNo);
                //清除托盘绑定的数据
                palletService.clearPalletData(palletNo);*/
                //根据下底盘条码获取拔电气工位放行时间点，如果不为空则不将工位完成数量加1
                leaves = stationCycleTimeService.getRemoveElectricAndGasLeftTimeByBottomPlateBarcode(bottomPlateBarcode);
                //根据下底盘条码保存拔电气的放行时间和cycleTime
                stationCycleTimeService.updateRemoveElectricAndGasStationLeftTimeByBarcode(leaveTime, bottomPlateBarcode);
                //计算当前工单在拔电气工位的平均时间
                averageTime = stationCycleTimeService.getRemoveElectricAndGasAverageTime(orderNo);
                break;
            default:
                break;
        }
        String count = cacheDatas.get(orderNo, stationName);
        if (leaves == null) {
            count = Integer.parseInt(count) + 1 + "";
            cacheDatas.put(orderNo, stationName, count);
        }
        //将当前工位完成数量和平均时间推送到前台
        Map<String, String> countAndAverageTime = new HashMap<>();
        countAndAverageTime.put("currentFinished", count);
        countAndAverageTime.put("averageTime", averageTime);
        template.convertAndSend("/topic/" + stationName + "/countAndAverageTime", countAndAverageTime);
    }

    @Async("taskExecutor")
    public void printer1(String productNo, String orderNo, String bottomPlateBarcode) {
        PrintLabelDto printLabelDto = new PrintLabelDto();
        printLabelDto.setProductNo(productNo);
        printLabelDto.setLine("110");
        printLabelDto.setTagType(4);
        printLabelDto.setQuantity(1);
        printLabelDto.setPrinter(1);
        printLabelDto.setSerial("");
        printLabelDto.setOrder("12345678");
        String serial = "";
        ResponseEntity<String> responseEntity;
        int i = 0;
        while ("".equals(serial) || (i < 6 && serial.length() > 20)) {
            responseEntity = httpTemplate.postForEntity(httpTemplate.getBekoApiHttpSchemeHierarchical()
                    + PRINTLABEL, printLabelDto, String.class);
            serial = responseEntity.getBody().replace("\"", "");
            LOGGER.info("******************"+i+"ci get serialNumber printer01：" + serial);
            try {
                //延迟500ms调用
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i++;
        }
        //如果6次调用失败
        if (serial.length() > 20) {
            //推送消息到前台
            log.info("line leader screen printer,{},{},{}", productNo, orderNo, bottomPlateBarcode);
            //调用8次还没有返回正确的serialNumber，推送信息到前台
            template.convertAndSend("/topic/lineLeaderScreen/printApiError", productNo + "," + orderNo + "," + bottomPlateBarcode);
            LOGGER.error("******************get error serialNumber printer01：" + serial);
            LOGGER.error("******************failure printer api*********************");
            return;
        }
        ProductCode productCode = productCodeService.getProductCodeByBottomPlateBarCode(bottomPlateBarcode);
        //表明每次为空的时候生成一条记录
        if (productCode == null) {
            productCode = new ProductCode();
            productCode.setCreateDate(Timestamp.from(Instant.now()));
            productCode.setOrderNo(orderNo);
            productCode.setProductNo(productNo);
            productCode.setProductCode(bottomPlateBarcode); //下底盘条码
            productCode.setStatus("0");
            productCode.setSerialNo(serial);
            productCodeService.save(productCode);
            UaClient uaClient = opcUaClientTemplate.getUaClientList().get(2);

            NodeId printer01Node = new NodeId(3, "\"ITread\".\"Sebd_Labeling\"");
            try {
                //如果发成功了,则给plc信号,hi 你可以抓取了。往节点写1值
                //写2次
                boolean flag1 = opcUaClientTemplate.writeNodeValue(uaClient, printer01Node, 1);
                if(!flag1){
                    boolean flag2 = opcUaClientTemplate.writeNodeValue(uaClient, printer01Node, 1);
                }
            }catch (Exception e){
                log.error("get opcua failure,write sigual to printer01");
            }
        }else{
            template.convertAndSend("/topic/lineLeaderScreen/doubleProduct",bottomPlateBarcode);
        }
    }
    @Async("taskExecutor")
    private void printer(){
        try {
            //第一台打印机
            PrintLabelDto printLabelDto = new PrintLabelDto();
            printLabelDto.setProductNo("7783270115");
            printLabelDto.setLine("110");
            printLabelDto.setTagType(4);
            printLabelDto.setQuantity(1);
            printLabelDto.setPrinter(1);
            printLabelDto.setSerial("");
            printLabelDto.setOrder("12345678");
            ResponseEntity<String> responseEntity = httpTemplate.postForEntity(httpTemplate.getBekoApiHttpSchemeHierarchical()
                    + PRINTLABEL, printLabelDto, String.class);
            String serial = responseEntity.getBody().replace("\"", "");
            LOGGER.info("============第一台打印机条码序列号为：" + serial);
            printLabelDto.setSerial(serial);
            //第二台打印机
            printLabelDto.setTagType(3);
            ResponseEntity<String> responseEntity2 = httpTemplate.postForEntity(httpTemplate.getBekoApiHttpSchemeHierarchical() + PRINTLABEL, printLabelDto, String.class);
            LOGGER.info("============第二台打印机条码序列号为：" + responseEntity2.getBody().replace("\"", ""));
            //第三台打印机
            printLabelDto.setTagType(1);
            ResponseEntity<String> responseEntity3 = httpTemplate.postForEntity(httpTemplate.getBekoApiHttpSchemeHierarchical() + PRINTLABEL, printLabelDto, String.class);
            LOGGER.info("============第三台打印机条码序列号为：" + responseEntity3.getBody().replace("\"", ""));
            //第四台打印机
            printLabelDto.setPrinter(2);
            ResponseEntity<String> responseEntity4 = httpTemplate.postForEntity(httpTemplate.getBekoApiHttpSchemeHierarchical() + PRINTLABEL, printLabelDto, String.class);
            LOGGER.info("============第四台打印机条码序列号为：" + responseEntity4.getBody().replace("\"", ""));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("=========调用打印机API错误==========");
        }
    }
}