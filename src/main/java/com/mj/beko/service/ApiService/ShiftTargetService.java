package com.mj.beko.service.ApiService;

import com.mj.beko.domain.OperatorShift;

import java.util.Map;

/**
 * Created by Ricardo on 2017/12/9.
 */
public interface ShiftTargetService {
    //获取目标生产数量
    Map<String,String> getTargetQuantityAndShiftName();
    Map<String,String> getCurrentShift(OperatorShift operatorShift);

}
