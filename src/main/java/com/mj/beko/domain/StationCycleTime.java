package com.mj.beko.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;

/**
 * @author wanghb
 * 所有工位到位、放行时间点和CycleTime实体
 */
@Entity
@Table(name = "station_cycle_time")
@Data
public class StationCycleTime implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    /**
     * 下底盘条码
     */
    @Column(name = "bottom_place_code")
    private String bottomPlaceCode;

    /**
     * 产品类型
     */
    @Column(name = "product_no")
    private String productNo;

    /** 下底盘工位的到位、放行时间点和CycleTime **/
    @Column(name = "bottom_plate_station_start")
    private Timestamp bottomPlateStationStart;

    @Column(name = "bottom_plate_station_end")
    private Timestamp bottomPlateStationEnd;

    @Column(name = "bottom_plate_station_cycle_time")
    private float bottomPlateStationCycleTime;

    /** 上底盘工位的到位、放行时间点和CycleTime **/
    @Column(name = "top_plate_station_start")
    private Timestamp topPlateStationStart;

    @Column(name = "top_plate_station_end")
    private Timestamp topPlateStationEnd;

    @Column(name = "top_plate_station_cycle_time")
    private float topPlateStationCycleTime;

    /** 气密检测工位的到位、放行时间点和CycleTime **/
    @Column(name = "airtight_station_start")
    private Timestamp airtightStationStart;

    @Column(name = "airtight_station_end")
    private Timestamp airtightStationEnd;

    @Column(name = "airtight_station_cycle_time")
    private float airtightStationCycleTime;

    /** 流量检测工位的到位、放行时间点和CycleTime **/
    @Column(name = "flux_station_start")
    private Timestamp fluxStationStart;

    @Column(name = "flux_station_end")
    private Timestamp fluxStationEnd;

    @Column(name = "flux_station_cycle_time")
    private float fluxStationCycleTime;

    /** 电测试工位的到位、放行时间点和CycleTime **/
    @Column(name = "electric_station_start")
    private Timestamp electricStationStart;

    @Column(name = "electric_station_end")
    private Timestamp electricStationEnd;

    @Column(name = "electric_station_cycle_time")
    private float electricStationCycleTime;

    /** 旋钮工位的到位、放行时间点和CycleTime **/
    @Column(name = "knob_station_start")
    private Timestamp knobStationStart;

    @Column(name = "knob_station_end")
    private Timestamp knobStationEnd;

    @Column(name = "knob_station_cycle_time")
    private float knobStationCycleTime;

    /** 火焰测试工位的到位、放行时间点和CycleTime **/
    @Column(name = "fire_test_start")
    private Timestamp fireTestStart;

    @Column(name = "fire_test_end")
    private Timestamp fireTestEnd;

    @Column(name = "fire_test_cycle_time")
    private float fireTestCycleTime;

    /** 视觉控制工位的到位、放行时间点和CycleTime **/
    @Column(name = "vision_control_start")
    private Timestamp visionControlStart;

    @Column(name = "vision_control_end")
    private Timestamp visionControlEnd;

    @Column(name = "vision_control_cycle_time")
    private float visionControlCycleTime;

    /** 拔电和气工位的到位、放行时间点和CycleTime **/
    @Column(name = "remove_electric_gas_start")
    private Timestamp removeElectricGasStart;

    @Column(name = "remove_electric_gas_end")
    private Timestamp removeElectricGasEnd;

    @Column(name = "remove_electric_gas_cycle_time")
    private float removeElectricGasCycleTime;

    @Column(name = "order_no")
    private String orderNo;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StationCycleTime stationCycleTime = (StationCycleTime) o;
        if (stationCycleTime.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), stationCycleTime.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "StationCycleTime{" +
            "id=" + getId() +
            ", bottomPlaceCode='" + getBottomPlaceCode() + "'" +
            ", productNo='" + getProductNo() + "'" +
            ", bottomPlateStationStart='" + getBottomPlateStationStart() + "'" +
            ", bottomPlateStationEnd='" + getBottomPlateStationEnd() + "'" +
            ", bottomPlateStationCycleTime='" + getBottomPlateStationCycleTime() + "'" +
            ", topPlateStationStart='" + getTopPlateStationStart() + "'" +
            ", topPlateStationEnd='" + getTopPlateStationEnd() + "'" +
            ", topPlateStationCycleTime='" + getTopPlateStationCycleTime() + "'" +
            ", orderNo='" + getOrderNo() + "'" +
            "}";
    }
}
