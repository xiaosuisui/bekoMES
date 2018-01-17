package com.mj.beko.util;

import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ricardo on 2017/8/17.
 * 获取当前的时间
 */
@Slf4j
public class DateTimeFormatUtil {
    public static Timestamp getCurrentDateTime(){
        log.info(new Timestamp(new Date().getTime()).toString());
        return new Timestamp(new Date().getTime());
    }
}
