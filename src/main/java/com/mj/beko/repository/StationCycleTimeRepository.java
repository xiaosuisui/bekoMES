package com.mj.beko.repository;

import com.mj.beko.domain.StationCycleTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by Ricardo on 2017/8/24.
 */
public interface StationCycleTimeRepository extends JpaRepository<StationCycleTime,Long> {

    /**
     * 根据订单编号查询下底盘工位的平均操作时间
     * @param orderNo
     * @return
     */
    @Query(value = "select avg(bottom_plate_station_cycle_time) from station_cycle_time where order_no = ?1 and bottom_plate_station_cycle_time != 0", nativeQuery = true)
    String getBottomPlateAverageTime(String orderNo);

    /**
     * 根据下底盘条码获取下底盘工位放行时间
     * @param bottomPlaceCode
     * @return
     */
    @Query(value = "select bottom_plate_station_end from station_cycle_time where bottom_place_code = ?1", nativeQuery = true)
    Timestamp getbottomPlateLeftTimeByBottomPlateBarcode(String bottomPlaceCode);

    /**
     * 根据下底盘条码修改下底盘到位时间点
     * @param arrivedTime
     * @param barcode
     */
    @Modifying
    @Query(value = "update station_cycle_time set bottom_plate_station_start = ?1 where bottom_place_code = ?2", nativeQuery = true)
    void updateBottomPlateStationArriveTimeByBarcode(Timestamp arrivedTime, String barcode);

    @Modifying
    @Query(value = "update station_cycle_time " +
            "set bottom_plate_station_end = ?1, " +
            "bottom_plate_station_cycle_time = DATEDIFF(S, bottom_plate_station_start, ?1) " +
            "where bottom_place_code = ?2", nativeQuery = true)
    void updateBottomPlateStationCycleTimeByBarcode(Timestamp leaveTime, String barcode);

    /**
     * 根据下底盘条码修改上底盘的到位时间
     * @param arrivedTime
     * @param bottomPlateBarcode
     */
    @Modifying
    @Query(value = "update station_cycle_time set top_plate_station_start = ?1 where bottom_place_code = ?2", nativeQuery = true)
    void updateTopPlateStationArriveTimeByBarcode(Timestamp arrivedTime, String bottomPlateBarcode);

    /**
     * 根据下底盘条码获取上底盘工位放行时间点
     * @param bottomPlateBarcode
     * @return
     */
    @Query(value = "select top_plate_station_end from station_cycle_time where bottom_place_code = ?1", nativeQuery = true)
    Timestamp getTopPlateLeftTimeByBottomPlateBarcode(String bottomPlateBarcode);

    /**
     * 根据下底盘条码修改上底盘工位放行时间
     * @param leaveTime
     * @param bottomPlateBarcode
     */
    @Modifying
    @Query(value = "update station_cycle_time " +
            "set top_plate_station_end = ?1, " +
            "top_plate_station_cycle_time = DATEDIFF(S, top_plate_station_start, ?1) " +
            "where bottom_place_code = ?2", nativeQuery = true)
    void updateTopPlateStationCycleTimeByBarcode(Timestamp leaveTime, String bottomPlateBarcode);

    /**
     * 计算当前工单在上底盘工位的平均时间
     * @param orderNo
     * @return
     */
    @Query(value = "select avg(top_plate_station_cycle_time) from station_cycle_time where order_no = ?1 and top_plate_station_cycle_time != 0", nativeQuery = true)
    String getTopPlateAverageTime(String orderNo);

    /**
     * 根据下底盘条码修改气密检测工位的到位时间
     * @param arrivedTime
     * @param bottomPlaceCode
     */
    @Modifying
    @Query(value = "update station_cycle_time set airtight_station_start = ?1 where bottom_place_code = ?2", nativeQuery = true)
    void updateAirtightStationArriveTimeByBarcode(Timestamp arrivedTime, String bottomPlaceCode);

    /**
     * 根据下底盘条码修改气密检测工位的放行时间和cycleTime
     * @param leaveTime
     * @param bottomPlaceCode
     */
    @Modifying
    @Query(value = "update station_cycle_time " +
            "set airtight_station_end = ?1, " +
            "airtight_station_cycle_time = DATEDIFF(S, airtight_station_start, ?1) " +
            "where bottom_place_code = ?2", nativeQuery = true)
    void updateAirtightStationLeaveTimeByBarcode(Timestamp leaveTime, String bottomPlaceCode);

