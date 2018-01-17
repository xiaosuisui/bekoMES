package com.mj.beko.repository;

import com.mj.beko.domain.MesToFlowTestRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Created by Ricardo on 2017/12/13.
 * write data to flowTestPlc
 */
@Repository
public interface MesToFlowTestTypeRepository extends JpaRepository<MesToFlowTestRange, Long> {

    @Query(value = "select * from mes_to_flow_test_range where product_id=:productId", nativeQuery = true)
    MesToFlowTestRange getMesToFlowTestTypeByProductId(@Param("productId") String productId);
}