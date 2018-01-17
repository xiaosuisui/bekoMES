package com.mj.beko.domain;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by Ricardo on 2017/11/17.
 */
@Data
@Entity
@Slf4j
@Table(name = "t_tvConfig")
public class TvDataConfig  implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;
    private String tvName;
    private String isShow;
    private String pageName;
    private int frequence;
    private String pic;
    private Timestamp createDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        TvDataConfig that = (TvDataConfig) o;

        if (frequence != that.frequence) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (tvName != null ? !tvName.equals(that.tvName) : that.tvName != null) return false;
        if (pageName != null ? !pageName.equals(that.pageName) : that.pageName != null) return false;
        return pic != null ? pic.equals(that.pic) : that.pic == null;
    }
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (tvName != null ? tvName.hashCode() : 0);
        result = 31 * result + (pageName != null ? pageName.hashCode() : 0);
        result = 31 * result + frequence;
        result = 31 * result + (pic != null ? pic.hashCode() : 0);
        return result;
    }
    @Override
    public String toString() {
        return "TvDataConfig{" +
                "id=" + id +
                ", tvName='" + tvName + '\'' +
                ", pageName='" + pageName + '\'' +
                ", frequence=" + frequence +
                ", pic='" + pic + '\'' +
                '}';
    }
}