    /**
     * 根据下底盘条码修改流量检测工位的到位时间
     * @param arrivedTime
     * @param bottomPlaceCode
     */
    @Modifying
    @Query(value = "update station_cycle_time set flux_station_start = ?1 where bottom_place_code = ?2", nativeQuery = true)
    void updateFluxStationArriveTimeByBarcode(Timestamp arrivedTime, String bottomPlaceCode);

    /**
     * 根据下底盘条码修改流量检测工位的放行时间和cycleTime
     * @param leaveTime
     * @param bottomPlaceCode
     */
    @Modifying
    @Query(value = "update station_cycle_time " +
            "set flux_station_end = ?1, " +
            "flux_station_cycle_time = DATEDIFF(S, flux_station_start, ?1) " +
            "where bottom_place_code = ?2", nativeQuery = true)
    void updateFluxStationLeaveTimeByBarcode(Timestamp leaveTime, String bottomPlaceCode);

    /**
     * 根据下底盘条码修改电测试工位的到位时间
     * @param arrivedTime
     * @param bottomPlaceCode
     */
    @Modifying
    @Query(value = "update station_cycle_time set electric_station_start = ?1 where bottom_place_code = ?2", nativeQuery = true)
    void updateElectricStationArriveTimeByBarcode(Timestamp arrivedTime, String bottomPlaceCode);

    /**
     * 根据下底盘条码修改电测试工位的放行时间和cycleTime
     * @param leaveTime
     * @param bottomPlaceCode
     */
    @Modifying
    @Query(value = "update station_cycle_time " +
            "set electric_station_end = ?1, " +
            "electric_station_cycle_time = DATEDIFF(S, electric_station_start, ?1) " +
            "where bottom_place_code = ?2", nativeQuery = true)
    void updateElectricStationLeaveTimeByBarcode(Timestamp leaveTime, String bottomPlaceCode);

    /**
     * 根据下底盘条码修改电测试工位的到位时间
     * @param arrivedTime
     * @param bottomPlaceCode
     */
    @Modifying
    @Query(value = "update station_cycle_time set knob_station_start = ?1 where bottom_place_code = ?2", nativeQuery = true)
    void updateKnobStationArriveTimeByBarcode(Timestamp arrivedTime, String bottomPlaceCode);

    /**
     * 根据下底盘条码获取旋钮工位放行时间
     * @param bottomPlateBarcode
     */
    @Query(value = "select knob_station_end from station_cycle_time where bottom_place_code = ?1", nativeQuery = true)
    Timestamp getKnobLeftTimeByBottomPlateBarcode(String bottomPlateBarcode);

    /**
     * 根据下底盘条码修改旋钮工位的放行时间和cycleTime
     * @param leaveTime
     * @param bottomPlateBarcode
     */
    @Modifying
    @Query(value = "update station_cycle_time " +
            "set knob_station_end = ?1, " +
            "knob_station_cycle_time = DATEDIFF(S, knob_station_start, ?1) " +
            "where bottom_place_code = ?2", nativeQuery = true)
    void updateKnobStationCycleTimeByBarcode(Timestamp leaveTime, String bottomPlateBarcode);

    /**
     * 计算当前工单在旋钮工位的平均时间
     * @param orderNo
     * @return
     */
    @Query(value = "select avg(knob_station_cycle_time) from station_cycle_time where order_no = ?1 and knob_station_cycle_time != 0", nativeQuery = true)
    String getKnobAverageTime(String orderNo);

    /**
     * 根据下底盘条码保存火焰测试的到位时间
     * @param arrivedTime
     * @param bottomPlateBarcode
     */
    @Modifying
    @Query(value = "update station_cycle_time set fire_test_start = ?1 where bottom_place_code = ?2", nativeQuery = true)
    void updateFireTestStationArriveTimeByBarcode(Timestamp arrivedTime, String bottomPlateBarcode);

    /**
     * 根据下底盘条码保存火焰测试的放行时间和cycleTime
     * @param leaveTime
     * @param bottomPlateBarcode
     */
    @Modifying
    @Query(value = "update station_cycle_time " +
            "set fire_test_end = ?1, " +
            "fire_test_cycle_time = DATEDIFF(S, fire_test_start, ?1) " +
            "where bottom_place_code = ?2", nativeQuery = true)
    void updateFireTestStationLeftTimeByBarcode(Timestamp leaveTime, String bottomPlateBarcode);

