package com.mj.beko.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author wanghb
 * 核心零件实体
 */
@Entity
@Table(name = "consumed_parts")
@Data
public class ConsumedParts implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    /**
     * 零件ID
     */
    @Column(name = "part_id")
    private String partId;

    /**
     * 零件名称
     */
    @Column(name = "part_name")
    private String partName;

    /**
     * 零件描述
     */
    @Column(name = "part_desc")
    private String partDesc;

    /**
     * 零件发布版本
     */
    @Column(name = "part_date_released")
    private String partDateReleased;

    @ManyToOne
    private Operation operation;

    @OneToMany(mappedBy = "consumedParts")
    @JsonIgnore
    private Set<PartsDatasets> partsDatasets = new HashSet<>();
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConsumedParts consumedParts = (ConsumedParts) o;
        if (consumedParts.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), consumedParts.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ConsumedParts{" +
                "id=" + getId() +
                ", partName='" + getPartName() + "'" +
                ", partDateReleased='" + getPartDateReleased() + "'" +
                "}";
    }
}
