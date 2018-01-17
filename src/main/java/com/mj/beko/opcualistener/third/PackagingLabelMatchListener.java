package com.mj.beko.opcualistener.third;

import com.mj.beko.codeScanner.MinaTcpSickReaderForPacking01;
import com.mj.beko.codeScanner.MinaTcpSickReaderForPacking02;
import com.mj.beko.codeScanner.MinaTcpSickReaderForPacking03;
import com.mj.beko.opcua.OpcUaUtil;
import com.prosysopc.ua.client.MonitoredDataItem;
import com.prosysopc.ua.client.MonitoredDataItemListener;
import lombok.extern.slf4j.Slf4j;
import org.opcfoundation.ua.builtintypes.DataValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;

/**
 * Created by Ricardo on 2018/1/15.
 */
@Slf4j
@Component
public class PackagingLabelMatchListener implements MonitoredDataItemListener {
    @Autowired
    private MinaTcpSickReaderForPacking01 minaTcpSickReaderForPacking01;//第三个打印机的条码
    @Autowired
    private MinaTcpSickReaderForPacking02 minaTcpSickReaderForPacking02;//第四个打印机的条码
    @Autowired
    private MinaTcpSickReaderForPacking03 minaTcpSickReaderForPacking03;//eps条码
    @Inject
    private TaskExecutor taskExecutor;
    @Override
    public void onDataChange(MonitoredDataItem monitoredDataItem, DataValue oldValue, DataValue newValue) {
        log.info("listener read printer01 label signal,");
        if (!OpcUaUtil.isNewNodeValueValid("PLC3", monitoredDataItem.getNodeId(), oldValue, newValue)
                || newValue.getValue().intValue() == 0) {
            return;
        }
        //读取贴标机打印出来的条码info
        taskExecutor.execute(() -> readPackagePrinterLabel());
    }
    @Async("taskExecutor")
    public void readPackagePrinterLabel(){
        log.info("start read printer03 label....");
        minaTcpSickReaderForPacking03.connect();
        try {
            minaTcpSickReaderForPacking03.startsick();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
