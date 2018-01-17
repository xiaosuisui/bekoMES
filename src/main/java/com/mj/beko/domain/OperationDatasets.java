package com.mj.beko.domain;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author wanghb
 * 工序数据集实体
 */
@Entity
@Table(name = "operation_datasets")
@Data
public class OperationDatasets implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    /**
     * 工序数据集的键
     */
    @Column(name = "operation_key")
    private String operationKey;

    /**
     * 工序数据集的值
     */
    @Column(name = "operation_value")
    private String operationValue;

    @ManyToOne
    private Operation operation;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OperationDatasets operationDatasets = (OperationDatasets) o;
        if (operationDatasets.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), operationDatasets.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "OperationDatasets{" +
                "id=" + getId() +
                ", operationKey='" + getOperationKey() + "'" +
                ", operationValue='" + getOperationValue() + "'" +
                "}";
    }
}
