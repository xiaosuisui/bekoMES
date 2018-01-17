package com.mj.beko.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ricardo on 2017/8/11.
 */
@Component
public class DynamicScheduledTask2 implements SchedulingConfigurer {
    @Autowired
    private ScheduledTask scheduledTask;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private String cron ="0/5 * * * * ?";
    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.addTriggerTask(new Runnable() {
            @Override
            public void run() {
                scheduledTask.getTask2();
            }
        }, new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                CronTrigger trigger =new CronTrigger(cron);
                Date nextExecDate = trigger.nextExecutionTime(triggerContext);
                return nextExecDate;
            }
        });
    }
    public void setCron(String cron){
        this.cron=cron;
    }
}
