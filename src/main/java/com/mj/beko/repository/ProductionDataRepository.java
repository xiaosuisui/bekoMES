package com.mj.beko.repository;

import com.mj.beko.domain.ProductionData;
import com.mj.beko.domain.TestStationData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Created by Ricardo on 2017/11/11.
 */
@Repository
public interface ProductionDataRepository extends JpaRepository<ProductionData, Long>,JpaSpecificationExecutor {
}
