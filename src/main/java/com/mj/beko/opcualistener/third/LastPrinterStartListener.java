package com.mj.beko.opcualistener.third;

import com.mj.beko.codeScanner.GetBarcode;
import com.mj.beko.domain.ProductCode;
import com.mj.beko.domain.dto.PrintLabelDto;
import com.mj.beko.httpclient.HttpTemplate;
import com.mj.beko.opcua.OpcUaClientException;
import com.mj.beko.opcua.OpcUaClientTemplate;
import com.mj.beko.opcua.OpcUaUtil;
import com.mj.beko.repository.ProductCodeRepository;
import com.mj.beko.service.ProductCodeService;
import com.prosysopc.ua.client.MonitoredDataItem;
import com.prosysopc.ua.client.MonitoredDataItemListener;
import com.prosysopc.ua.client.UaClient;
import lombok.extern.slf4j.Slf4j;
import org.opcfoundation.ua.builtintypes.DataValue;
import org.opcfoundation.ua.builtintypes.NodeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wanghb
 * 第三、四台贴标机开始
 */
@Component
@Slf4j
public class LastPrinterStartListener implements MonitoredDataItemListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LastPrinterStartListener.class);

    @Inject
    private TaskExecutor taskExecutor;

    @Inject
    private GetBarcode getBarcode;

    @Inject
    private ProductCodeService productCodeService;

    @Inject
    private ProductCodeRepository productCodeRepository;
    @Inject
    private HttpTemplate httpTemplate;
    @Inject
    private SimpMessagingTemplate template;
    @Autowired
    private RedisTemplate redisTemplate;

    private static final String PRINTLABEL = "/GasAutomationApi/api/Product/PrintProductLabel";

    @Inject
    private OpcUaClientTemplate opcUaClientTemplate;

    @Override
    public void onDataChange(MonitoredDataItem monitoredDataItem, DataValue oldValue, DataValue newValue) {
        LOGGER.info("进入监听器，变化前的值为：{}，变化后的值为：{}", new Object[]{oldValue, newValue});
        if (!OpcUaUtil.isNewNodeValueValid("PLC3", monitoredDataItem.getNodeId(), oldValue, newValue)
                || newValue.getValue().intValue() == 1) {
            return;
        }
        taskExecutor.execute(() -> doThing());
    }

    @Async("taskExecutor")
    public void doThing(){
        //调用扫描枪
        String barcode = getBarcode.getBarcode3();
        ProductCode productCode;
        if (barcode == null) {
            Map<String,String> infoMap =new HashMap<String,String>();
            infoMap.put("result","lastPrinterError");
            infoMap.put("type","13");
            infoMap.put("reason","cant get last printer eps code");
            template.convertAndSend("/topic/lineLeaderScreen/lastPrinterError",infoMap);
            log.info("*****last station printer cant get eps code****");
            return;
        } else {
            //根据eps条码查询ProductCode
            productCode = productCodeService.getProductCodeByEpsCode(barcode);
        }
        if (productCode == null) {
            Map<String,String> stringMap =new HashMap<String,String>();
            stringMap.put("result","lastPrinterError");
            stringMap.put("type","10");
            stringMap.put("reason","get eps code but cant search eps info in productModule,eps value is"+barcode);
            log.info(" have get eps code,but no eps info in productCode module");
            return;
        }
        String productNo = productCode.getProductNo();
        String orderNo = productCode.getOrderNo();
        String serial = productCode.getSerialNo();
        //调用第三台打印机
        PrintLabelDto printLabelDto = new PrintLabelDto();
        printLabelDto.setProductNo(productNo);
        printLabelDto.setLine("110");
        printLabelDto.setTagType(1);
        printLabelDto.setQuantity(1);
        printLabelDto.setPrinter(1);
        printLabelDto.setSerial("");
        printLabelDto.setOrder("12345678");
        printLabelDto.setSerial(serial);
        ResponseEntity<String> responseEntity;
        int i = 0;
        String returnSerial = "";
        while ("".equals(returnSerial) || (i < 3 && returnSerial.length() > 20)) {
            responseEntity = httpTemplate.postForEntity(httpTemplate.getBekoApiHttpSchemeHierarchical()
                    + PRINTLABEL, printLabelDto, String.class);
            returnSerial = responseEntity.getBody().replace("\"", "");
            log.info("***third  printer return serial number**** ,{}",returnSerial);
            i++;
        }
        if (returnSerial.length() > 20) {
            //调用最后2台的打印机的api错误
            Map<String,String> infoMap =new HashMap<String,String>();
            infoMap.put("result","lastPrinterError");
            infoMap.put("type","11");
            infoMap.put("reason","call third printerApi error,return serial Number is"+returnSerial);
            template.convertAndSend("/topic/lineLeaderScreen/lastPrinterError",infoMap);
            log.info("call third printerApi error,return serial Number is,{}",returnSerial);
            return;
        }
        redisTemplate.opsForValue().set("cachePackage01Label",returnSerial);
        int j = 0;
        String returnSerial1 = "";
        printLabelDto.setPrinter(2);
        while ("".equals(returnSerial1) || (j < 3 && returnSerial1.length() > 20)) {
            responseEntity = httpTemplate.postForEntity(httpTemplate.getBekoApiHttpSchemeHierarchical()
                    + PRINTLABEL, printLabelDto, String.class);
            returnSerial1 = responseEntity.getBody().replace("\"", "");
            log.info("***fouth  printer return serial number**** ,{}",returnSerial1);
            j++;
        }
        if (returnSerial1.length() > 20) {
            Map<String,String> infoMap =new HashMap<String,String>();
            infoMap.put("result","lastPrinterError");
            infoMap.put("type","12");
            infoMap.put("reason","call fouth printerApi error,return serial Number is"+returnSerial1);
            template.convertAndSend("/topic/lineLeaderScreen/lastPrinterError",infoMap);
            log.info("call fouth printerApi error,return serial Number is,{}",returnSerial1);
            //推送消息到前台
            return;
        }
        redisTemplate.opsForValue().set("cachePackage02Label",returnSerial1);
        productCode.setStatus("3");
        productCode.setPackageEpsCode(barcode);
        productCodeRepository.saveAndFlush(productCode);
        //控制plc放行
        log.info("start write value to plc,get last printer info,can go, serialNumber is {},{}",returnSerial,returnSerial1);
        UaClient uaClient = opcUaClientTemplate.getUaClientList().get(2);
        NodeId finishNode = new NodeId(3, "\"ITread\".\"Read_Finish\"");
        try {
            opcUaClientTemplate.writeNodeValue(uaClient, finishNode, 1);
        } catch (OpcUaClientException e) {
            e.printStackTrace();
        }
        log.info("success write into to plc,last printer can go,serialNumber is {}{}",returnSerial,returnSerial1);
    }
}