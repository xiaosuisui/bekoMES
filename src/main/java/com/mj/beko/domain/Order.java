package com.mj.beko.domain;

import com.mj.beko.util.JsonReadingProperty;
import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by Ricardo on 2017/8/23.
 */
@Entity
@Table(name = "t_order")
@Data
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    //订单编号
    @NotNull
    @Column(name = "order_no", nullable = false)
    @JsonReadingProperty("OrderNo")
    private String orderNo;

    //产品类型
    @Column(name = "product_no")
    @JsonReadingProperty("SapNo")
    private String productNo;

    //数量
    @Column(name = "quantity")
    @JsonReadingProperty("Quantity")
    private int quantity;

    //订单计划时间
    @Column(name = "operation_date_time")
    @JsonReadingProperty("OperationDateTime")
    private Timestamp operationDateTime;

    //订单实际开始日期
    @Column(name = "start_date")
    private Timestamp startDate;

    //结束日期
    @Column(name = "end_date")
    private Timestamp endDate;

    //状态
    @Column(name = "status")
    private String status;

    //上线数量(基于第一工位)
    @Column(name = "online_number")
    private int onlineNumber;

    //返修数量
    @Column(name = "repair_number")
    private int repairNumber;
    //坏件数量
    private int brokenNumber;
    //完成数量
    @Column(name = "completion_number")
    private int completionNumber;
    @Column(name = "repair02_broken")
    private int repair02Broken;
    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderNo='" + orderNo + '\'' +
                ", productNo='" + productNo + '\'' +
                ", quantity=" + quantity +
                ", operationDateTime=" + operationDateTime +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status='" + status + '\'' +
                ", onlineNumber=" + onlineNumber +
                ", repairNumber=" + repairNumber +
                ", brokenNumber=" + brokenNumber +
                ", completionNumber=" + completionNumber +
                '}';
    }
}
