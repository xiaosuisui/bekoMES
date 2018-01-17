package com.mj.beko.opcualistener.oldversion;

import com.mj.beko.opcua.OpcUaUtil;
import com.prosysopc.ua.client.MonitoredDataItem;
import com.prosysopc.ua.client.MonitoredDataItemListener;
import org.opcfoundation.ua.builtintypes.DataValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author wanghb
 */
@Component
public class TopPlateListener implements MonitoredDataItemListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopPlateListener.class);

    @Override
    public void onDataChange(MonitoredDataItem monitoredDataItem, DataValue oldValue, DataValue newValue) {
        LOGGER.info("进入监听器，变化前的值为：{}，变化后的值为：{}", new Object[]{oldValue, newValue});
        if (!OpcUaUtil.isNewNodeValueValid("PLC2", monitoredDataItem.getNodeId(), oldValue, newValue)) {
            return;
        }
    }
}
