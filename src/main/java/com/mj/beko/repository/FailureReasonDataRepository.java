package com.mj.beko.repository;

import com.mj.beko.domain.FailureReasonData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by Ricardo on 2017/11/16.
 */
@Repository
public interface FailureReasonDataRepository extends JpaRepository<FailureReasonData, Long>,JpaSpecificationExecutor {

    /**
     * 根据下底盘条码和工位名称查询记录数
     * @param bottomPlateBarcode
     * @param stationName
     * @return
     */
    @Query(value = "select count(0) from failure_reason_data where bar_code = ?1 and workstation = ?2", nativeQuery = true)
    int getCountByBottomPlateBarcodeAndStation(String bottomPlateBarcode, String stationName);
}
