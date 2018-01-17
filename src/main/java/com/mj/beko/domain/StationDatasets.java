package com.mj.beko.domain;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author wanghb
 * 工位数据集实体
 */
@Entity
@Table(name = "station_datasets")
@Data
public class StationDatasets implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    /**
     * 工位数据集的键
     */
    @Column(name = "station_key")
    private String stationKey;

    /**
     * 工位数据集的值
     */
    @Column(name = "station_value")
    private String stationValue;

    @ManyToOne
    private OrderStation orderStation;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StationDatasets stationDatasets = (StationDatasets) o;
        if (stationDatasets.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), stationDatasets.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "StationDatasets{" +
                "id=" + getId() +
                ", stationKey='" + getStationKey() + "'" +
                ", stationValue='" + getStationValue() + "'" +
                "}";
    }
}
