package com.mj.beko.opcualistener.first;

import com.mj.beko.domain.Order;
import com.mj.beko.opcua.OpcUaClientException;
import com.mj.beko.opcua.OpcUaClientTemplate;
import com.mj.beko.opcua.OpcUaUtil;
import com.mj.beko.opcualistener.AsynPushDataToScreen;
import com.mj.beko.service.OrderService;
import com.mj.beko.service.StationCycleTimeService;
import com.prosysopc.ua.client.MonitoredDataItem;
import com.prosysopc.ua.client.MonitoredDataItemListener;
import com.prosysopc.ua.client.UaClient;
import org.opcfoundation.ua.builtintypes.DataValue;
import org.opcfoundation.ua.builtintypes.NodeId;
import org.opcfoundation.ua.builtintypes.UnsignedByte;
import org.opcfoundation.ua.builtintypes.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import javax.inject.Inject;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wanghb
 * 一段返修
 */
@Component
public class TopPlateArriveAndLeftListener implements MonitoredDataItemListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopPlateArriveAndLeftListener.class);

    @Inject
    private RedisTemplate<String, Object> redisTemplate;

    @Inject
    private SimpMessagingTemplate template;

    @Inject
    private AsynPushDataToScreen asynPushDataToScreen;

    @Inject
    private OrderService orderService;

    @Inject
    private StationCycleTimeService stationCycleTimeService;

    @Inject
    private OpcUaClientTemplate opcUaClientTemplate;

    @Override
    public void onDataChange(MonitoredDataItem monitoredDataItem, DataValue oldValue, DataValue newValue) {
        LOGGER.info("进入监听器，变化前的值为：{}，变化后的值为：{}", new Object[]{oldValue, newValue});
        if (!OpcUaUtil.isNewNodeValueValid("PLC1", monitoredDataItem.getNodeId(), oldValue, newValue)) {
            return;
        }
        String node = monitoredDataItem.getNodeId().getValue().toString();
        //RFID信号触发
        if ("\"information\".\"Station2_ID\"".equals(node)) {
            String palletNo = ((UnsignedByte[])newValue.getValue().getValue())[0].intValue() + "";
            if ("0".equals(palletNo)) return;
            doThingsWhenArrived(palletNo);
        } else if ("\"information\".\"Station2_leave\"".equals(node)){
            if (newValue.getValue().intValue() == 0) return;
            doThingWhenLeave();
        }
    }

    private void doThingsWhenArrived(String palletNo) {
        //1、记录到位时间点
        Timestamp arrivedTime = Timestamp.from(Instant.now());

        //2、根据托盘号查看是否为空托盘，如果为空托盘什么也不做
        if (!redisTemplate.hasKey(palletNo)) {
            LOGGER.debug("****************未绑定数据的托盘号(PalletNo)为：" + palletNo + "***************************");
            return;
        };
        HashOperations<String, String, String> cacheDatas = redisTemplate.opsForHash();
        String currentOrderNo = cacheDatas.get(palletNo, "currentOrderNo");
        String nextOrderNo = cacheDatas.get(palletNo, "nextOrderNo");
        String bottomPlateBarcode = cacheDatas.get(palletNo, "bottomPlateBarcode");
        //根据下底盘条码修改上底盘的到位时间
        stationCycleTimeService.updateTopPlateStationArriveTimeByBarcode(arrivedTime, bottomPlateBarcode);
        //根据订单号查询当前工单和下一工单信息
        List<Order> orders = orderService.getOrderListByOrderNos(currentOrderNo, nextOrderNo);
        if (orders == null || orders.size() <= 0) return;
        Order currentOrder = orders.get(0);
        Order nextOrder = orders.size() == 2 ? orders.get(1) : null;
        //异步推送工艺信息到旋钮显示
        asynPushDataToScreen.queryOperationsAndPush(currentOrder, nextOrder, "TopPlate", palletNo);
//        UaClient uaClient = opcUaClientTemplate.getUaClientList().get(0);
//        NodeId checkPalletNode = new NodeId(3, "\"information\".\"Scan_DoubleCheck\"");
//        try {
//            opcUaClientTemplate.writeNodeValue(uaClient, checkPalletNode, 1);
//        } catch (OpcUaClientException e) {
//            LOGGER.error("***************第二工位写plc控制信号错误*****************");
//            e.printStackTrace();
//        }
    }

    private void doThingWhenLeave() {
        //1、记录放行的时间点
        Timestamp leaveTime = Timestamp.from(Instant.now());
        //2、通过RFID读取托盘编号
        UaClient uaClient = opcUaClientTemplate.getUaClientList().get(0);
        NodeId rfidPallet = new NodeId(3, "\"information\".\"Station2_ID\"");
        Variant readNodeVariant;
        String palletNo = "";
        try {
            readNodeVariant = opcUaClientTemplate.readNodeVariant(uaClient, rfidPallet);
            palletNo = ((UnsignedByte[])readNodeVariant.getValue())[0].intValue() + "";
        } catch (OpcUaClientException e) {
            System.out.println("********************没有读到RFID信息*************************");
            e.printStackTrace();
            return;
        }
        //2、根据托盘号查看是否为空托盘，如果为空托盘直接放行
        if (!redisTemplate.hasKey(palletNo)) return;

        //将旋钮工位的产品数量加1
        HashOperations<String, String, String> cacheDatas = redisTemplate.opsForHash();
        String orderNo = cacheDatas.get(palletNo, "currentOrderNo");
        if (orderNo == null || "".equals(orderNo)) return;
        String bottomPlateBarcode = cacheDatas.get(palletNo, "bottomPlateBarcode");
        //根据下底盘条码获取上底盘工位放行时间点，如果不为空则不将工位完成数量加1
        Timestamp topPlateLeftTime = stationCycleTimeService.getTopPlateLeftTimeByBottomPlateBarcode(bottomPlateBarcode);
        String count = cacheDatas.get(orderNo, "TopPlate");
        if (topPlateLeftTime == null) {
            count = Integer.parseInt(count) + 1 + "";
            cacheDatas.put(orderNo, "TopPlate", count);
        }
        //根据下底盘条码修改上底盘工位放行时间
        stationCycleTimeService.updateTopPlateStationCycleTimeByBarcode(leaveTime, bottomPlateBarcode);
        //将当前工位完成数量和平均时间推送到前台
        Map<String, String> countAndAverageTime = new HashMap<>();
        countAndAverageTime.put("currentFinished", count);
        //计算当前工单在上底盘工位的平均时间
        String averageTime = stationCycleTimeService.getTopPlateAverageTime(orderNo);
        countAndAverageTime.put("averageTime", averageTime);
        template.convertAndSend("/topic/TopPlate/countAndAverageTime", countAndAverageTime);
    }
}