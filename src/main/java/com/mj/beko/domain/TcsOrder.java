package com.mj.beko.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Created by Ricardo on 2017/8/24.
 */
@Entity
@Table(name = "tcs_order")
@Data
public class TcsOrder implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "tcs_order_name")
    private String tcsOrderName;

    @Column(name = "station_no")
    private String stationNo;

    @Column(name = "consume_part_name")
    private String consumePartName;

    @Column(name = "consume_part_quantity")
    private String consumePartQuantity;

    @Column(name = "state")
    private String state;

    @Column(name = "start_time")
    private Timestamp startTime;

    @Column(name = "executing_vehicle_regex")
    private String executingVehicleRegex;

    @Column(name = "tcs_type")
    private String type;

    @Column(name = "function_type")
    private String functionType;

    @Column(name = "start_point")
    private String startPoint;

    @Column(name = "end_point")
    private String endPoint;
    private String node01; //预留三个节点
    private String node02; //预留三个节点
    private String node03;//预留三个节点
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TcsOrder tcsOrder = (TcsOrder) o;
        if (tcsOrder.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), tcsOrder.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }


    @Override
    public String toString() {
        return "TcsOrder{" +
                "id=" + getId() +
                ", tcsOrderName='" + getTcsOrderName() + "'" +
                ", stationNo='" + getStationNo() + "'" +
                ", consumePartName='" + getConsumePartName() + "'" +
                ", consumePartQuantity='" + getConsumePartQuantity() + "'" +
                ", state='" + getState() + "'" +
                ", startTime='" + getStartTime() + "'" +
                ", executingVehicleRegex='" + getExecutingVehicleRegex() + "'" +
                ", type='" + getType() + "'" +
                ", functionType='" + getFunctionType() + "'" +
                ", startPoint='" + getStartPoint() + "'" +
                ", endPoint='" + getEndPoint() + "'" +
                "}";
    }
}
