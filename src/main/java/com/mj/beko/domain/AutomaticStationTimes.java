package com.mj.beko.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author wanghb
 * 自动化工位时间点实体类
 */
@Entity
@Table(name = "automatic_station_times")
@Data
public class AutomaticStationTimes implements Serializable{

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    /**
     * 工位名称
     */
    @Column(name = "station_name")
    private String stationName;

    /**
     * 到位时间
     */
    @Column(name = "arrive_time")
    private Timestamp arriveTime;

    /**
     * 放行时间
     */
    @Column(name = "leave_time")
    private Timestamp leaveTime;

    /**
     * cycle time
     */
    @Column(name = "cycle_time")
    private float cycleTime;

    @Override
    public String toString() {
        return "AutomaticStationTimes{" +
                "id=" + id +
                ", stationName='" + stationName + '\'' +
                ", arriveTime='" + arriveTime + '\'' +
                ", leaveTime=" + leaveTime +
                ", cycleTime=" + cycleTime +
                '}';
    }
}