    /**
     * 根据下底盘条码获取火焰测试工位放行时间点
     * @param bottomPlateBarcode
     * @return
     */
    @Query(value = "select fire_test_end from station_cycle_time where bottom_place_code = ?1", nativeQuery = true)
    Timestamp getFireTestLeftTimeByBottomPlateBarcode(String bottomPlateBarcode);

    /**
     * 计算当前工单在火焰测试工位的平均时间
     * @param orderNo
     * @return
     */
    @Query(value = "select avg(fire_test_cycle_time) from station_cycle_time where order_no = ?1 and fire_test_cycle_time != 0", nativeQuery = true)
    String getFireTestAverageTime(String orderNo);

    /**
     * 根据下底盘条码保存视觉控制的到位时间
     * @param arrivedTime
     * @param bottomPlateBarcode
     */
    @Modifying
    @Query(value = "update station_cycle_time set vision_control_start = ?1 where bottom_place_code = ?2", nativeQuery = true)
    void updateVisionControlStationArriveTimeByBarcode(Timestamp arrivedTime, String bottomPlateBarcode);

    /**
     * 根据下底盘条码保存视觉控制的放行时间和cycleTime
     * @param leaveTime
     * @param bottomPlateBarcode
     */
    @Modifying
    @Query(value = "update station_cycle_time " +
            "set vision_control_end = ?1, " +
            "vision_control_cycle_time = DATEDIFF(S, vision_control_start, ?1) " +
            "where bottom_place_code = ?2", nativeQuery = true)
    void updateVisionControlStationLeftTimeByBarcode(Timestamp leaveTime, String bottomPlateBarcode);

    /**
     * 根据下底盘条码获取视觉控制工位放行时间点
     * @param bottomPlateBarcode
     * @return
     */
    @Query(value = "select vision_control_end from station_cycle_time where bottom_place_code = ?1", nativeQuery = true)
    Timestamp getVisionControlLeftTimeByBottomPlateBarcode(String bottomPlateBarcode);

    /**
     * 计算当前工单在视觉控制工位的平均时间
     * @param orderNo
     * @return
     */
    @Query(value = "select avg(vision_control_cycle_time) from station_cycle_time where order_no = ?1 and vision_control_cycle_time != 0", nativeQuery = true)
    String getVisionControlAverageTime(String orderNo);

    /**
     * 根据下底盘条码保存拔电和气工位的到位时间
     * @param arrivedTime
     * @param bottomPlateBarcode
     */
    @Modifying
    @Query(value = "update station_cycle_time set remove_electric_gas_start = ?1 where bottom_place_code = ?2", nativeQuery = true)
    void updateRemoveElectricGasArriveTimeByBarcode(Timestamp arrivedTime, String bottomPlateBarcode);

    /**
     * 根据下底盘条码保存拔电气的放行时间和cycleTime
     * @param leaveTime
     * @param bottomPlateBarcode
     */
    @Modifying
    @Query(value = "update station_cycle_time " +
            "set remove_electric_gas_end = ?1, " +
            "remove_electric_gas_cycle_time = DATEDIFF(S, remove_electric_gas_start, ?1) " +
            "where bottom_place_code = ?2", nativeQuery = true)
    void updateRemoveElectricAndGasStationLeftTimeByBarcode(Timestamp leaveTime, String bottomPlateBarcode);

    /**
     * 根据下底盘条码获取拔电气工位放行时间点
     * @param bottomPlateBarcode
     * @return
     */
    @Query(value = "select remove_electric_gas_end from station_cycle_time where bottom_place_code = ?1", nativeQuery = true)
    Timestamp getRemoveElectricAndGasLeftTimeByBottomPlateBarcode(String bottomPlateBarcode);

    /**
     * 计算当前工单在拔电气工位的平均时间
     * @param orderNo
     * @return
     */
    @Query(value = "select avg(remove_electric_gas_cycle_time) from station_cycle_time where order_no = ?1 and remove_electric_gas_cycle_time != 0", nativeQuery = true)
    String getRemoveElectricAndGasAverageTime(String orderNo);

    /**
     * 根据下底盘条码查询订单号
     * @param bottomPlateBarcode
     * @return
     */
    @Query(value = "select order_no from station_cycle_time where bottom_place_code = ?1", nativeQuery = true)
    String getOrderNoByBottomPlaceCode(String bottomPlateBarcode);
    @Query(value = "select top 100 * from station_cycle_time where bottom_place_code=:bottomPlateBarCode",nativeQuery = true)
    List<StationCycleTime> getOneStationCycleTimeByBottomPlateStation(@Param("bottomPlateBarCode") String bottomPlateBarCode);
}
