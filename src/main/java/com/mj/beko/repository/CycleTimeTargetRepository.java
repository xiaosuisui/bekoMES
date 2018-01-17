package com.mj.beko.repository;

import com.mj.beko.domain.CycleTimeTarget;
import com.mj.beko.domain.OperatorLoginData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Created by Ricardo on 2017/11/14.
 */
public interface CycleTimeTargetRepository extends JpaRepository<CycleTimeTarget,Long>,JpaSpecificationExecutor {
    /**
     * 通过产品Id查询对应的每小时的生产数量
     * @param productId
     * @return
     */
    @Query(value = "select top 1 * from cycle_time_target where product_no=:productId",nativeQuery = true)
    CycleTimeTarget getCycleTimeTargetByProductNo(@Param("productId") String productId);

}
