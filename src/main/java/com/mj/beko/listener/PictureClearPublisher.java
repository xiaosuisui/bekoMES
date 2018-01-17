package com.mj.beko.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Created by Ricardo on 2017/8/19.
 */
@Component
public class PictureClearPublisher {
    @Autowired
    private ApplicationContext applicationContext;
    public void publish(String msg){
        applicationContext.publishEvent(new PictureClearEvent(this,msg));
    }
}
