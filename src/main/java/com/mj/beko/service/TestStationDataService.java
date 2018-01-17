package com.mj.beko.service;

import com.mj.beko.domain.TestStationData;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by Ricardo on 2017/11/11.
 */
public interface TestStationDataService {
    /**
     * 保存
     * @param testStationData
     * @return
     */
    TestStationData save(TestStationData testStationData);

    /**
     * 通过Id查
     * @param id
     * @return
     */
    TestStationData findOne(Long id);

    /**
     * 分页查询条件返修记录
     * @param productNo
     * @param page
     * @param size
     * @return
     */
    Page<TestStationData> findAllTestDataByPageAndCondition(String productNo,String barCode, int page, int size);

    /**
     * 条件查询总记录数
     * @param productNo
     * @param barCode
     * @return
     */
    long getAllCountByCondition(String productNo,String barCode);

    /**
     * 根据下底盘条码获取最近一次的气密检测结果为"NOK"的数量
     * @param bottomPlaceCode
     * @return
     */
    int getAirtightNokCountByBottomPlaceCode(String bottomPlaceCode);

    /**
     * 根据下底盘条码获取最近一次的流量检测结果为"NOK"的数量
     * @param bottomPlaceCode
     * @return
     */
    int getFluxNokCountByBottomPlaceCode(String bottomPlaceCode);

    /**
     * 通过下底盘条码查询测试工位中该条码不合格的记录
     * @param barCode
     * @return
     */
    List<TestStationData> getTestNOKDataByBarcode(String barCode);
    /**
     * 查询测试工位中的合格的标识符
     * @param barCode
     * @param type
     * @return
     */
    List<TestStationData> getDiffTestStationResultMark(String barCode,String type);

    /**
     * 根据下底盘条码获取打螺丝工位结果为"NOK"的数量
     * @param bottomPlaceCode
     * @return
     */
    int getScrewsNokCountByBottomPlaceCode(String bottomPlaceCode);

    /**
     * 通过托盘号查找对应的电测试的合格的记录
     * @param palletNo
     * @return
     */
    List<TestStationData> getElectricResultMarkByPalletNo(String palletNo);
}
