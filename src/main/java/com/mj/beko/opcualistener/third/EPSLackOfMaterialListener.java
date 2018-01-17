package com.mj.beko.opcualistener.third;

import com.mj.beko.domain.TcsOrder;
import com.mj.beko.opcua.OpcUaUtil;
import com.mj.beko.service.TcsOrderService;
import com.mj.beko.tcs.InitTcsOrderService;
import com.prosysopc.ua.client.MonitoredDataItem;
import com.prosysopc.ua.client.MonitoredDataItemListener;
import org.opcfoundation.ua.builtintypes.DataValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.inject.Inject;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

/**
 * @author wanghb
 * EPS缺料
 */
@Component
public class EPSLackOfMaterialListener implements MonitoredDataItemListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(EPSLackOfMaterialListener.class);

    @Inject
    private InitTcsOrderService initTcsOrderService;

    @Inject
    private TcsOrderService tcsOrderService;

    private static Timestamp underLackTime = null;
    private static Timestamp upLackTime = null;

    @Override
    public void onDataChange(MonitoredDataItem monitoredDataItem, DataValue oldValue, DataValue newValue) {
        LOGGER.info("进入监听器，变化前的值为：{}，变化后的值为：{}", new Object[]{oldValue, newValue});
        if (!OpcUaUtil.isNewNodeValueValid("PLC3", monitoredDataItem.getNodeId(), oldValue, newValue)
                || newValue.getValue().intValue() == 0) {
            return;
        }
        String node = monitoredDataItem.getNodeId().getValue().toString();
        try {
            if ("\"ITread\".\"EPS_under_demand\"".equals(node)) {
                if (underLackTime != null) {
                    //防止时间光电信号不稳定出现重复叫料,忽略1分钟内重复连续叫料
                    Timestamp nowTime = Timestamp.from(Instant.now());
                    if ((nowTime.getTime() - underLackTime.getTime())/(1000 * 60) < 1) return;
                }
                underLackTime = Timestamp.from(Instant.now());
                initTcsOrderService.createTcsOrderSet("EPS", "EPSDOWN");
            } else if ("\"ITread\".\"EPS_on_demand\"".equals(node)) {
                if (upLackTime != null) {
                    //防止时间光电信号不稳定出现重复叫料,忽略1分钟内重复连续叫料
                    Timestamp nowTime = Timestamp.from(Instant.now());
                    if ((nowTime.getTime() - upLackTime.getTime())/(1000 * 60) < 1) return;
                }
                //如果存在下泡沫的订单，则不生成上泡沫的订单(查询状态不为4和5的epsDown类型的订单。如果查到了,则不生成上泡沫订单)
                //怎么获取当前正在执行的状态为3的订单(取1的逻辑主要是为了处理很多僵尸记录的情况(例如:订单未执行。))
                List<TcsOrder> tcsOrders = tcsOrderService.getLastEpsDownTypeTcsOrder();
                if (tcsOrders != null && tcsOrders.size() > 0) {
                    TcsOrder tcsOrder;
                    if (tcsOrders.size() == 1) {
                        tcsOrder = tcsOrders.get(0);
                    } else {
                        tcsOrder = tcsOrders.get(1);
                    }
                    //如果此时下泡沫订单状态为3，则发送执行下一步操作的命令
                    if ("3".equals(tcsOrder.getState())) {
                        initTcsOrderService.createNextStepTcsOrder(tcsOrder.getTcsOrderName(), "Location-0023");
                    }
                    return;
                }
                upLackTime = Timestamp.from(Instant.now());
                initTcsOrderService.createTcsOrderSet("EPS", "EPSUP");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}