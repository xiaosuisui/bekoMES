package com.mj.beko.service;

import com.mj.beko.domain.MesToFlowTestRange;

/**
 * Created by Ricardo on 2017/12/13.
 */
public interface MesToFlowTestService {

    MesToFlowTestRange getFlowStepRangeValue(String productId);
}