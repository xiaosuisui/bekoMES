package com.mj.beko.repository;

import com.mj.beko.domain.OperatorShift;
import com.mj.beko.domain.TcsOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by Ricardo on 2017/12/1.
 */
public interface ShiftRepository extends JpaRepository<OperatorShift, Long>,JpaSpecificationExecutor {
    /**
     * 获取当前正在执行的shift制度
     * @return
     */
    @Query(value = "select  top 1 * from t_shift where active = 'true' order by id desc ",nativeQuery = true)
    OperatorShift getCurrentShift();

}
