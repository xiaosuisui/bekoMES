package com.mj.beko.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Ricardo on 2017/12/17.
 * 计算每个orderNo,对应的不同工位的需要运输的小车数
 */
@Entity
@Data
@Table(name = "t_agv_car_station_number")
public class AgvCarNumberForStation implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;
    private String shiftName;//current shift name
    private String orderNo;//orderNo
    private String productNo;//orderNo
    private String station;//station
    private int totalNumber;//总数量
    private String material;//物料名称
    private int totalCar;//total number
    private int currentCar;//current car
    private String extraField01;//预留字段01
    private String extraField02;//预留字段02
    private String extraField03;//预留字段03
    private String extraField04;//预留字段04

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        AgvCarNumberForStation that = (AgvCarNumberForStation) o;

        if (totalCar != that.totalCar) return false;
        if (currentCar != that.currentCar) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (shiftName != null ? !shiftName.equals(that.shiftName) : that.shiftName != null) return false;
        if (orderNo != null ? !orderNo.equals(that.orderNo) : that.orderNo != null) return false;
        if (productNo != null ? !productNo.equals(that.productNo) : that.productNo != null) return false;
        if (station != null ? !station.equals(that.station) : that.station != null) return false;
        if (material != null ? !material.equals(that.material) : that.material != null) return false;
        if (extraField01 != null ? !extraField01.equals(that.extraField01) : that.extraField01 != null) return false;
        if (extraField02 != null ? !extraField02.equals(that.extraField02) : that.extraField02 != null) return false;
        if (extraField03 != null ? !extraField03.equals(that.extraField03) : that.extraField03 != null) return false;
        return extraField04 != null ? extraField04.equals(that.extraField04) : that.extraField04 == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (shiftName != null ? shiftName.hashCode() : 0);
        result = 31 * result + (orderNo != null ? orderNo.hashCode() : 0);
        result = 31 * result + (productNo != null ? productNo.hashCode() : 0);
        result = 31 * result + (station != null ? station.hashCode() : 0);
        result = 31 * result + (material != null ? material.hashCode() : 0);
        result = 31 * result + totalCar;
        result = 31 * result + currentCar;
        result = 31 * result + (extraField01 != null ? extraField01.hashCode() : 0);
        result = 31 * result + (extraField02 != null ? extraField02.hashCode() : 0);
        result = 31 * result + (extraField03 != null ? extraField03.hashCode() : 0);
        result = 31 * result + (extraField04 != null ? extraField04.hashCode() : 0);
        return result;
    }
    @Override
    public String toString() {
        return "AgvCarNumberForStation{" +
                "id=" + id +
                ", shiftName='" + shiftName + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", productNo='" + productNo + '\'' +
                ", station='" + station + '\'' +
                ", material='" + material + '\'' +
                ", totalCar=" + totalCar +
                ", currentCar=" + currentCar +
                ", extraField01='" + extraField01 + '\'' +
                ", extraField02='" + extraField02 + '\'' +
                ", extraField03='" + extraField03 + '\'' +
                ", extraField04='" + extraField04 + '\'' +
                '}';
    }
}
