package com.mj.beko.service.impl;

import com.mj.beko.domain.StationCycleTime;
import com.mj.beko.repository.StationCycleTimeRepository;
import com.mj.beko.service.StationCycleTimeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by Ricardo on 2017/8/24.
 */
@Service
@Transactional
@Slf4j
public class StationCycleTimeServiceImpl implements StationCycleTimeService {

    @Autowired
    private StationCycleTimeRepository stationCycleTimeRepository;

    @Override
    public StationCycleTime save(StationCycleTime stationCycleTime) {
        log.info("save stationCycleTime");
        return stationCycleTimeRepository.save(stationCycleTime);
    }

    /**
     * 根据下底盘条码修改下底盘到位时间点
     * @param arrivedTime
     * @param barcode
     */
    @Override
    public void updateBottomPlateStationArriveTimeByBarcode(Timestamp arrivedTime, String barcode) {
        stationCycleTimeRepository.updateBottomPlateStationArriveTimeByBarcode(arrivedTime, barcode);
    }

    @Override
    public void updateBottomPlateStationCycleTimeByBarcode(Timestamp leaveTime, String barcode) {
        log.debug("根据下底盘条码修改下底盘工位的放行时间和cycleTime : {}", barcode);
        stationCycleTimeRepository.updateBottomPlateStationCycleTimeByBarcode(leaveTime, barcode);

    }

    /**
     * 根据订单编号查询下底盘工位的平均操作时间
     * @param orderNo
     * @return
     */
    @Override
    public String getBottomPlateAverageTime(String orderNo) {
        return stationCycleTimeRepository.getBottomPlateAverageTime(orderNo);
    }

    /**
     * 根据下底盘条码修改上底盘的到位时间
     * @param arrivedTime
     * @param bottomPlateBarcode
     */
    @Override
    public void updateTopPlateStationArriveTimeByBarcode(Timestamp arrivedTime, String bottomPlateBarcode) {
        stationCycleTimeRepository.updateTopPlateStationArriveTimeByBarcode(arrivedTime, bottomPlateBarcode);
    }

    /**
     * 根据下底盘条码获取上底盘工位放行时间点
     * @param bottomPlateBarcode
     * @return
     */
    @Override
    public Timestamp getTopPlateLeftTimeByBottomPlateBarcode(String bottomPlateBarcode) {
        return stationCycleTimeRepository.getTopPlateLeftTimeByBottomPlateBarcode(bottomPlateBarcode);
    }

    /**
     * 根据下底盘条码修改气密检测工位的到位时间
     * @param arrivedTime
     * @param bottomPlaceCode
     */
    @Override
    public void updateAirtightStationArriveTimeByBarcode(Timestamp arrivedTime, String bottomPlaceCode) {
        stationCycleTimeRepository.updateAirtightStationArriveTimeByBarcode(arrivedTime, bottomPlaceCode);
    }

    /**
     * 根据下底盘条码获取下底盘工位放行时间
     * @param bottomPlateBarcode
     * @return
     */
    @Override
    public Timestamp getbottomPlateLeftTimeByBottomPlateBarcode(String bottomPlateBarcode) {
        return stationCycleTimeRepository.getbottomPlateLeftTimeByBottomPlateBarcode(bottomPlateBarcode);
    }

    /**
     * 根据下底盘条码修改上底盘工位放行时间
     * @param leaveTime
     * @param bottomPlateBarcode
     */
    @Override
    public void updateTopPlateStationCycleTimeByBarcode(Timestamp leaveTime, String bottomPlateBarcode) {
        stationCycleTimeRepository.updateTopPlateStationCycleTimeByBarcode(leaveTime, bottomPlateBarcode);
    }

    /**
     * 计算当前工单在上底盘工位的平均时间
     * @param orderNo
     * @return
     */
    @Override
    public String getTopPlateAverageTime(String orderNo) {
        return stationCycleTimeRepository.getTopPlateAverageTime(orderNo);
    }

    /**
     * 根据下底盘条码修改气密检测工位的放行时间和cycleTime
     * @param leaveTime
     * @param bottomPlaceCode
     */
    @Override
    public void updateAirtightStationLeaveTimeByBarcode(Timestamp leaveTime, String bottomPlaceCode) {
        stationCycleTimeRepository.updateAirtightStationLeaveTimeByBarcode(leaveTime, bottomPlaceCode);
    }

