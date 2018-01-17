package com.mj.beko.database;

import java.lang.annotation.*;

/**
 * Created by xiaosui on 2017/6/27.
 * 自定义注解,指定数据源
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TargetDataSource {
    String value();
}
