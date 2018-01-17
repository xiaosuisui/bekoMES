package com.mj.beko.service.ApiService;

/**
 * Created by Ricardo on 2017/11/22.
 */
public interface KnobeStationApiService {
    /**
     * 旋钮工位的托盘号的推送
     * @param palletNo
     */
    void pushPalletNoToKnobeStation(String palletNo);
}