    /**
     * 根据下底盘条码修改流量检测工位的到位时间
     * @param arrivedTime
     * @param bottomPlaceCode
     */
    @Override
    public void updateFluxStationArriveTimeByBarcode(Timestamp arrivedTime, String bottomPlaceCode) {
        stationCycleTimeRepository.updateFluxStationArriveTimeByBarcode(arrivedTime, bottomPlaceCode);
    }

    /**
     * 根据下底盘条码修改流量检测工位的放行时间和cycleTime
     * @param leaveTime
     * @param bottomPlaceCode
     */
    @Override
    public void updateFluxStationLeaveTimeByBarcode(Timestamp leaveTime, String bottomPlaceCode) {
        stationCycleTimeRepository.updateFluxStationLeaveTimeByBarcode(leaveTime, bottomPlaceCode);
    }

    /**
     * 根据下底盘条码修改电测试工位的到位时间
     * @param arrivedTime
     * @param bottomPlaceCode
     */
    @Override
    public void updateElectricStationArriveTimeByBarcode(Timestamp arrivedTime, String bottomPlaceCode) {
        stationCycleTimeRepository.updateElectricStationArriveTimeByBarcode(arrivedTime, bottomPlaceCode);
    }

    /**
     * 根据下底盘条码修改电测试工位的放行时间和cycleTime
     * @param leaveTime
     * @param bottomPlaceCode
     */
    @Override
    public void updateElectricStationLeaveTimeByBarcode(Timestamp leaveTime, String bottomPlaceCode) {
        stationCycleTimeRepository.updateElectricStationLeaveTimeByBarcode(leaveTime, bottomPlaceCode);
    }

    /**
     * 根据下底盘条码修改电测试工位的到位时间
     * @param arrivedTime
     * @param bottomPlaceCode
     */
    @Override
    public void updateKnobStationArriveTimeByBarcode(Timestamp arrivedTime, String bottomPlaceCode) {
        stationCycleTimeRepository.updateKnobStationArriveTimeByBarcode(arrivedTime, bottomPlaceCode);
    }

    /**
     * 根据下底盘条码获取旋钮工位放行时间
     * @param bottomPlateBarcode
     */
    @Override
    public Timestamp getKnobLeftTimeByBottomPlateBarcode(String bottomPlateBarcode) {
        return stationCycleTimeRepository.getKnobLeftTimeByBottomPlateBarcode(bottomPlateBarcode);
    }

    /**
     * 根据下底盘条码修改旋钮工位的放行时间和cycleTime
     * @param leaveTime
     * @param bottomPlateBarcode
     */
    @Override
    public void updateKnobStationCycleTimeByBarcode(Timestamp leaveTime, String bottomPlateBarcode) {
        stationCycleTimeRepository.updateKnobStationCycleTimeByBarcode(leaveTime, bottomPlateBarcode);
    }

    /**
     * 计算当前工单在旋钮工位的平均时间
     * @param orderNo
     * @return
     */
    @Override
    public String getKnobAverageTime(String orderNo) {
        return stationCycleTimeRepository.getKnobAverageTime(orderNo);
    }

    /**
     * 根据下底盘条码保存火焰测试的到位时间
     * @param arrivedTime
     * @param bottomPlateBarcode
     */
    @Override
    public void updateFireTestStationArriveTimeByBarcode(Timestamp arrivedTime, String bottomPlateBarcode) {
        stationCycleTimeRepository.updateFireTestStationArriveTimeByBarcode(arrivedTime, bottomPlateBarcode);
    }

    /**
     * 根据下底盘条码保存火焰测试的放行时间和cycleTime
     * @param leaveTime
     * @param bottomPlateBarcode
     */
    @Override
    public void updateFireTestStationLeftTimeByBarcode(Timestamp leaveTime, String bottomPlateBarcode) {
        stationCycleTimeRepository.updateFireTestStationLeftTimeByBarcode(leaveTime, bottomPlateBarcode);
    }

