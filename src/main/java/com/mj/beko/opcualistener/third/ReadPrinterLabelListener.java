package com.mj.beko.opcualistener.third;

import com.mj.beko.codeScanner.MinaTcpSickReader;
import com.mj.beko.opcua.OpcUaUtil;
import com.prosysopc.ua.client.MonitoredDataItem;
import com.prosysopc.ua.client.MonitoredDataItemListener;
import lombok.extern.slf4j.Slf4j;
import org.opcfoundation.ua.builtintypes.DataValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;

/**
 * Created by Ricardo on 2018/1/13.
 * 读取贴标机打印出来的标签的信号
 */
@Slf4j
@Component
public class ReadPrinterLabelListener implements MonitoredDataItemListener {
    @Inject
    private TaskExecutor taskExecutor;
    @Inject
    private RedisTemplate redisTemplate;
    @Autowired
    MinaTcpSickReader minaTcpSickReader;
    @Override
    public void onDataChange(MonitoredDataItem monitoredDataItem, DataValue oldValue, DataValue newValue) {
        log.info("listener read printer01 label signal,");
        if (!OpcUaUtil.isNewNodeValueValid("PLC3", monitoredDataItem.getNodeId(), oldValue, newValue)
                || newValue.getValue().intValue() == 0) {
            return;
        }
        //读取贴标机打印出来的条码信息
        taskExecutor.execute(() -> readPrinterLabel());
    }
    @Async("taskExecutor")
    public void readPrinterLabel(){
        //读取贴标机的条码
        minaTcpSickReader.connect();
        try {
            minaTcpSickReader.startsick();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
