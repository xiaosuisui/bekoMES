package com.mj.beko.domain;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by Ricardo on 2018/1/5.
 */
@Entity
@Data
public class AgvToPlcMaterialType {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;
    private String robot;//robot编号
    private String materialNo;//物料编号
    private String stationName;//工位
    private String materialType;//物料类型
    private int plcType;//写给PLC的类型
}
