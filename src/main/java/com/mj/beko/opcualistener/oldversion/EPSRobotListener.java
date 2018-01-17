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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * @author wanghb
 */
@Component
public class EPSRobotListener implements MonitoredDataItemListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(EPSRobotListener.class);

    @Inject
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void onDataChange(MonitoredDataItem monitoredDataItem, DataValue oldValue, DataValue newValue) {
        LOGGER.info("进入监听器，变化前的值为：{}，变化后的值为：{}", new Object[]{oldValue, newValue});
        if (!OpcUaUtil.isNewNodeValueValid("PLC1", monitoredDataItem.getNodeId(), oldValue, newValue)) {
            return;
        }
        String node = monitoredDataItem.getNodeId().getValue().toString();
        if (!("BekoOpcua.EPSRobot.putDownEPSUp".equals(node) && newValue.getValue().intValue() == 1)) return;
        ValueOperations<String, Object> redisOperations = redisTemplate.opsForValue();
        Queue<String> bottomPlateBarcodeQueue = (Queue<String>) redisOperations.get("bottomPlateBarcodeQueue");
        Queue<String> epsBarcodeQueue = (Queue<String>) redisOperations.get("epsBarcodeQueue");
        if (redisOperations.get("bottomPlateAndEpsBarcodeQueue") == null) {
            redisOperations.set("bottomPlateAndEpsBarcodeQueue", new LinkedList<Map<String, String>>());
        }
        Queue<Map<String, String>> bottomPlateAndEpsBarcodeQueue = (Queue<Map<String, String>>) redisOperations.get("bottomPlateAndEpsBarcodeQueue");
        Map<String, String> bottomPlateAndEpsBarcodeMap = new HashMap<String, String>();
        bottomPlateAndEpsBarcodeMap.put("bottomPlateBarcode", bottomPlateBarcodeQueue.poll());
        bottomPlateAndEpsBarcodeMap.put("epsBarcode", epsBarcodeQueue.poll());
        bottomPlateAndEpsBarcodeQueue.offer(bottomPlateAndEpsBarcodeMap);
        redisOperations.set("bottomPlateAndEpsBarcodeQueue", bottomPlateAndEpsBarcodeQueue);
    }
}
