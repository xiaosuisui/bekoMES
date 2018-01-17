package com.mj.beko.service.impl;

import com.mj.beko.domain.MesToFlowTestRange;
import com.mj.beko.repository.MesToFlowTestTypeRepository;
import com.mj.beko.service.MesToFlowTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;

/**
 * Created by Ricardo on 2017/12/13.
 */
@Service
@Transactional
public class MesToFlowTestServiceImpl implements MesToFlowTestService {

    @Autowired
    private MesToFlowTestTypeRepository mesToFlowTestTypeRepository;

    @Override
    public MesToFlowTestRange getFlowStepRangeValue(String productId) {
        return mesToFlowTestTypeRepository.getMesToFlowTestTypeByProductId(productId);
    }
}
