package com.mj.beko.listener;

import lombok.Data;
import org.springframework.context.ApplicationEvent;


/**
 * Created by Ricardo on 2017/8/19.
 * 自定义事件监听
 */
@Data
public class PictureClearEvent extends ApplicationEvent {
    private String msg;
    public PictureClearEvent(Object source , String msg){
        super(source);
        this.msg=msg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PictureClearEvent that = (PictureClearEvent) o;

        return msg.equals(that.msg);
    }
    @Override
    public int hashCode() {
        return msg.hashCode();
    }
}
