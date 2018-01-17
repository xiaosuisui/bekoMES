package com.mj.beko.opcualistener.second;

import com.mj.beko.opcua.OpcUaUtil;
import com.mj.beko.tcs.InitTcsOrderService;
import com.prosysopc.ua.client.MonitoredDataItem;
import com.prosysopc.ua.client.MonitoredDataItemListener;
import org.opcfoundation.ua.builtintypes.DataValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.inject.Inject;
import java.io.IOException;

/**
 * @author wanghb
 * 二段旋钮工位箱子缺料和到料
 */
@Component
public class KnobsBoxLackOfMaterialAndArriveListener implements MonitoredDataItemListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(KnobsBoxLackOfMaterialAndArriveListener.class);

    @Inject
    private InitTcsOrderService initTcsOrderService;

    @Override
    public void onDataChange(MonitoredDataItem monitoredDataItem, DataValue oldValue, DataValue newValue) {
        LOGGER.info("进入监听器，变化前的值为：{}，变化后的值为：{}", new Object[]{oldValue, newValue});
        if (!OpcUaUtil.isNewNodeValueValid("PLC2", monitoredDataItem.getNodeId(), oldValue, newValue)
                || newValue.getValue().intValue() == 0) {
            return;
        }
        try {
            initTcsOrderService.createTcsOrderSet("LIULIJIA", "LIULIJIA");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}