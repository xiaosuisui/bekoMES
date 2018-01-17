package com.mj.beko.opcualistener.third;

import com.mj.beko.codeScanner.GetBarcode;
import com.mj.beko.domain.ProductCode;
import com.mj.beko.domain.dto.PrintLabelDto;
import com.mj.beko.httpclient.HttpTemplate;
import com.mj.beko.opcua.OpcUaUtil;
import com.mj.beko.repository.ProductCodeRepository;
import com.mj.beko.service.ProductCodeService;
import com.prosysopc.ua.client.MonitoredDataItem;
import com.prosysopc.ua.client.MonitoredDataItemListener;
import org.opcfoundation.ua.builtintypes.DataValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.time.Instant;

/**
 * @author wanghb
 * 第二台贴标机
 */
@Component
public class DocumentArriveListener implements MonitoredDataItemListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentArriveListener.class);

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

    private static final String PRINTLABEL = "/GasAutomationApi/api/Product/PrintProductLabel";

    @Override
    public void onDataChange(MonitoredDataItem monitoredDataItem, DataValue oldValue, DataValue newValue) {
        LOGGER.info("进入监听器，变化前的值为：{}，变化后的值为：{}", new Object[]{oldValue, newValue});
        if (!OpcUaUtil.isNewNodeValueValid("PLC3", monitoredDataItem.getNodeId(), oldValue, newValue)
                || newValue.getValue().intValue() == 0) {
            return;
        }
        taskExecutor.execute(() -> doThing());
    }

    @Async("taskExecutor")
    public void doThing(){
        //调用扫描枪
        String barcode = getBarcode.getBarcode2();
        ProductCode productCode;
        if (barcode == null) {
            barcode = String.valueOf(Timestamp.from(Instant.now()).getTime());
            productCode = productCodeService.getOldProductCodeOnlyByStatus("1");
        } else {
            //根据eps条码查询ProductCode
            productCode = productCodeService.getProductCodeByEpsCode(barcode);
        }
        String productNo = productCode.getProductNo();
        String orderNo = productCode.getOrderNo();
        String serial = productCode.getSerialNo();
        //调用第二台打印机
        PrintLabelDto printLabelDto = new PrintLabelDto();
        printLabelDto.setProductNo(productNo);
        printLabelDto.setLine("110");
        printLabelDto.setTagType(3);
        printLabelDto.setQuantity(1);
        printLabelDto.setPrinter(1);
        printLabelDto.setOrder("12345678");
        printLabelDto.setSerial(serial);
        ResponseEntity<String> responseEntity;
        int i = 0;
        String returnSerial = "";
        while ("".equals(returnSerial) || (i < 3 && returnSerial.length() > 20)) {
            responseEntity = httpTemplate.postForEntity(httpTemplate.getBekoApiHttpSchemeHierarchical()
                    + PRINTLABEL, printLabelDto, String.class);
            returnSerial = responseEntity.getBody().replace("\"", "");
            i++;
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (returnSerial.length() > 20) {
            //推送消息到前台
            return;
        }
        productCode.setStatus("2");
        productCode.setDocumentEpsCode(barcode);
        productCodeRepository.saveAndFlush(productCode);
        /////////////////////////////////////////
        //          控制PLC滚筒滚动              //
        /////////////////////////////////////////
    }
}