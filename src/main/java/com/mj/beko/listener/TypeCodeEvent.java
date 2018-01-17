package com.mj.beko.listener;

import lombok.Data;
import org.springframework.context.ApplicationEvent;

/**
 * Created by Ricardo on 2018/1/14.
 */
@Data
public class TypeCodeEvent extends ApplicationEvent {
    private String msg;
    public TypeCodeEvent(Object source , String msg){
        super(source);
        this.msg=msg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        TypeCodeEvent that = (TypeCodeEvent) o;

        return msg.equals(that.msg);
    }
    @Override
    public int hashCode() {
        return msg.hashCode();
    }
}
