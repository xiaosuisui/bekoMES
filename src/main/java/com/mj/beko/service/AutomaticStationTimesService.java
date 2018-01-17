package com.mj.beko.service;

import com.mj.beko.domain.AutomaticStationTimes;

import java.sql.Timestamp;

/**
 * @author wanghb
 */
public interface AutomaticStationTimesService {

    /**
     * 保存到位时间点
     * @param automaticStationTimes
     */
    void save(AutomaticStationTimes automaticStationTimes);

    /**
     * 根据工位名称修改最新一条的放行时间和cycleTime
     * @param station
     * @param leaveTime
     */
    void updateLeaveTimeByStation(String station, Timestamp leaveTime);
}
