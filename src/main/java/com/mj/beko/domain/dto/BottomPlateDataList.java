package com.mj.beko.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mj.beko.util.JsonReadingProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author wanghb
 * 用于接收下底盘信息集合
 */
@Data
public class BottomPlateDataList<T> implements Serializable{

    private static final long serialVersionUID = 1L;

    @JsonProperty("Table")
    private List<T> bottomPlateDataList;
}
