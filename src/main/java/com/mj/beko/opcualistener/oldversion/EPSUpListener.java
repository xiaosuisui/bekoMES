package com.mj.beko.opcualistener.oldversion;

import com.mj.beko.codeScanner.GetBarcode;
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
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author wanghb
 */
@Component
public class EPSUpListener implements MonitoredDataItemListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(EPSUpListener.class);

    @Inject
    private GetBarcode getBarcode;

    @Inject
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void onDataChange(MonitoredDataItem monitoredDataItem, DataValue oldValue, DataValue newValue) {
        LOGGER.info("进入监听器，变化前的值为：{}，变化后的值为：{}", new Object[]{oldValue, newValue});
        if (!OpcUaUtil.isNewNodeValueValid("PLC1", monitoredDataItem.getNodeId(), oldValue, newValue)) {
            return;
        }
        if (newValue.getValue().intValue() == 0) return;
        //调用固定扫描仪读取上泡沫条码
//        String epsBarcode = getBarcode.getBarcode();
//        if (epsBarcode == null) epsBarcode = "";
        String epsBarcode = "302010"; //模拟EPS条码
        ValueOperations<String, Object> epsBarcodeOperations = redisTemplate.opsForValue();
        if (epsBarcodeOperations.get("epsBarcodeQueue") == null) {
            epsBarcodeOperations.set("epsBarcodeQueue", new LinkedList<String>());
        }
        Queue<String> epsBarcodeQueue = (Queue<String>) epsBarcodeOperations.get("epsBarcodeQueue");
        if (!epsBarcodeQueue.contains(epsBarcode)) {
            epsBarcodeQueue.offer(epsBarcode);
            epsBarcodeOperations.set("epsBarcodeQueue", epsBarcodeQueue);
        }
    }
}
