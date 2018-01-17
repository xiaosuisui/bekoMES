package com.mj.beko.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by Ricardo on 2017/11/11.
 */
@Entity
@Table(name = "production_data")
@Data
public class ProductionData implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;
    @Column(name = "barCode")
    private String barCode;

    private String orderNo;

    private String productNo;

    @Column(name = "operator")
    private String operator;

    @Column(name = "station")
    private String station;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "value")
    private String value;
    private Timestamp createTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ProductionData that = (ProductionData) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (barCode != null ? !barCode.equals(that.barCode) : that.barCode != null) return false;
        if (orderNo != null ? !orderNo.equals(that.orderNo) : that.orderNo != null) return false;
        if (productNo != null ? !productNo.equals(that.productNo) : that.productNo != null) return false;
        if (operator != null ? !operator.equals(that.operator) : that.operator != null) return false;
        if (station != null ? !station.equals(that.station) : that.station != null) return false;
        if (contentType != null ? !contentType.equals(that.contentType) : that.contentType != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;
        return createTime != null ? createTime.equals(that.createTime) : that.createTime == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (barCode != null ? barCode.hashCode() : 0);
        result = 31 * result + (orderNo != null ? orderNo.hashCode() : 0);
        result = 31 * result + (productNo != null ? productNo.hashCode() : 0);
        result = 31 * result + (operator != null ? operator.hashCode() : 0);
        result = 31 * result + (station != null ? station.hashCode() : 0);
        result = 31 * result + (contentType != null ? contentType.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ProductionData{" +
                "barCode='" + barCode + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", productNo='" + productNo + '\'' +
                ", operator='" + operator + '\'' +
                ", station='" + station + '\'' +
                ", contentType='" + contentType + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
