package com.mj.beko.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author wanghb
 * 工位实体
 */
@Entity
@Table(name = "t_workstation")
@Data
public class Workstation {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    /**
     * 工位ID
     */
    @Column(name = "station_id")
    private String stationId;

    /**
     * 工位名
     */
    @Column(name = "station_name")
    private String stationName;

    /**
     * 工位描述
     */
    @Column(name = "station_desc")
    private String stationDesc;

    /**
     * 工位序号
     */
    @Column(name = "seq_no")
    private String seqNo;

    /**
     * 工位配置百分比
     */
    @Column(name = "station_allocation_percent")
    private String stationAllocationPercent;

    /**
     * 工位最后修改时间
     */
    @Column(name = "station_last_modified_date")
    private Timestamp stationLastModifiedDate;

    @OneToMany(mappedBy = "workstation")
    @JsonIgnore
    private Set<OrderStation> orderStations = new HashSet<OrderStation>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Workstation workstation = (Workstation) o;
        if (workstation.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), workstation.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Workstation{" +
                "id=" + getId() +
                ", stationId='" + getStationId() + "'" +
                ", stationName='" + getStationName() + "'" +
                ", stationDesc='" + getStationDesc() + "'" +
                ", seqNo='" + getSeqNo() + "'" +
                ", stationAllocationPercent='" + getStationAllocationPercent() + "'" +
                ", stationLastModifiedDate='" + getStationLastModifiedDate() + "'" +
                "}";
    }
}
