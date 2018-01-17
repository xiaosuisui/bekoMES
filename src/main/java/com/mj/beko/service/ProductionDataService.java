package com.mj.beko.service;

import com.mj.beko.domain.ProductionData;
import com.mj.beko.domain.TestStationData;
import org.springframework.data.domain.Page;

/**
 * Created by Ricardo on 2017/11/11.
 */
public interface ProductionDataService {
    /**
     * 保存
     * @param productionData
     * @return
     */
    ProductionData save(ProductionData productionData);

    /**
     * 通过Id查
     * @param id
     * @return
     */
    ProductionData findOne(Long id);
    /**
     * 分页查询条件返修记录
     * @param productNo
     * @param page
     * @param size
     * @return
     */
    Page<ProductionData> findAllProductionDataByPageAndCondition(String productNo, String barCode, int page, int size);

    /**
     * 条件查询总记录数
     * @param productNo
     * @param barCode
     * @return
     */
    long getAllCountByCondition(String productNo,String barCode);
}
