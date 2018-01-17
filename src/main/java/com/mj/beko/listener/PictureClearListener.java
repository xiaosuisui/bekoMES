package com.mj.beko.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Created by Ricardo on 2017/8/19.
 * 事件监听器
 */
@Component
public class PictureClearListener implements ApplicationListener<PictureClearEvent> {
    @Override
    public void onApplicationEvent(PictureClearEvent demoEvent) {
        String msg =demoEvent.getMsg();
        System.out.println("监听器收到了事件的发布的消息"+msg);
    }
}
