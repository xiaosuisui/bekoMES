package com.mj.beko.service.ApiService;

/**
 * Created by Ricardo on 2017/11/22.
 */
public interface BurnStationApiService {
    /**
     * 托盘号推送 burn station
     * @param palletNo
     */
    void pushPalletNoToBurnStationLeft(String palletNo);
    void pushPalletNoToBurnStationRight(String palletNo);
}
