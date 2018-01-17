package com.mj.beko.database;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaosui on 2017/6/27.
 * 定义动态数据源的上下文
 */
public class DynamicDataSourceContextHolder {
    /*ThreadLocal维护变量*/
    private static final ThreadLocal<String> contextHolder = new ThreadLocal<String>();
    /*管理所有的数据源的Id*/
    public static List<String> dataSourceIds = new ArrayList<String>();
    public static void setDataSourceType(String dataSourceType) {
        contextHolder.set(dataSourceType);
    }

    public static String getDataSourceType() {
        return contextHolder.get();
    }
    public static void clearDataSourceType() {
        contextHolder.remove();
    }
    /*判断当前的数据库是否存在*/
    public static boolean containsDataSource(String dataSourceId){
        return dataSourceIds.contains(dataSourceId);
    }
}
