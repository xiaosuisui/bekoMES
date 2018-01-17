package com.mj.beko.service;

import com.mj.beko.domain.StationCycleTime;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by Ricardo on 2017/8/24.
 */
public interface StationCycleTimeService {
    /**
     * 保存
     * @param stationCycleTime
     * @return
     */
    StationCycleTime save(StationCycleTime stationCycleTime);

    /**
     * 根据下底盘条码修改下底盘到位时间点
     * @param arrivedTime
     * @param barcode
     */
    void updateBottomPlateStationArriveTimeByBarcode(Timestamp arrivedTime, String barcode);

    /**
     * 修改下底盘工位的放行时间和cycleTime
     * @param leaveTime
     * @param barcode
     */
    void updateBottomPlateStationCycleTimeByBarcode(Timestamp leaveTime, String barcode);

    /**
     * 根据订单编号查询下底盘工位的平均操作时间
     * @param orderNo
     * @return
     */
    String getBottomPlateAverageTime(String orderNo);

    /**
     * 根据下底盘条码修改上底盘的到位时间
     * @param arrivedTime
     * @param bottomPlateBarcode
     */
    void updateTopPlateStationArriveTimeByBarcode(Timestamp arrivedTime, String bottomPlateBarcode);

    /**
     * 根据下底盘条码获取上底盘工位放行时间点
     * @param bottomPlateBarcode
     * @return
     */
    Timestamp getTopPlateLeftTimeByBottomPlateBarcode(String bottomPlateBarcode);

    /**
     * 根据下底盘条码修改气密检测工位的到位时间
     * @param arrivedTime
     * @param bottomPlaceCode
     */
    void updateAirtightStationArriveTimeByBarcode(Timestamp arrivedTime, String bottomPlaceCode);

    /**
     * 根据下底盘条码获取下底盘工位放行时间
     * @param bottomPlateBarcode
     * @return
     */
    Timestamp getbottomPlateLeftTimeByBottomPlateBarcode(String bottomPlateBarcode);

    /**
     * 根据下底盘条码修改上底盘工位放行时间
     * @param leaveTime
     * @param bottomPlateBarcode
     */
    void updateTopPlateStationCycleTimeByBarcode(Timestamp leaveTime, String bottomPlateBarcode);

    /**
     * 计算当前工单在上底盘工位的平均时间
     * @param orderNo
     * @return
     */
    String getTopPlateAverageTime(String orderNo);

    /**
     * 根据下底盘条码修改气密检测工位的放行时间和cycleTime
     * @param leaveTime
     * @param bottomPlaceCode
     */
    void updateAirtightStationLeaveTimeByBarcode(Timestamp leaveTime, String bottomPlaceCode);

    /**
     * 根据下底盘条码修改流量检测工位的到位时间
     * @param arrivedTime
     * @param bottomPlaceCode
     */
    void updateFluxStationArriveTimeByBarcode(Timestamp arrivedTime, String bottomPlaceCode);

    /**
     * 根据下底盘条码修改流量检测工位的放行时间和cycleTime
     * @param leaveTime
     * @param bottomPlaceCode
     */
    void updateFluxStationLeaveTimeByBarcode(Timestamp leaveTime, String bottomPlaceCode);

    /**
     * 根据下底盘条码修改电测试工位的到位时间
     * @param arrivedTime
     * @param bottomPlaceCode
     */
    void updateElectricStationArriveTimeByBarcode(Timestamp arrivedTime, String bottomPlaceCode);

    /**
     * 根据下底盘条码修改电测试工位的放行时间和cycleTime
     * @param leaveTime
     * @param bottomPlaceCode
     */
    void updateElectricStationLeaveTimeByBarcode(Timestamp leaveTime, String bottomPlaceCode);

    /**
     * 根据下底盘条码修改电测试工位的到位时间
     * @param arrivedTime
     * @param bottomPlaceCode
     */
    void updateKnobStationArriveTimeByBarcode(Timestamp arrivedTime, String bottomPlaceCode);

