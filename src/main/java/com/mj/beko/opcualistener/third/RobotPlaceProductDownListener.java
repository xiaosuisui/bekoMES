package com.mj.beko.opcualistener.third;

import com.mj.beko.opcua.OpcUaClientException;
import com.mj.beko.opcua.OpcUaClientTemplate;
import com.mj.beko.opcua.OpcUaUtil;
import com.prosysopc.ua.client.MonitoredDataItem;
import com.prosysopc.ua.client.MonitoredDataItemListener;
import com.prosysopc.ua.client.UaClient;
import lombok.extern.slf4j.Slf4j;
import org.opcfoundation.ua.builtintypes.DataValue;
import org.opcfoundation.ua.builtintypes.NodeId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ricardo on 2018/1/6.
 * Robot place product down
 */
@Slf4j
@Component
public class RobotPlaceProductDownListener implements MonitoredDataItemListener {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private OpcUaClientTemplate opcUaClientTemplate;
    @Inject
    private SimpMessagingTemplate template;

    @Override
    public void onDataChange(MonitoredDataItem monitoredDataItem, DataValue oldValue, DataValue newValue) {
        log.info("read robot place product for match bottomPlate,");
        if (!OpcUaUtil.isNewNodeValueValid("PLC3", monitoredDataItem.getNodeId(), oldValue, newValue)
                || newValue.getValue().intValue() == 0) {
            return;
        }
        //read cache
        readMatchBottomPlateBarCode();
    }
    public void readMatchBottomPlateBarCode(){
        //get bottomPlate from cache
        ValueOperations valueOperations=redisTemplate.opsForValue();
        ListOperations listOperations=redisTemplate.opsForList();
        String matchBottomPlateCode=valueOperations.get("bottomPlateBarCode").toString();
        log.info("have receive robot info,cache barcode value is{}",matchBottomPlateCode);
        valueOperations.set("matchBottomPlateCode",matchBottomPlateCode);
        listOperations.leftPush("cacheBottomValue",matchBottomPlateCode);
        Map<String,String> infoMap =new HashMap<String,String>();
        infoMap.put("matchBottomPlateCode",matchBottomPlateCode);
        //把接收到的信号实施的抛出来
        template.convertAndSend("/topic/lineLeaderScreen/matchBottomPlateCode",infoMap);
        //设置完成后,写值给plc确认已经抓取到了机器人信号
        //给机器人写一个信号,hi哥们你可以继续工作了。
        UaClient uaClient = opcUaClientTemplate.getUaClientList().get(2);
        log.info("start write node value plc tell reobot you can go is{}",matchBottomPlateCode);
        NodeId printer01Node = new NodeId(3, "\"ITread\".\"Match_ok\"");
        try {
            //如果发成功了,则给plc信号,hi 你可以抓取了。往节点写1值
            //写2次
            boolean flag1 = opcUaClientTemplate.writeNodeValue(uaClient, printer01Node, 1);
            if(!flag1){
                log.info("write second write node value to plc,robot can go scanner Complate,bottomPlate{}",matchBottomPlateCode);
                boolean flag2 = opcUaClientTemplate.writeNodeValue(uaClient, printer01Node, 1);
            }
            log.info("write successful write node value to plc,robot can go,bottomPlate is {}",matchBottomPlateCode);

        }catch (Exception e){
            try {
                //如果报异常了，在写一次
                boolean flag3 = opcUaClientTemplate.writeNodeValue(uaClient, printer01Node, 1);
            } catch (OpcUaClientException e1) {
                e1.printStackTrace();
            }
            log.error("write plc to tell robot can go,{}");
        }
    }
}
