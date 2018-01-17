package com.mj.beko.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Ricardo on 2017/12/17.
 * 托盘的类型和容量,对应到工位上,计算可以运输多少辆车
 */
@Entity
@Data
public class PalletTypeAndCapility implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;
    private String workStation;//工位
    private String type;//类型
    private String capility;//标准的生产容量
    private String workStationAlis;//别名
    private String extraField01;//预留字段01
    private String extraField02;//预留字段02
    private String extraField03;//预留字段03

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PalletTypeAndCapility that = (PalletTypeAndCapility) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (workStation != null ? !workStation.equals(that.workStation) : that.workStation != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (capility != null ? !capility.equals(that.capility) : that.capility != null) return false;
        if (workStationAlis != null ? !workStationAlis.equals(that.workStationAlis) : that.workStationAlis != null)
            return false;
        if (extraField01 != null ? !extraField01.equals(that.extraField01) : that.extraField01 != null) return false;
        if (extraField02 != null ? !extraField02.equals(that.extraField02) : that.extraField02 != null) return false;
        return extraField03 != null ? extraField03.equals(that.extraField03) : that.extraField03 == null;
    }
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (workStation != null ? workStation.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (capility != null ? capility.hashCode() : 0);
        result = 31 * result + (workStationAlis != null ? workStationAlis.hashCode() : 0);
        result = 31 * result + (extraField01 != null ? extraField01.hashCode() : 0);
        result = 31 * result + (extraField02 != null ? extraField02.hashCode() : 0);
        result = 31 * result + (extraField03 != null ? extraField03.hashCode() : 0);
        return result;
    }
    @Override
    public String toString() {
        return "PalletTypeAndCapility{" +
                "id=" + id +
                ", workStation='" + workStation + '\'' +
                ", type='" + type + '\'' +
                ", capility='" + capility + '\'' +
                ", workStationAlis='" + workStationAlis + '\'' +
                ", extraField01='" + extraField01 + '\'' +
                ", extraField02='" + extraField02 + '\'' +
                ", extraField03='" + extraField03 + '\'' +
                '}';
    }
}
