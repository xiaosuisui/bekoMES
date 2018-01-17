package com.mj.beko.service.ApiService;

/**
 * Created by Ricardo on 2017/12/12.
 * 动态计算当前时间应该生产的数量
 */
public interface CurrentShiftService {
    /*每2分钟动态计算数据*/
    int getCurrentShiftNumber();
    /**
     * 判断当前的shift是否结束
     * @param sfhitName
     * @return
     */
     String getResultIfShiftCanEnd(String sfhitName,String date);

}
