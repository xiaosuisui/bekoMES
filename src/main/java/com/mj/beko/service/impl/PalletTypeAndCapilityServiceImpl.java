package com.mj.beko.service.impl;

import com.mj.beko.domain.PalletTypeAndCapility;
import com.mj.beko.repository.PalletTypeAndCapilityRepository;
import com.mj.beko.service.PalletTypeAndCapilityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Ricardo on 2017/12/22.
 */
@Service
@Slf4j
public class PalletTypeAndCapilityServiceImpl implements PalletTypeAndCapilityService {
    @Autowired
    private PalletTypeAndCapilityRepository palletTypeAndCapilityRepository;
    @Override
    public PalletTypeAndCapility getPalletTypeAndCapilitiesByWorkStation(String station) {
        log.info("get current station pallet capility");
        return palletTypeAndCapilityRepository.getPalletTypeAndCapilitiesByWorkStation(station);
    }
}
