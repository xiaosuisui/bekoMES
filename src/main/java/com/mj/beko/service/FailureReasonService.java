package com.mj.beko.service;

import com.mj.beko.domain.CycleTimeTarget;
import com.mj.beko.domain.FailureReason;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by Ricardo on 2017/11/14.
 */
public interface FailureReasonService  extends BaseService<FailureReason> {
    /**
     * 通过条件查询
     * @param page
     * @param size
     * @return
     */
    Page<FailureReason> findAllFailureReasonCondition(String workstation, int page, int size);

    /**
     * 条件查询记录数
     * @return
     */

    long getAllFailureReasonByCondition(String workstation);

    /**
     * 通过Id查找
     * @param id
     * @return
     */
    FailureReason findOneById(Long id);

    void  delete(Long id);
}
