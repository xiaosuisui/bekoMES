package com.mj.beko.service;

import com.mj.beko.domain.PalletTypeAndCapility;
import org.springframework.stereotype.Service;

/**
 * Created by Ricardo on 2017/12/22.
 */

public interface PalletTypeAndCapilityService {
    /**
     * 工位查询每个工位的标准的托盘的生产数量
     * @param station
     * @return
     */
    PalletTypeAndCapility getPalletTypeAndCapilitiesByWorkStation(String station);
}
