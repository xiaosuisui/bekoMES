package com.mj.beko.service;

import com.mj.beko.domain.CycleTimeTarget;
import com.mj.beko.domain.DownTimeData;
import org.springframework.data.domain.Page;

/**
 * Created by Ricardo on 2017/11/14.
 */
public interface CycleTimeTargetService extends BaseService<CycleTimeTarget> {

    /**
     * 通过条件查询
     * @param page
     * @param size
     * @return
     */
    Page<CycleTimeTarget> findAllCycleTimeTargetCondition(String productNo, int page, int size);

    /**
     * 条件查询记录数
     * @return
     */

    long getAllCycleTimeTargetByCondition(String productNo);

    /**
     * 通过Id查找
     * @param id
     * @return
     */
    CycleTimeTarget findOneById(Long id);

    void  delete(Long id);
    CycleTimeTarget getCycleTimeTargetByProductId(String productId);
}
