package com.mj.beko.domain;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
/**
 * Created by Ricardo on 2017/12/8.
 * 不同的产品对应的plc类型(二段)
 */
@Entity
@Data
@Table(name = "t_mestoplctype")
public class MesToPlcType implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;
    private String productId;//产品Id
    private String partNumber;//零件号
    private String node;//节点名称;(预留字段写入的节点名称)
    private String plcStation;//对应的plc区域(工位名称)
    private String value;//对应的plc区域的值

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        MesToPlcType that = (MesToPlcType) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (productId != null ? !productId.equals(that.productId) : that.productId != null) return false;
        if (partNumber != null ? !partNumber.equals(that.partNumber) : that.partNumber != null) return false;
        if (plcStation != null ? !plcStation.equals(that.plcStation) : that.plcStation != null) return false;
        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (productId != null ? productId.hashCode() : 0);
        result = 31 * result + (partNumber != null ? partNumber.hashCode() : 0);
        result = 31 * result + (plcStation != null ? plcStation.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
    @Override
    public String toString() {
        return "MesToPlcType{" +
                "id=" + id +
                ", productId='" + productId + '\'' +
                ", partNumber='" + partNumber + '\'' +
                ", plcStation='" + plcStation + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
