package com.mj.beko.util;

/**
 * Created by Ricardo on 2017/8/19.
 * 判断字符串是否为空
 */
public class StrOperatorUtil {
    public static boolean strIsBlank(String str){
        return str==null||"".equals(str)||"null".equals(str)||str.length()<0||str.isEmpty();
    }
    public static  boolean strIsNotBlank(String str){
        return str!=null && str!=""&&str.length()>0;
    }
}
