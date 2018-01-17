package com.mj.beko.service.ApiService;

import com.mj.beko.domain.ProductRepair;

/**
 * Created by Ricardo on 2017/11/22.
 * 返修工位的相关的跟tablet和screen交互的Api
 */
public interface RepairStationApiService {
    /**
     * 返修工位的托盘号的推送
     * @param palletNo
     */
    void pushPalletNoToRepairStation01(String palletNo);
    /**
     * 通过palletNo 和 reason 生成一条返修记录
     * @param palletNo
     * @param reason
     * @return
     */
     void createOneProductRepair(String palletNo,String reason,String status);

}
