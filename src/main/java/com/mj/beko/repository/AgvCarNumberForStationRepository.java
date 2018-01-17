package com.mj.beko.repository;

import com.mj.beko.domain.AgvCarNumberForStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Created by Ricardo on 2017/12/21.
 */
@Repository
public interface AgvCarNumberForStationRepository extends JpaRepository<AgvCarNumberForStation,Long>,JpaSpecificationExecutor {
    /**
     *通过station和orderNo 查询对应的实体
     * @return
     */
    @Query(value = "select top 1 * from t_agv_car_station_number where station=:station order by id desc",nativeQuery = true)
    AgvCarNumberForStation getAgvCarNumberForStationByOrderNoAndStation(@Param("station") String station);
}

