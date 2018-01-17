package com.mj.beko.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * Created by Ricardo on 2017/8/24.
 */
@Entity
@Table(name = "t_product_code")
@Data
public class ProductCode implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "product_no")
    private String productNo;//产品Id

    @Column(name = "eps_code")
    private String epsCode;//扫描到的eps条码

    @Column(name = "document_eps_code")
    private String documentEpsCode;//扫描到的document eps条码

    @Column(name = "package_eps_code")
    private String packageEpsCode;//扫描到的document eps条码

    @Column(name = "serial_no")
    private String serialNo;//call printer 生成的序列号

    @Column(name = "product_code")
    private String productCode; //第一工位的下底盘条码

    @Column(name = "order_no")
    private String orderNo;//该产品的订单号

    @Column(name = "status")
    private String status; //4个条码是否打印完成(0,1,2,3)

    @Column(name = "create_date")
    private Timestamp createDate;

    @Column(name = "print_date")
    private Timestamp printDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ProductCode that = (ProductCode) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (productNo != null ? !productNo.equals(that.productNo) : that.productNo != null) return false;
        if (epsCode != null ? !epsCode.equals(that.epsCode) : that.epsCode != null) return false;
        if (serialNo != null ? !serialNo.equals(that.serialNo) : that.serialNo != null) return false;
        if (productCode != null ? !productCode.equals(that.productCode) : that.productCode != null) return false;
        if (orderNo != null ? !orderNo.equals(that.orderNo) : that.orderNo != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (createDate != null ? !createDate.equals(that.createDate) : that.createDate != null) return false;
        return printDate != null ? printDate.equals(that.printDate) : that.printDate == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (productNo != null ? productNo.hashCode() : 0);
        result = 31 * result + (epsCode != null ? epsCode.hashCode() : 0);
        result = 31 * result + (serialNo != null ? serialNo.hashCode() : 0);
        result = 31 * result + (productCode != null ? productCode.hashCode() : 0);
        result = 31 * result + (orderNo != null ? orderNo.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        result = 31 * result + (printDate != null ? printDate.hashCode() : 0);
        return result;
    }
    @Override
    public String toString() {
        return "ProductCode{" +
                "id=" + id +
                ", productNo='" + productNo + '\'' +
                ", epsCode='" + epsCode + '\'' +
                ", serialNo='" + serialNo + '\'' +
                ", productCode='" + productCode + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", status='" + status + '\'' +
                ", createDate=" + createDate +
                ", printDate=" + printDate +
                '}';
    }
}
