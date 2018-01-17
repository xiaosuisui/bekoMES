package com.mj.beko.domain;

import javafx.util.converter.TimeStringConverter;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by Ricardo on 2017/11/11.
 * 测试工位数据
 */
@Entity
@Table(name = "test_station_data")
@Data
public class TestStationData implements Serializable {
    private static final long serialVersionUID = 1L;
    private String orderNo;
    private String productNo;
    private String barCode;
    private String step;
    private String contentType;
    private String value;
    private String result;
    private Timestamp createTime;
    public TestStationData(){}

    /**
     * 构造函数
     * @param orderNo
     * @param productNo
     * @param barCode
     * @param step
     * @param contentType
     * @param value
     * @param result
     */
    public TestStationData(String orderNo, String productNo, String barCode, String step, String contentType, String value, String result) {
        this.orderNo = orderNo;
        this.productNo = productNo;
        this.barCode = barCode;
        this.step = step;
        this.contentType = contentType;
        this.value = value;
        this.result = result;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        TestStationData that = (TestStationData) o;

        if (orderNo != null ? !orderNo.equals(that.orderNo) : that.orderNo != null) return false;
        if (productNo != null ? !productNo.equals(that.productNo) : that.productNo != null) return false;
        if (barCode != null ? !barCode.equals(that.barCode) : that.barCode != null) return false;
        if (step != null ? !step.equals(that.step) : that.step != null) return false;
        if (contentType != null ? !contentType.equals(that.contentType) : that.contentType != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;
        return result != null ? result.equals(that.result) : that.result == null;
    }
    @Override
    public int hashCode() {
        int result1 = super.hashCode();
        result1 = 31 * result1 + (orderNo != null ? orderNo.hashCode() : 0);
        result1 = 31 * result1 + (productNo != null ? productNo.hashCode() : 0);
        result1 = 31 * result1 + (barCode != null ? barCode.hashCode() : 0);
        result1 = 31 * result1 + (step != null ? step.hashCode() : 0);
        result1 = 31 * result1 + (contentType != null ? contentType.hashCode() : 0);
        result1 = 31 * result1 + (value != null ? value.hashCode() : 0);
        result1 = 31 * result1 + (result != null ? result.hashCode() : 0);
        return result1;
    }

    @Override
    public String toString() {
        return "TestStationData{" +
                "orderNo='" + orderNo + '\'' +
                ", productNo='" + productNo + '\'' +
                ", barCode='" + barCode + '\'' +
                ", step='" + step + '\'' +
                ", contentType='" + contentType + '\'' +
                ", value='" + value + '\'' +
                ", result='" + result + '\'' +
                '}';
    }
}
