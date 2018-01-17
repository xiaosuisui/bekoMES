package com.mj.beko.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by Administrator on 2017/10/23/023.
 */

@Data
@Entity
@Table(name = "t_product_repair")
public class ProductRepair implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "bottom_place_code")
    private String bottomPlaceCode;

    @Column(name = "product_no")
    private String productNo;


    @Column(name = "repair_reason")
    private String repairReason;

    @Column(name = "state")
    private String state;

    @Column(name = "start_time")
    private Timestamp startTime;

    @Column(name = "end_time")
    private Timestamp endTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ProductRepair that = (ProductRepair) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (bottomPlaceCode != null ? !bottomPlaceCode.equals(that.bottomPlaceCode) : that.bottomPlaceCode != null)
            return false;
        if (productNo != null ? !productNo.equals(that.productNo) : that.productNo != null) return false;
        if (repairReason != null ? !repairReason.equals(that.repairReason) : that.repairReason != null) return false;
        if (state != null ? !state.equals(that.state) : that.state != null) return false;
        if (startTime != null ? !startTime.equals(that.startTime) : that.startTime != null) return false;
        return endTime != null ? endTime.equals(that.endTime) : that.endTime == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (bottomPlaceCode != null ? bottomPlaceCode.hashCode() : 0);
        result = 31 * result + (productNo != null ? productNo.hashCode() : 0);
        result = 31 * result + (repairReason != null ? repairReason.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ProductRepair{" +
                "id=" + id +
                ", bottomPlaceCode='" + bottomPlaceCode + '\'' +
                ", productNo=" + productNo +
                ", repairReason='" + repairReason + '\'' +
                ", state='" + state + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
