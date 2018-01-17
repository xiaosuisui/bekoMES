package com.mj.beko.repository;

import com.mj.beko.domain.DownTimeData;
import com.mj.beko.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Ricardo on 2017/11/13.
 */
@Repository
public interface DownTimeRepository extends JpaRepository<DownTimeData, Long>,JpaSpecificationExecutor {
    @Query(value = "select top 5 * from down_time_data order by id desc ",nativeQuery = true)
    List<DownTimeData> findDownDataTopFour();
}
