package com.mj.beko.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author wanghb
 * 工位与工艺信息中间表
 */
@Entity
@Table(name = "order_station")
@Data
public class OrderStation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    /**
     * 产品类型
     */
    @Column(name = "product_no")
    private String productNo;

    @ManyToOne
    private Workstation workstation;

    @OneToMany(mappedBy = "orderStation")
    @JsonIgnore
    private Set<Operation> operations = new HashSet<>();

    @OneToMany(mappedBy = "orderStation")
    @JsonIgnore
    private Set<StationDatasets> stationDatasets = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OrderStation orderStation = (OrderStation) o;
        if (orderStation.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), orderStation.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "OrderStation{" +
                "id=" + getId() +
                ", productNo='" + getProductNo() + "'" +
                "}";
    }
}
