package com.mj.beko.domain;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by Ricardo on 2017/11/13.
 * downTime Data
 */
@Entity
@Data
@Slf4j
public class DownTimeData implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;
    private String operator;
    private String workstation;
    private String contentType;
    private String reason;
    private Timestamp createTime;
    private Timestamp endTime;
    private String status;
    private int countTime;
    public DownTimeData(){}
    public DownTimeData(String operator, String workstation, String reason) {
        this.operator = operator;
        this.workstation = workstation;
        this.reason = reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DownTimeData that = (DownTimeData) o;

        if (operator != null ? !operator.equals(that.operator) : that.operator != null) return false;
        if (workstation != null ? !workstation.equals(that.workstation) : that.workstation != null) return false;
        if (reason != null ? !reason.equals(that.reason) : that.reason != null) return false;
        return createTime != null ? createTime.equals(that.createTime) : that.createTime == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (operator != null ? operator.hashCode() : 0);
        result = 31 * result + (workstation != null ? workstation.hashCode() : 0);
        result = 31 * result + (reason != null ? reason.hashCode() : 0);
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        return result;
    }
    @Override
    public String toString() {
        return "DownTimeData{" +
                "operator='" + operator + '\'' +
                ", workstation='" + workstation + '\'' +
                ", reason='" + reason + '\'' +
                ", createTime=" + createTime +
                ", endTime=" + endTime +
                '}';
    }
}
