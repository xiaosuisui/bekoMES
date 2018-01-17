package com.mj.beko.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by MOUNTAIN on 2017/10/25.
 */
@Entity
@Table(name = "t_product_resource_file")
@Data
public class ProductResourceFile implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    //productNo
    @Column(name = "product_no")
    private String productNo;

    //工位的id
    @Column(name = "workstation_id")
    private Long workstationId;

    //文件类型：PDF, Excel, Word, Video and Picture
    @Column(name = "type")
    private String type;

    //文件名 数据库中存储的值为:UUID+"_"+文件真实名称
    @Column(name = "storage_location")
    private String storageLocation;

}
