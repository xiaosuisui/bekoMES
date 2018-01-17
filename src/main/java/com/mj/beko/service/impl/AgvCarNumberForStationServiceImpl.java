package com.mj.beko.service.impl;

import com.mj.beko.domain.AgvCarNumberForStation;
import com.mj.beko.repository.AgvCarNumberForStationRepository;
import com.mj.beko.service.AgvCarNumberForStationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Created by Ricardo on 2017/12/21.
 */
@Service
@Transactional
@Slf4j
public class AgvCarNumberForStationServiceImpl implements AgvCarNumberForStationService {
    @Autowired
    private AgvCarNumberForStationRepository agvCarNumberForStationRepository;
    @Override
    public AgvCarNumberForStation getAgvCarNumberForStationByOrderNoAndStation(String station) {
        log.info("get current station pallet capility,",station);
        return agvCarNumberForStationRepository.getAgvCarNumberForStationByOrderNoAndStation(station);
    }
}
