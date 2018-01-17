package com.mj.beko.opcualistener.third;

import com.mj.beko.opcua.OpcUaUtil;
import com.mj.beko.service.OrderService;
import com.mj.beko.service.StationCycleTimeService;
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
 * 最后一个工位产品下线
 */
@Component
public class LastStationListener implements MonitoredDataItemListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LastStationListener.class);

    @Inject
    private RedisTemplate<String, Object> redisTemplate;

    @Inject
    private StationCycleTimeService stationCycleTimeService;

    @Inject
    private OrderService orderService;

    @Override
    public void onDataChange(MonitoredDataItem monitoredDataItem, DataValue oldValue, DataValue newValue) {
        LOGGER.info("进入监听器，变化前的值为：{}，变化后的值为：{}", new Object[]{oldValue, newValue});
        if (!OpcUaUtil.isNewNodeValueValid("PLC3", monitoredDataItem.getNodeId(), oldValue, newValue)
                || newValue.getValue().intValue() == 0) {
            return;
        }
        ValueOperations<String, Object> epsBarcodeOperations = redisTemplate.opsForValue();
        Queue<String> epsBarcodeQueue = (Queue<String>) epsBarcodeOperations.get("bottomPlateBarcodeQueue");
        String bottomPlateBarcode = epsBarcodeQueue.poll();
        //根据下底盘条码查询订单号
        String orderNo = stationCycleTimeService.getOrderNoByBottomPlaceCode(bottomPlateBarcode);
        //根据订单号修改订单表中的完成数量
        orderService.updateCompletionNumberByOrderNo(orderNo);
        //当completion_number+broken_number=quantity时根据订单号将订单结束
        int count = orderService.finishOrderByOrderNo(orderNo);
        if (count > 0) {
            //清除当前订单的缓存
            redisTemplate.delete(orderNo);
        }
    }
}