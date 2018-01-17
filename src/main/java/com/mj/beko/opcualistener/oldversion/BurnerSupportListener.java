package com.mj.beko.opcualistener.oldversion;

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
 * 第二次嘉定测试OPCUA Server
 */
@Component
public class BurnerSupportListener implements MonitoredDataItemListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(BurnerSupportListener.class);

    @Inject
    private InitTcsOrderService initTcsOrderService;

    @Override
    public void onDataChange(MonitoredDataItem monitoredDataItem, DataValue oldValue, DataValue newValue) {
        if (!OpcUaUtil.isNewNodeValueValid("PLC1", monitoredDataItem.getNodeId(), oldValue, newValue)) {
            return;
        }
        System.out.println("*********************************");
        System.out.println("节点变更前值为：" + oldValue.getValue().getValue().toString());
        System.out.println("节点变更后值为：" + newValue.getValue().getValue().toString());
        System.out.println("*********************************");

        //初始值为false
        Boolean oldVal = oldValue.getValue().booleanValue();
        Boolean newVal = newValue.getValue().booleanValue();
        //缺料，开始叫料
        if (newVal) {
            try {
                initTcsOrderService.createTcsOrderSet("station01key", "CallMaterial");
            } catch (IOException e) {
                LOGGER.error("叫料出错！", e);
                e.printStackTrace();
            }
        }
    }
}
