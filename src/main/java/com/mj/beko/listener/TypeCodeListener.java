package com.mj.beko.listener;

import org.springframework.context.ApplicationListener;

/**
 * Created by Ricardo on 2018/1/14.
 */
public class TypeCodeListener implements ApplicationListener<TypeCodeEvent> {
    @Override
    public void onApplicationEvent(TypeCodeEvent event) {
        String msg =event.getMsg();
        System.out.println("监听器收到了事件的发布的消息"+msg);
    }
}
