package com.mj.beko.service;

import com.mj.beko.domain.ProductBrokenData;

/**
 * Created by Ricardo on 2018/1/13.
 */
public interface ProductBrokenDataService {
    /**
     * 通过下底盘条码查询产品的坏件数量
     * @param bottomPlateBarCode
     * @return
     */
    ProductBrokenData getProductBrokenDataByBottomPlateCode(String bottomPlateBarCode);
}
