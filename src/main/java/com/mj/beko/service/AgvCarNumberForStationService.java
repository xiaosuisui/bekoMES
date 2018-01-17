package com.mj.beko.service;

import com.mj.beko.domain.AgvCarNumberForStation;

/**
 * Created by Ricardo on 2017/12/21.
 */
public interface AgvCarNumberForStationService {
    AgvCarNumberForStation getAgvCarNumberForStationByOrderNoAndStation(String station);
}
