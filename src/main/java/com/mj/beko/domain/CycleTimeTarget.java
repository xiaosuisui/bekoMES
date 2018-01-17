package com.mj.beko.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by Ricardo on 2017/11/14.
 */
@Entity
@Data
public class CycleTimeTarget implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;
    private String productNo;
    private String lineId;
    private String target;
    private Timestamp updateTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        CycleTimeTarget that = (CycleTimeTarget) o;

        if (productNo != null ? !productNo.equals(that.productNo) : that.productNo != null) return false;
        if (lineId != null ? !lineId.equals(that.lineId) : that.lineId != null) return false;
        return target != null ? target.equals(that.target) : that.target == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (productNo != null ? productNo.hashCode() : 0);
        result = 31 * result + (lineId != null ? lineId.hashCode() : 0);
        result = 31 * result + (target != null ? target.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CycleTimeTarget{" +
                "productNo='" + productNo + '\'' +
                ", lineId='" + lineId + '\'' +
                ", target='" + target + '\'' +
                '}';
    }
}
