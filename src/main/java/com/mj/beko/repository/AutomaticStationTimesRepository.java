package com.mj.beko.repository;

import com.mj.beko.domain.AutomaticStationTimes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

/**
 * @author wanghb
 */
@Repository
public interface AutomaticStationTimesRepository extends JpaRepository<AutomaticStationTimes, Long>{
    /**
     * 根据工位名称修改最新一条的放行时间和cycleTime
     * @param station
     * @param leaveTime
     */
    @Modifying
    @Query(value = "update automatic_station_times " +
                      "set leave_time = ?2, " +
                           "cycle_time = DATEDIFF(S, arrive_time, ?2) " +
                    "where id = (" +
                                    "select top 1 ast.id " +
                                      "from automatic_station_times ast " +
                                     "where ast.station = ?1 " +
                                     "order by ast.id desc" +
                                 ")", nativeQuery = true)
    void updateLeaveTimeByStation(String station, Timestamp leaveTime);
}
