package com.mj.beko.domain.dto;

import com.mj.beko.util.JsonReadingProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wanghb
 * 用于接收下底盘信息
 */
@Data
public class BottomPlateData implements Serializable{

    private static final long serialVersionUID = 1L;

    @JsonReadingProperty("ID")
    private String id;

    @JsonReadingProperty("DummyNo")
    private String dummyNo;

    @JsonReadingProperty("TestStep")
    private String testStep;

    @JsonReadingProperty("TestStepID")
    private String testStepID;

    @JsonReadingProperty("TestData")
    private String testData;

    @JsonReadingProperty("TestStatu")
    private String testStatu;

    @JsonReadingProperty("OpCode")
    private String opCode;

    @JsonReadingProperty("PartNo")
    private String partNo;

    @JsonReadingProperty("ArcelikSendStatu")
    private String arcelikSendStatu;
}
