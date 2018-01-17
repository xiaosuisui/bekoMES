package com.mj.beko.service.ApiService;

/**
 * Created by Ricardo on 2017/12/14.
 * 计算每小时的标准产量
 */
public interface CountStandOutputService {
    //shiftName(前端传入,跟当前一体机保持一致,)
    Object getCountStandOutputTimeRange(String ShiftName);
}