    /**
     * 根据下底盘条码获取火焰测试工位放行时间点
     * @param bottomPlateBarcode
     * @return
     */
    @Override
    public Timestamp getFireTestLeftTimeByBottomPlateBarcode(String bottomPlateBarcode) {
        return stationCycleTimeRepository.getFireTestLeftTimeByBottomPlateBarcode(bottomPlateBarcode);
    }

    /**
     * 计算当前工单在火焰测试工位的平均时间
     * @param orderNo
     * @return
     */
    @Override
    public String getFireTestAverageTime(String orderNo) {
        return stationCycleTimeRepository.getFireTestAverageTime(orderNo);
    }

    /**
     * 根据下底盘条码保存视觉控制的到位时间
     * @param arrivedTime
     * @param bottomPlateBarcode
     */
    @Override
    public void updateVisionControlStationArriveTimeByBarcode(Timestamp arrivedTime, String bottomPlateBarcode) {
        stationCycleTimeRepository.updateVisionControlStationArriveTimeByBarcode(arrivedTime, bottomPlateBarcode);
    }

    /**
     * 根据下底盘条码保存视觉控制的放行时间和cycleTime
     * @param leaveTime
     * @param bottomPlateBarcode
     */
    @Override
    public void updateVisionControlStationLeftTimeByBarcode(Timestamp leaveTime, String bottomPlateBarcode) {
        stationCycleTimeRepository.updateVisionControlStationLeftTimeByBarcode(leaveTime, bottomPlateBarcode);
    }

    /**
     * 根据下底盘条码获取视觉控制工位放行时间点
     * @param bottomPlateBarcode
     * @return
     */
    @Override
    public Timestamp getVisionControlLeftTimeByBottomPlateBarcode(String bottomPlateBarcode) {
        return stationCycleTimeRepository.getVisionControlLeftTimeByBottomPlateBarcode(bottomPlateBarcode);
    }

    /**
     * 计算当前工单在视觉控制工位的平均时间
     * @param orderNo
     * @return
     */
    @Override
    public String getVisionControlAverageTime(String orderNo) {
        return stationCycleTimeRepository.getVisionControlAverageTime(orderNo);
    }

    /**
     * 根据下底盘条码保存拔电和气工位的到位时间
     * @param arrivedTime
     * @param bottomPlateBarcode
     */
    @Override
    public void updateRemoveElectricGasArriveTimeByBarcode(Timestamp arrivedTime, String bottomPlateBarcode) {
        stationCycleTimeRepository.updateRemoveElectricGasArriveTimeByBarcode(arrivedTime, bottomPlateBarcode);
    }

    /**
     * 根据下底盘条码保存拔电气的放行时间和cycleTime
     * @param leaveTime
     * @param bottomPlateBarcode
     */
    @Override
    public void updateRemoveElectricAndGasStationLeftTimeByBarcode(Timestamp leaveTime, String bottomPlateBarcode) {
        stationCycleTimeRepository.updateRemoveElectricAndGasStationLeftTimeByBarcode(leaveTime, bottomPlateBarcode);
    }

    /**
     * 根据下底盘条码获取拔电气工位放行时间点
     * @param bottomPlateBarcode
     * @return
     */
    @Override
    public Timestamp getRemoveElectricAndGasLeftTimeByBottomPlateBarcode(String bottomPlateBarcode) {
        return stationCycleTimeRepository.getRemoveElectricAndGasLeftTimeByBottomPlateBarcode(bottomPlateBarcode);
    }

    /**
     * 计算当前工单在拔电气工位的平均时间
     * @param orderNo
     * @return
     */
    @Override
    public String getRemoveElectricAndGasAverageTime(String orderNo) {
        return stationCycleTimeRepository.getRemoveElectricAndGasAverageTime(orderNo);
    }

    /**
     * 根据下底盘条码查询订单号
     * @param bottomPlateBarcode
     * @return
     */
    @Override
    public String getOrderNoByBottomPlaceCode(String bottomPlateBarcode) {
        return stationCycleTimeRepository.getOrderNoByBottomPlaceCode(bottomPlateBarcode);
    }

    @Override
    public List<StationCycleTime> getOneStationCycleTimeByBottomPlateStation(String bottomPlateBarCode) {
        log.info("get station cycle time by bottom plate barcode");
        return stationCycleTimeRepository.getOneStationCycleTimeByBottomPlateStation(bottomPlateBarCode);
    }
}
