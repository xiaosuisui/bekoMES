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
 * 工序(Operation)实体
 */
@Entity
@Table(name = "t_operation")
@Data
public class Operation implements Serializable{

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    /**
     * 工艺ID
     */
    @Column(name = "operation_id")
    private String operationId;

    /**
     * 工序名称
     */
    @Column(name = "operation_name")
    private String operationName;

    /**
     * 工序描述
     */
    @Column(name = "operation_desc")
    private String operationDesc;

    /**
     * 工序序号
     */
    @Column(name = "seq_no")
    private String seqNo;

    /**
     * 工艺配置
     */
    @Column(name = "operation_allocation")
    private String operationAllocation;

    /**
     * 操作持续时间
     */
    @Column(name = "operation_duration")
    private String operationDuration;

    /**
     * 人工操作的标准时间
     */
    @Column(name = "operation_std_man_sec")
    private String operationStdManSec;

    /**
     * 操作过程分类
     */
    @Column(name = "operation_process_category")
    private String operationProcessCategory;

    @ManyToOne
    @JsonIgnore
    private OrderStation orderStation;

    @OneToMany(mappedBy = "operation",fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<OperationDatasets> operationDatasets = new HashSet<>();

    @OneToMany(mappedBy = "operation")
    @JsonIgnore
    private Set<ConsumedParts> consumedParts = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Operation operation = (Operation) o;
        if (operation.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), operation.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Operation{" +
                "id=" + getId() +
                ", operationId='" + getOperationId() + "'" +
                ", operationName='" + getOperationName() + "'" +
                ", operationDesc='" + getOperationDesc() + "'" +
                ", seqNo='" + getSeqNo() + "'" +
                ", operationAllocation='" + getOperationAllocation() + "'" +
                ", operationDuration='" + getOperationDuration() + "'" +
                ", operationStdManSec='" + getOperationStdManSec() + "'" +
                ", operationProcessCategory='" + getOperationProcessCategory() + "'" +
                "}";
    }
}
