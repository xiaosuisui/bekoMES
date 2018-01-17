package com.mj.beko.service;

import com.mj.beko.domain.ProductCode;
import com.mj.beko.domain.TcsOrder;
import org.springframework.data.domain.Page;

/**
 * Created by Ricardo on 2017/8/24.
 */
public interface ProductCodeService extends BaseService<ProductCode> {
    String getAllCountProductCode();
    /**
     * 通过条件查询
     * @param productNo
     * @param orderNo
     * @param page
     * @param size
     * @return
     */
    Page<ProductCode> findAllByPageAndCondition(String productNo, String orderNo, int page, int size);

    /**
     * 条件查询记录数
     * @param productNo
     * @param orderNo
     * @return
     */

    long getAllCountByCondition(String productNo,String orderNo);

    /**
     * eps条码查找productCode
     * @param epsCode
     * @return
     */
    ProductCode getProductCodeByEpsCode(String epsCode);

    /**
     * 根据状态查询最老的一条记录
     * @param status
     * @return
     */
    ProductCode getOldProductCodeByStatus(String status);

    ProductCode getOldProductCodeOnlyByStatus(String status);
    //通过下底盘条码查询是否有相同的bottomPlateBarCode
    ProductCode getProductCodeByBottomPlateBarCode(String bottomPlateBarCode);
    //通过eps条码查找productCode中是否有相同的记录(status 1 or status =2),如果有，则直接放行。

    ProductCode getProductCodeByEpsCodeAndStatus(String epsCode);
    ProductCode getProductCodeByBottomPlateCode(String bottomPlateBarCode);
}
