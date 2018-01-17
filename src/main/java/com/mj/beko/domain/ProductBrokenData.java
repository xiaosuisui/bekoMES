package com.mj.beko.domain;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by Ricardo on 2018/1/13.
 * product broken data。产品计件的数量
 */
@Entity
@Table(name = "t_product_broken_data")
@Data
public class ProductBrokenData {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;
    private String orderNo;//订单号
    private String productId;//产品信息
    private String bottomPlateBarCode;//下底盘条码
    private Timestamp createTime;//创建日期
    private String stationName; //创建工位
    private String extraField01;//预留字段01
    private String extraField02;//预留字段02
    private String extraField03;//预留字段03

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ProductBrokenData that = (ProductBrokenData) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (orderNo != null ? !orderNo.equals(that.orderNo) : that.orderNo != null) return false;
        if (productId != null ? !productId.equals(that.productId) : that.productId != null) return false;
        if (bottomPlateBarCode != null ? !bottomPlateBarCode.equals(that.bottomPlateBarCode) : that.bottomPlateBarCode != null)
            return false;
        if (createTime != null ? !createTime.equals(that.createTime) : that.createTime != null) return false;
        if (stationName != null ? !stationName.equals(that.stationName) : that.stationName != null) return false;
        if (extraField01 != null ? !extraField01.equals(that.extraField01) : that.extraField01 != null) return false;
        if (extraField02 != null ? !extraField02.equals(that.extraField02) : that.extraField02 != null) return false;
        return extraField03 != null ? extraField03.equals(that.extraField03) : that.extraField03 == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (orderNo != null ? orderNo.hashCode() : 0);
        result = 31 * result + (productId != null ? productId.hashCode() : 0);
        result = 31 * result + (bottomPlateBarCode != null ? bottomPlateBarCode.hashCode() : 0);
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        result = 31 * result + (stationName != null ? stationName.hashCode() : 0);
        result = 31 * result + (extraField01 != null ? extraField01.hashCode() : 0);
        result = 31 * result + (extraField02 != null ? extraField02.hashCode() : 0);
        result = 31 * result + (extraField03 != null ? extraField03.hashCode() : 0);
        return result;
    }
    @Override
    public String toString() {
        return "ProductBrokenData{" +
                "id=" + id +
                ", orderNo='" + orderNo + '\'' +
                ", productId='" + productId + '\'' +
                ", bottomPlateBarCode='" + bottomPlateBarCode + '\'' +
                ", createTime=" + createTime +
                ", stationName='" + stationName + '\'' +
                ", extraField01='" + extraField01 + '\'' +
                ", extraField02='" + extraField02 + '\'' +
                ", extraField03='" + extraField03 + '\'' +
                '}';
    }
}
