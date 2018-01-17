package com.mj.beko.domain.dto;

/**
 * @author wanghb
 * 用于调用beko接口的订单VO
 */
public class OrderDto {
    private String sapLineId;
    private String startDate;
    private String endDate;

    public String getSapLineId() {
        return sapLineId;
    }

    public void setSapLineId(String sapLineId) {
        this.sapLineId = sapLineId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
