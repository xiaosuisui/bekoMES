package com.mj.beko.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
/**
 * Created by Ricardo on 2017/8/24.
 */
@Data
@Entity
@Table(name = "t_product")
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "product_no")
    private String productNo;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "jhi_type")
    private String type;

    @Column(name = "qrcode")
    private String qrcode;

    @Column(name = "pic_path")
    private String picPath;

    @Column(name = "unit")
    private String unit;

    @Column(name = "pro_desc")
    private String desc;
    /*构建空的构造函数*/
    public Product(){}
    public Product(String productNo,String productName,String type,String qrcode){
        this.productNo=productNo;
        this.productName=productName;
        this.type=type;
        this.qrcode=qrcode;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Product product = (Product) o;
        if (product.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), product.getId());
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + getId() +
                ", productNo='" + getProductNo() + "'" +
                ", productName='" + getProductName() + "'" +
                ", type='" + getType() + "'" +
                ", qrcode='" + getQrcode() + "'" +
                ", picPath='" + getPicPath() + "'" +
                ", unit='" + getUnit() + "'" +
                ", desc='" + getDesc() + "'" +
                "}";
    }

}
