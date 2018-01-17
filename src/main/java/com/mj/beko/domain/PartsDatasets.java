package com.mj.beko.domain;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author wanghb
 * 零件数据集实体
 */
@Entity
@Table(name = "parts_datasets")
@Data
public class PartsDatasets implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    /**
     * 零件数据集的键
     */
    @Column(name = "part_key")
    private String partKey;

    /**
     * 零件数据集的值
     */
    @Column(name = "part_value")
    private String partValue;

    @ManyToOne
    private ConsumedParts consumedParts;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PartsDatasets partsDatasets = (PartsDatasets) o;
        if (partsDatasets.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), partsDatasets.getId());
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "PartsDatasets{" +
                "id=" + getId() +
                ", partKey='" + getPartKey() + "'" +
                ", partValue='" + getPartValue() + "'" +
                "}";
    }
}
