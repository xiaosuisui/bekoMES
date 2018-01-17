package com.mj.beko.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by Ricardo on 2017/11/6.
 */
@Entity
@Table(name = "t_operator_login")
@Data
public class OperatorLoginData implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")

    private Long id;
    @Column(name = "operator")
    private String operator;

    @Column(name = "operation")
    private String operation;

    @Column(name = "operation_time")
    private Timestamp operationTime;

    @Column(name = "workstation")
    private String workstation;
    public OperatorLoginData(){}

    public OperatorLoginData(String operator, String operation, String workstation) {
        this.operator = operator;
        this.operation = operation;
        this.workstation = workstation;
    }

    public OperatorLoginData(String operator, String operation, Timestamp operationTime, String workstation) {
        this.operator = operator;
        this.operation = operation;
        this.operationTime = operationTime;
        this.workstation = workstation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        OperatorLoginData that = (OperatorLoginData) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (operator != null ? !operator.equals(that.operator) : that.operator != null) return false;
        if (operation != null ? !operation.equals(that.operation) : that.operation != null) return false;
        if (operationTime != null ? !operationTime.equals(that.operationTime) : that.operationTime != null)
            return false;
        return workstation != null ? workstation.equals(that.workstation) : that.workstation == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (operator != null ? operator.hashCode() : 0);
        result = 31 * result + (operation != null ? operation.hashCode() : 0);
        result = 31 * result + (operationTime != null ? operationTime.hashCode() : 0);
        result = 31 * result + (workstation != null ? workstation.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "OperatorLoginData{" +
                "operator='" + operator + '\'' +
                ", operation='" + operation + '\'' +
                ", operationTime=" + operationTime +
                ", workstation='" + workstation + '\'' +
                '}';
    }
}
