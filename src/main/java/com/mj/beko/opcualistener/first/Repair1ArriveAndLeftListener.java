package com.mj.beko.opcualistener.first;

import com.mj.beko.domain.Order;
import com.mj.beko.opcua.OpcUaUtil;
import com.mj.beko.opcualistener.AsynPushDataToScreen;
import com.mj.beko.service.*;
import com.prosysopc.ua.client.MonitoredDataItem;
import com.prosysopc.ua.client.MonitoredDataItemListener;
import org.opcfoundation.ua.builtintypes.DataValue;
import org.opcfoundation.ua.builtintypes.UnsignedByte;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import javax.inject.Inject;
import java.util.List;

/**
 * @author wanghb
 * 一段返修
 */
@Component
public class Repair1ArriveAndLeftListener implements MonitoredDataItemListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(Repair1ArriveAndLeftListener.class);

    @Inject
    private RedisTemplate<String, Object> redisTemplate;

    @Inject
    private SimpMessagingTemplate template;

    @Inject
    private AsynPushDataToScreen asynPushDataToScreen;

    @Inject
    private OrderService orderService;

    @Override
    public void onDataChange(MonitoredDataItem monitoredDataItem, DataValue oldValue, DataValue newValue) {
        LOGGER.info("进入监听器，变化前的值为：{}，变化后的值为：{}", new Object[]{oldValue, newValue});
        if (!OpcUaUtil.isNewNodeValueValid("PLC1", monitoredDataItem.getNodeId(), oldValue, newValue)) {
            return;
        }
        String node = monitoredDataItem.getNodeId().getValue().toString();
        String palletNo = ((UnsignedByte[]) newValue.getValue().getValue())[0].intValue() + "";
        if ("0".equals(palletNo)) return;

        //推送当前工单、下一工单和托盘号
        template.convertAndSend("/topic/Repair01/palletNo", palletNo);
        HashOperations<String, String, String> cacheDatas = redisTemplate.opsForHash();
        String currentOrderNo = cacheDatas.get(palletNo, "currentOrderNo");
        String nextOrderNo = cacheDatas.get(palletNo, "nextOrderNo");
        List<Order> orders = orderService.getOrderListByOrderNos(currentOrderNo, nextOrderNo);
        if (orders == null || orders.size() <= 0) return;
        Order currentOrder = orders.get(0);
        Order nextOrder = orders.size() == 2 ? orders.get(1) : null;
        //异步推送当前工单和下一工单到一段返修工位工位
        asynPushDataToScreen.pushCurrentAndNextOrder(currentOrder, nextOrder, "Repair01");
    }
}