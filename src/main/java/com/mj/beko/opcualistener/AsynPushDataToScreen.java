package com.mj.beko.opcualistener;

import com.mj.beko.domain.Operation;
import com.mj.beko.domain.Order;
import com.mj.beko.domain.Workstation;
import com.mj.beko.service.OperationService;
import com.mj.beko.service.WorkstationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by Administrator on 2017/11/16.
 */
@Slf4j
@Component
public class AsynPushDataToScreen {

    @Inject
    private WorkstationService workstationService;

    @Inject
    private OperationService operationService;

    @Inject
    private SimpMessagingTemplate template;

    /**
     * 异步推送工艺信息、当前工单、下一工单和托盘号到屏幕上显示
     */
    @Async("taskExecutor")
    public void queryOperationsAndPush (Order currentOrder, Order nextOrder, String stationName, String palletNo) {
        String stationNm = stationName;
        if (stationName.contains("FireTest")) {
            stationNm = "FireTest1";
        }
        //根据工位名获取工位信息
        Workstation workstation = workstationService.getWorkstationByStationName(stationNm);
        List<Operation> operationList = operationService.getOperationByProductNoAndWorkstationId(currentOrder.getProductNo(), workstation.getId());
        double targetTime = 0.0000;
        for (Operation op : operationList){
            targetTime += Double.parseDouble(op.getOperationDuration());
        }
        template.convertAndSend("/topic/" + stationName + "/targetTime", targetTime);
        template.convertAndSend("/topic/" + stationName + "/operation", operationList);
        template.convertAndSend("/topic/" + stationName + "/palletNo", palletNo);
        pushCurrentAndNextOrder(currentOrder, nextOrder, stationName);
    }

    @Async("taskExecutor")
    public void pushCurrentAndNextOrder(Order currentOrder, Order nextOrder, String stationName){
        //推送当前工单和下一工单信息
        template.convertAndSend("/topic/" + stationName + "/currentOrder", currentOrder);
        template.convertAndSend("/topic/" + stationName + "/nextOrder", nextOrder);
    }
}
