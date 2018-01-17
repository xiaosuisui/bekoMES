package com.mj.beko.opcualistener.oldversion;

import com.mj.beko.opcua.OpcUaUtil;
import com.prosysopc.ua.client.MonitoredDataItem;
import com.prosysopc.ua.client.MonitoredDataItemListener;
import org.opcfoundation.ua.builtintypes.DataValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Queue;

/**
 * @author wanghb
 * 贴下底盘底部总成条码处的机器人
 */
@Component
public class BottomBarcodeRobotListener implements MonitoredDataItemListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(BottomBarcodeRobotListener.class);

    @Inject
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void onDataChange(MonitoredDataItem monitoredDataItem, DataValue oldValue, DataValue newValue) {
        LOGGER.info("进入监听器，变化前的值为：{}，变化后的值为：{}", new Object[]{oldValue, newValue});
        if (!OpcUaUtil.isNewNodeValueValid("PLC1", monitoredDataItem.getNodeId(), oldValue, newValue)) {
            return;
        }
        String node = monitoredDataItem.getNodeId().getValue().toString();
        if (!("BekoOpcua.BottomBarcodeRobot.catchProduction".equals(node) && newValue.getValue().intValue() == 1)) return;
        //获取第一个下底盘条码用来生成总成条码
        ValueOperations<String, Object> bottomBarcodeOperations = redisTemplate.opsForValue();
        Queue<String> barcodeForPrinterQueue = (Queue<String>) bottomBarcodeOperations.get("barcodeForPrinterQueue");
        String bottomPlateBarcode = barcodeForPrinterQueue.poll();
        bottomBarcodeOperations.set("barcodeForPrinterQueue", barcodeForPrinterQueue);
        /** 此处调用打印机DLL生产总成条码 **/

    }
}
