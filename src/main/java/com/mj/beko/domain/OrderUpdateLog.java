package com.mj.beko.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by Ricardo on 2017/11/9.
 */
@Entity
@Table(name = "t_order_updateLog")
@Data
public class OrderUpdateLog implements Serializable{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    private String username;
    private String moduleName;
    private String operatorType;
    private String operatorValue;
    private Timestamp operatorTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        OrderUpdateLog that = (OrderUpdateLog) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;
        if (moduleName != null ? !moduleName.equals(that.moduleName) : that.moduleName != null) return false;
        if (operatorType != null ? !operatorType.equals(that.operatorType) : that.operatorType != null) return false;
        if (operatorValue != null ? !operatorValue.equals(that.operatorValue) : that.operatorValue != null)
            return false;
        return operatorTime != null ? operatorTime.equals(that.operatorTime) : that.operatorTime == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (moduleName != null ? moduleName.hashCode() : 0);
        result = 31 * result + (operatorType != null ? operatorType.hashCode() : 0);
        result = 31 * result + (operatorValue != null ? operatorValue.hashCode() : 0);
        result = 31 * result + (operatorTime != null ? operatorTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "OrderUpdateiLog{" +
                "username='" + username + '\'' +
                ", moduleName='" + moduleName + '\'' +
                ", operatorType='" + operatorType + '\'' +
                ", operatorValue='" + operatorValue + '\'' +
                ", operatorTime=" + operatorTime +
                '}';
    }
}
