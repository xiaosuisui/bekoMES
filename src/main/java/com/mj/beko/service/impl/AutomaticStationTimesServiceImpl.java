package com.mj.beko.service.impl;

import com.mj.beko.domain.AutomaticStationTimes;
import com.mj.beko.repository.AutomaticStationTimesRepository;
import com.mj.beko.service.AutomaticStationTimesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

/**
 * @author wanghb
 */
@Service
@Transactional
@Slf4j
public class AutomaticStationTimesServiceImpl implements AutomaticStationTimesService {

    @Autowired
    private AutomaticStationTimesRepository automaticStationTimesRepository;

    /**
     * 保存到位时间点
     * @param automaticStationTimes
     */
    @Override
    public void save(AutomaticStationTimes automaticStationTimes) {
        automaticStationTimesRepository.save(automaticStationTimes);
    }


    /**
     * 根据工位名称修改最新一条的放行时间和cycleTime
     * @param station
     * @param leaveTime
     */
    @Override
    public void updateLeaveTimeByStation(String station, Timestamp leaveTime) {
        automaticStationTimesRepository.updateLeaveTimeByStation(station, leaveTime);
    }
}
