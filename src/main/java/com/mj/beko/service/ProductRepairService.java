package com.mj.beko.service;

import com.mj.beko.domain.ProductCode;
import com.mj.beko.domain.ProductRepair;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by Administrator on 2017/10/23/023.
 */
public interface ProductRepairService {
    /*
       获取所有记录
    */
    String getAllCountProductRepair();

    List<ProductRepair> query();

    List<ProductRepair> queryByPage(int page,int size);

    /**
     * 分页查询条件返修记录
     * @param productNo
     * @param page
     * @param size
     * @return
     */
    Page<ProductRepair> findAllByPageAndCondition(String productNo,int page,int size);

    /**
     * 条件查询记录数
     * @param productNo
     * @return
     */

    long getAllCountByCondition(String productNo);
    ProductRepair save(ProductRepair productRepair);

    /**
     * 根据下底盘条码和State查询返修记录数
     * @param bottomPlateBarcode
     * @param state
     * @return
     */
    int getCountByBarvodeAndState(String bottomPlateBarcode, String state);
    /**
     * 通过下底盘条码查询
     */
    ProductRepair getProductRepairByBottomBarCode(String bottomPlateBarCode,String state);
}