    /**
     * 根据下底盘条码获取旋钮工位放行时间
     * @param bottomPlateBarcode
     * @return
     */
    Timestamp getKnobLeftTimeByBottomPlateBarcode(String bottomPlateBarcode);

    /**
     * 根据下底盘条码修改旋钮工位的放行时间和cycleTime
     * @param leaveTime
     * @param bottomPlateBarcode
     */
    void updateKnobStationCycleTimeByBarcode(Timestamp leaveTime, String bottomPlateBarcode);

    /**
     * 计算当前工单在旋钮工位的平均时间
     * @param orderNo
     * @return
     */
    String getKnobAverageTime(String orderNo);

    /**
     * 根据下底盘条码保存火焰测试的到位时间
     * @param arrivedTime
     * @param bottomPlateBarcode
     */
    void updateFireTestStationArriveTimeByBarcode(Timestamp arrivedTime, String bottomPlateBarcode);

    /**
     * 根据下底盘条码保存火焰测试的放行时间和cycleTime
     * @param leaveTime
     * @param bottomPlateBarcode
     */
    void updateFireTestStationLeftTimeByBarcode(Timestamp leaveTime, String bottomPlateBarcode);

    /**
     * 根据下底盘条码获取火焰测试工位放行时间点
     * @param bottomPlateBarcode
     * @return
     */
    Timestamp getFireTestLeftTimeByBottomPlateBarcode(String bottomPlateBarcode);

    /**
     * 计算当前工单在火焰测试工位的平均时间
     * @param orderNo
     * @return
     */
    String getFireTestAverageTime(String orderNo);

    /**
     * 根据下底盘条码保存视觉控制的到位时间
     * @param arrivedTime
     * @param bottomPlateBarcode
     */
    void updateVisionControlStationArriveTimeByBarcode(Timestamp arrivedTime, String bottomPlateBarcode);

    /**
     * 根据下底盘条码保存视觉控制的放行时间和cycleTime
     * @param leaveTime
     * @param bottomPlateBarcode
     */
    void updateVisionControlStationLeftTimeByBarcode(Timestamp leaveTime, String bottomPlateBarcode);

    /**
     * 根据下底盘条码获取视觉控制工位放行时间点
     * @param bottomPlateBarcode
     * @return
     */
    Timestamp getVisionControlLeftTimeByBottomPlateBarcode(String bottomPlateBarcode);

    /**
     * 计算当前工单在视觉控制工位的平均时间
     * @param orderNo
     * @return
     */
    String getVisionControlAverageTime(String orderNo);

    /**
     * 根据下底盘条码保存拔电和气工位的到位时间
     * @param arrivedTime
     * @param bottomPlateBarcode
     */
    void updateRemoveElectricGasArriveTimeByBarcode(Timestamp arrivedTime, String bottomPlateBarcode);

    /**
     * 根据下底盘条码保存拔电气的放行时间和cycleTime
     * @param leaveTime
     * @param bottomPlateBarcode
     */
    void updateRemoveElectricAndGasStationLeftTimeByBarcode(Timestamp leaveTime, String bottomPlateBarcode);

    /**
     * 根据下底盘条码获取拔电气工位放行时间点
     * @param bottomPlateBarcode
     * @return
     */
    Timestamp getRemoveElectricAndGasLeftTimeByBottomPlateBarcode(String bottomPlateBarcode);

    /**
     * 计算当前工单在拔电气工位的平均时间
     * @param orderNo
     * @return
     */
    String getRemoveElectricAndGasAverageTime(String orderNo);

    /**
     * 根据下底盘条码查询订单号
     * @param bottomPlateBarcode
     * @return
     */
    String getOrderNoByBottomPlaceCode(String bottomPlateBarcode);
    /**
     * 通过下底盘条码查询对应的stationCycleEntity
     */
    List<StationCycleTime> getOneStationCycleTimeByBottomPlateStation(String bottomPlateBarCode);
}
