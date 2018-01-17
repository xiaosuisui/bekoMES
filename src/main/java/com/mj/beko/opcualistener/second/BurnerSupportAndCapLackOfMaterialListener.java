package com.mj.beko.opcualistener.second;

import com.mj.beko.opcua.OpcUaClientTemplate;
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
 * 二段6个机器人工位的12个缺料信号
 */
@Component
public class BurnerSupportAndCapLackOfMaterialListener implements MonitoredDataItemListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(BurnerSupportAndCapLackOfMaterialListener.class);

    @Inject
    private OpcUaClientTemplate opcUaClientTemplate;

    @Inject
    private InitTcsOrderService initTcsOrderService;

    private static final String BS1_LEFT = "\"OPCOA\".\"BS1LeftLack\"";
    private static final String BS1_RIGHT = "\"OPCOA\".\"BS1RightLack\"";
    private static final String BS2_LEFT = "\"OPCOA\".\"BS2LeftLack\"";
    private static final String BS2_RIGHT = "\"OPCOA\".\"BS2RightLack\"";
    private static final String BS3_LEFT = "\"OPCOA\".\"BS3LeftLack\"";
    private static final String BS3_RIGHT = "\"OPCOA\".\"BS3RightLack\"";
    private static final String BS4_LEFT = "\"OPCOA\".\"BS4LeftLack\"";
    private static final String BS4_RIGHT = "\"OPCOA\".\"BS4RightLack\"";
    private static final String BC1_LEFT = "\"OPCOA\".\"BC1LeftLack\"";
    private static final String BC1_RIGHT = "\"OPCOA\".\"BC1RightLack\"";
    private static final String BC2_LEFT = "\"OPCOA\".\"BC2LeftLack\"";
    private static final String BC2_RIGHT = "\"OPCOA\".\"BC2RightLack\"";

    @Override
    public void onDataChange(MonitoredDataItem monitoredDataItem, DataValue oldValue, DataValue newValue) {
        LOGGER.info("进入监听器，变化前的值为：{}，变化后的值为：{}", new Object[]{oldValue, newValue});
        if (!OpcUaUtil.isNewNodeValueValid("PLC2", monitoredDataItem.getNodeId(), oldValue, newValue)
                || newValue.getValue().intValue() == 0) {
            return;
        }
        String node = monitoredDataItem.getNodeId().getValue().toString();
        try {
            switch (node) {
                case BS1_LEFT:
                    initTcsOrderService.createTcsOrderSet("BS1Left", "GUNTONG");
                    break;
                case BS1_RIGHT:
                    initTcsOrderService.createTcsOrderSet("BS1Right", "GUNTONG");
                    break;
                case BS2_LEFT:
                    initTcsOrderService.createTcsOrderSet("BS2Left", "GUNTONG");
                    break;
                case BS2_RIGHT:
                    initTcsOrderService.createTcsOrderSet("BS2Right", "GUNTONG");
                    break;
                case BS3_LEFT:
                    initTcsOrderService.createTcsOrderSet("BS3Left", "GUNTONG");
                    break;
                case BS3_RIGHT:
                    initTcsOrderService.createTcsOrderSet("BS3Right", "GUNTONG");
                    break;
                case BS4_LEFT:
                    initTcsOrderService.createTcsOrderSet("BS4Left", "GUNTONG");
                    break;
                case BS4_RIGHT:
                    initTcsOrderService.createTcsOrderSet("BS4Right", "GUNTONG");
                    break;
                case BC1_LEFT:
                    initTcsOrderService.createTcsOrderSet("BC1Left", "GUNTONG");
                    break;
                case BC1_RIGHT:
                    initTcsOrderService.createTcsOrderSet("BC1Right", "GUNTONG");
                    break;
                case BC2_LEFT:
                    initTcsOrderService.createTcsOrderSet("BC2Left", "GUNTONG");
                    break;
                case BC2_RIGHT:
                    initTcsOrderService.createTcsOrderSet("BC2Right", "GUNTONG");
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}