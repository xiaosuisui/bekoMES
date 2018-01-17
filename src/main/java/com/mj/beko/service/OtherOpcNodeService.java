package com.mj.beko.service;

import com.mj.beko.opcua.OpcUaClientException;
import com.mj.beko.opcua.OpcUaClientTemplate;
import com.mj.beko.tcs.InitTcsOrderService;
import com.prosysopc.ua.client.UaClient;
import lombok.extern.slf4j.Slf4j;
import org.opcfoundation.ua.builtintypes.NodeId;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;

/**
 * @author wanghb
 */
@Slf4j
@Service
public class OtherOpcNodeService {

    @Inject
    private OpcUaClientTemplate opcUaClientTemplate;

    @Inject
    private InitTcsOrderService initTcsOrderService;

    private static int oldValue = -1;

    /**
     * 二段旋钮工位箱子缺料和到料
     */
    @Scheduled(cron = "0/5 * * * * ?")
    public void knobsBoxLackOfMaterialAndArriveListener(){
        UaClient uaClient = opcUaClientTemplate.getUaClientList().get(1);
        NodeId knobBoxEmptyNode = new NodeId(3, "\"OPCOA\".\"KnobBox_Empty\"");
            try {
                int newValue = opcUaClientTemplate.readNodeVariant(uaClient, knobBoxEmptyNode).intValue();
                if (oldValue == 0 && newValue == 1) {
                    initTcsOrderService.createTcsOrderSet("LIULIJIA", "LIULIJIA");
                }
                oldValue = newValue;
            } catch (OpcUaClientException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
