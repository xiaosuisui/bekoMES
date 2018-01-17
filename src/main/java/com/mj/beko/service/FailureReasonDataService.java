package com.mj.beko.service;
import com.mj.beko.domain.FailureReasonData;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by Ricardo on 2017/11/16.
 */
public interface FailureReasonDataService extends BaseService<FailureReasonData>  {
    /**
     * 通过条件查询
     * @param page
     * @param size
     * @return
     */
    Page<FailureReasonData> findFailureReasonDataByCondition(String workstation,String barCode, int page, int size);

    /**
     * 条件查询记录数
     * @return
     */

    long getAllFailureReasonDataByCondition(String workstation,String barCode);

    /**
     * 通过Id查询
     * @param id
     * @return
     */
    FailureReasonData findOneById(Long id);

    /**
     * 根据下底盘条码和工位名称查询记录数
     * @param bottomPlateBarcode
     * @param stationName
     * @return
     */
    int getCountByBottomPlateBarcodeAndStation(String bottomPlateBarcode, String stationName);

    List<FailureReasonData> getFailureReasonByCode(String barCode);

    /*
     * 修改failureReason data status 0 --1
     * @param failureReasonData
     */
    void updateFailureReasonData(List<FailureReasonData> failureReasonData);

    /**
     * 查询失败原因中的数据(旋钮工位和火焰燃烧工位)
     * @param palletNo
     * @param point
     * @return
     */
    List<FailureReasonData> getKnobsAndBurnResultMarkByPalletNo(String palletNo,String point);

    /**
     * 通过工位编号和托盘号查询,状态为0的失败的记录,如果查到则为NOK
     * @param palletNo
     * @param workStation
     * @return
     */
    List<FailureReasonData> getKnobsAndBurnTestResult(String palletNo,String workStation);
}
