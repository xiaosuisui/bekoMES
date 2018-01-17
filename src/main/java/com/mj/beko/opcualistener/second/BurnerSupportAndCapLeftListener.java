package com.mj.beko.opcualistener.second;

import com.mj.beko.opcua.OpcUaClientException;
import com.mj.beko.opcua.OpcUaClientTemplate;
import com.mj.beko.opcua.OpcUaUtil;
import com.mj.beko.service.AutomaticStationTimesService;
import com.mj.beko.service.StationCycleTimeService;
import com.prosysopc.ua.client.MonitoredDataItem;
import com.prosysopc.ua.client.MonitoredDataItemListener;
import com.prosysopc.ua.client.UaClient;
import org.opcfoundation.ua.builtintypes.DataValue;
import org.opcfoundation.ua.builtintypes.NodeId;
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
import java.util.Map;

/**
 * @author wanghb
 * 二段7个工位的放行信号
 */
@Component
public class BurnerSupportAndCapLeftListener implements MonitoredDataItemListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(BurnerSupportAndCapLeftListener.class);

    private static final String BURNER_SUPPORT1_LEAVE = "\"OPCOA\".\"1BSWorkOver\"";
    private static final String BURNER_SUPPORT2_LEAVE = "\"OPCOA\".\"2BSWorkOver\"";
    private static final String BURNER_SUPPORT3_LEAVE = "\"OPCOA\".\"3BSWorkOver\"";
    private static final String BURNER_SUPPORT4_LEAVE = "\"OPCOA\".\"4BSWorkOver\"";

    private static final String KNOBS_LEAVE = "\"OPCOA\".\"KnobsWorkOver\"";

    private static final String BURNER_CAP1_LEAVE = "\"OPCOA\".\"1BCWorkOver\"";
    private static final String BURNER_CAP2_LEAVE = "\"OPCOA\".\"2BCWorkOver\"";

    private static final String BURNER_SUPPORT1_EMPTY = "\"OPCOA\".\"1BSProductionOn\"";
    private static final String BURNER_SUPPORT2_EMPTY = "\"OPCOA\".\"2BSProductionOn\"";
    private static final String BURNER_SUPPORT3_EMPTY = "\"OPCOA\".\"3BSProductionOn\"";
    private static final String BURNER_SUPPORT4_EMPTY = "\"OPCOA\".\"4BSProductionOn\"";

    private static final String BURNER_CAP1_EMPTY = "\"OPCOA\".\"1BCProductionOn\"";
    private static final String BURNER_CAP2_EMPTY = "\"OPCOA\".\"2BCProductionOn\"";

    private static final String BURNER_SUPPORT1_RFID = "\"OPCOA\".\"1_Robot_PalletCode\"";
    private static final String BURNER_SUPPORT2_RFID = "\"OPCOA\".\"2_Robot_PalletCode\"";
    private static final String BURNER_SUPPORT3_RFID = "\"OPCOA\".\"3_Robot_PalletCode\"";
    private static final String BURNER_SUPPORT4_RFID = "\"OPCOA\".\"4_Robot_PalletCode\"";

    private static final String KNOBS_RFID = "\"OPCOA\".\"KnobStation_PalletCode\"";

    private static final String BURNER_CAP1_RFID = "\"OPCOA\".\"5_Robot_PalletCode\"";
    private static final String BURNER_CAP2_RFID = "\"OPCOA\".\"6_Robot_PalletCode\"";

    @Inject
    private OpcUaClientTemplate opcUaClientTemplate;

    @Inject
    private RedisTemplate<String, Object> redisTemplate;

    @Inject
    private SimpMessagingTemplate template;

    @Inject
    private AutomaticStationTimesService automaticStationTimesService;

    @Inject
    private StationCycleTimeService stationCycleTimeService;

    @Override
    public void onDataChange(MonitoredDataItem monitoredDataItem, DataValue oldValue, DataValue newValue) {
        LOGGER.info("进入监听器，变化前的值为：{}，变化后的值为：{}", new Object[]{oldValue, newValue});
        if (!OpcUaUtil.isNewNodeValueValid("PLC2", monitoredDataItem.getNodeId(), oldValue, newValue)
                || newValue.getValue().intValue() == 0) {
            return;
        }
        String node = monitoredDataItem.getNodeId().getValue().toString();
        switch (node) {
            case BURNER_SUPPORT1_LEAVE:
                doThingWhenLeave(BURNER_SUPPORT1_RFID, "BurnerSupport1");
                break;
            case BURNER_SUPPORT2_LEAVE:
                doThingWhenLeave(BURNER_SUPPORT2_RFID, "BurnerSupport2");
                break;
            case BURNER_SUPPORT3_LEAVE:
                doThingWhenLeave(BURNER_SUPPORT3_RFID, "BurnerSupport3");
                break;
            case BURNER_SUPPORT4_LEAVE:
                doThingWhenLeave(BURNER_SUPPORT4_RFID, "BurnerSupport4");
                break;
            case KNOBS_LEAVE:
                doThingWhenKnobLeave(KNOBS_RFID, "Knobs");
                break;
            case BURNER_CAP1_LEAVE:
                doThingWhenLeave(BURNER_CAP1_RFID, "BurnerCap1");
                break;
            case BURNER_CAP2_LEAVE:
                doThingWhenLeave(BURNER_CAP2_RFID, "BurnerCap2");
                break;
            default:
                System.out.println("**********节点错误**********");
                break;
        }
    }

    private void doThingWhenLeave(String rfidNode, String stationName){
        //1、记录放行的时间点
        Timestamp leaveTime = Timestamp.from(Instant.now());
        //2、读取是否为空托盘节点
        UaClient uaClient = opcUaClientTemplate.getUaClientList().get(1);
        NodeId rfidPallet = new NodeId(3, rfidNode);
        boolean emptyOrNot = false;
        String palletNo = "";
        try {
            Variant readNodeVariant = opcUaClientTemplate.readNodeVariant(uaClient, rfidPallet);
            palletNo = readNodeVariant.intValue() + "";
        } catch (OpcUaClientException e) {
            e.printStackTrace();
        }
        //4、如果不是空托盘，则记录放行时间点
        if (redisTemplate.hasKey(palletNo)){
            automaticStationTimesService.updateLeaveTimeByStation(stationName, leaveTime);
        }
    }

    private void doThingWhenKnobLeave(String rfidNode, String stationName){
        //1、记录放行的时间点
        Timestamp leaveTime = Timestamp.from(Instant.now());

        //2、通过RFID读取托盘编号
        UaClient uaClient = opcUaClientTemplate.getUaClientList().get(1);
        NodeId rfidPallet = new NodeId(3, rfidNode);
        String palletNo = "";
        try {
            Variant readNodeVariant = opcUaClientTemplate.readNodeVariant(uaClient, rfidPallet);
            palletNo = readNodeVariant.intValue() + "";
        } catch (OpcUaClientException e) {
            e.printStackTrace();
        }

        //3、根据托盘号查看是否为空托盘,如果是空托盘直接放行
        if (!redisTemplate.hasKey(palletNo)) return;
        //将旋钮工位的产品数量加1
        HashOperations<String, String, String> cacheDatas = redisTemplate.opsForHash();
        String orderNo = cacheDatas.get(palletNo, "currentOrderNo");
        String bottomPlateBarcode = cacheDatas.get(palletNo, "bottomPlateBarcode");
        //根据下底盘条码获取旋钮工位放行时间点，如果不为空则不将工位完成数量加1
        Timestamp knobLeftTime = stationCycleTimeService.getKnobLeftTimeByBottomPlateBarcode(bottomPlateBarcode);
        String count = cacheDatas.get(orderNo, "Knobs");
        if (knobLeftTime == null) {
            count = Integer.parseInt(count) + 1 + "";
            cacheDatas.put(orderNo, "Knobs", count);
        }
        //将放行时间点记录到数据库中
        stationCycleTimeService.updateKnobStationCycleTimeByBarcode(leaveTime, bottomPlateBarcode);
        //将当前工位完成数量和平均时间推送到前台
        Map<String, String> countAndAverageTime = new HashMap<>();
        countAndAverageTime.put("currentFinished", count);
        //计算当前工单在旋钮工位的平均时间
        String averageTime = stationCycleTimeService.getKnobAverageTime(orderNo);
        countAndAverageTime.put("averageTime", averageTime);
        template.convertAndSend("/topic/" + stationName + "/countAndAverageTime", countAndAverageTime);
    }
}