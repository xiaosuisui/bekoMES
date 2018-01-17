package com.mj.beko.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A TscOrderCreateTemplate.
 */
@Entity
@Table(name = "tsc_order_create_template")
@Data
public class TscOrderCreateTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    private String palletEmptyPoint;// agv 空托盘点

    private String  palletOutPoint;//下空托盘点

    private String materialStartPoint;//物料上料点

    private String materialOutPoint;//物料下料点

    private String secMaterialOutPoint;//第二物料下料点(EPS专用)

    private String stopCarPoint;//停车点

    private String station;//对应的工位名称

    private String name;//别名(预留字段)

    private String verticalType;//小车类型

    private String functionName;//功能名称(预留)

    private String node01;//预留三个节点

    private String node02;//预留三个节点

    private String node03;//预留三个节点

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        TscOrderCreateTemplate that = (TscOrderCreateTemplate) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (palletEmptyPoint != null ? !palletEmptyPoint.equals(that.palletEmptyPoint) : that.palletEmptyPoint != null)
            return false;
        if (palletOutPoint != null ? !palletOutPoint.equals(that.palletOutPoint) : that.palletOutPoint != null)
            return false;
        if (materialStartPoint != null ? !materialStartPoint.equals(that.materialStartPoint) : that.materialStartPoint != null)
            return false;
        if (materialOutPoint != null ? !materialOutPoint.equals(that.materialOutPoint) : that.materialOutPoint != null)
            return false;
        if (stopCarPoint != null ? !stopCarPoint.equals(that.stopCarPoint) : that.stopCarPoint != null) return false;
        if (station != null ? !station.equals(that.station) : that.station != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return verticalType != null ? verticalType.equals(that.verticalType) : that.verticalType == null;
    }
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (palletEmptyPoint != null ? palletEmptyPoint.hashCode() : 0);
        result = 31 * result + (palletOutPoint != null ? palletOutPoint.hashCode() : 0);
        result = 31 * result + (materialStartPoint != null ? materialStartPoint.hashCode() : 0);
        result = 31 * result + (materialOutPoint != null ? materialOutPoint.hashCode() : 0);
        result = 31 * result + (stopCarPoint != null ? stopCarPoint.hashCode() : 0);
        result = 31 * result + (station != null ? station.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (verticalType != null ? verticalType.hashCode() : 0);
        return result;
    }
    @Override
    public String toString() {
        return "TscOrderCreateTemplate{" +
                "id=" + id +
                ", palletEmptyPoint='" + palletEmptyPoint + '\'' +
                ", palletOutPoint='" + palletOutPoint + '\'' +
                ", materialStartPoint='" + materialStartPoint + '\'' +
                ", materialOutPoint='" + materialOutPoint + '\'' +
                ", stopCarPoint='" + stopCarPoint + '\'' +
                ", station='" + station + '\'' +
                ", name='" + name + '\'' +
                ", verticalType='" + verticalType + '\'' +
                '}';
    }
}
