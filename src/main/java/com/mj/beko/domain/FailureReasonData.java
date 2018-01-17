package com.mj.beko.domain;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by Ricardo on 2017/11/16.
 * 用来采集失败原因的
 */
@Entity
@Slf4j
@Data
public class FailureReasonData implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;
    private String productNo;
    private String barCode; //下底盘条码
    private String operator;
    private String workstation;
    private String point; //位置
    private String reason;
    private Timestamp createTime;
    private String status;//记录进入返修记录的状态
    public FailureReasonData(){}
    //构造函数
    public FailureReasonData(String workstation,String operator,String reason){
        this.workstation=workstation;
        this.operator=operator;
        this.reason=reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        FailureReasonData that = (FailureReasonData) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (productNo != null ? !productNo.equals(that.productNo) : that.productNo != null) return false;
        if (barCode != null ? !barCode.equals(that.barCode) : that.barCode != null) return false;
        if (operator != null ? !operator.equals(that.operator) : that.operator != null) return false;
        if (workstation != null ? !workstation.equals(that.workstation) : that.workstation != null) return false;
        if (point != null ? !point.equals(that.point) : that.point != null) return false;
        if (reason != null ? !reason.equals(that.reason) : that.reason != null) return false;
        return createTime != null ? createTime.equals(that.createTime) : that.createTime == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (productNo != null ? productNo.hashCode() : 0);
        result = 31 * result + (barCode != null ? barCode.hashCode() : 0);
        result = 31 * result + (operator != null ? operator.hashCode() : 0);
        result = 31 * result + (workstation != null ? workstation.hashCode() : 0);
        result = 31 * result + (point != null ? point.hashCode() : 0);
        result = 31 * result + (reason != null ? reason.hashCode() : 0);
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FailureReasonData{" +
                "id=" + id +
                ", productNo='" + productNo + '\'' +
                ", barCode='" + barCode + '\'' +
                ", operator='" + operator + '\'' +
                ", workstation='" + workstation + '\'' +
                ", point='" + point + '\'' +
                ", reason='" + reason + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
