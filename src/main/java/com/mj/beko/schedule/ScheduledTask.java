package com.mj.beko.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ricardo on 2017/8/11.
 */
@Component
@Slf4j
public class ScheduledTask {

    @Inject
    private ScheduleGetProductPlanAndOperation scheduleGetProductPlanAndOperation;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    public void getTask1() {
        System.out.println("任务1,从配置文件加载任务信息，当前时间：" + dateFormat.format(new Date()));
//        log.info("************开始获取订单计划**************");
//        scheduleGetProductPlanAndOperation.getProductPlan();
    }
    public void getTask2(){System.out.println("任务2。。。。。");}

    public void getProductPlan(){
        log.info("************开始获取订单计划**************");
        scheduleGetProductPlanAndOperation.getProductPlan();
    }
}
