package com.mj.beko.domain;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import javax.persistence.*;

/**
 * Created by Ricardo on 2017/12/13.
 */
@Data
@Entity
@Slf4j
public class MesToFlowTestRange {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;
    private String productId;
    private String step01Rnage;//step01 range
    private String step02Range;//step02 range
    private String step03Range;//step03 range
    private String step04Range;//steo04 range
    private String extraField01;//extra field
    private String extraField02;//extra field
    private String screws;
    private String plc2Robot;
    private String fires;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MesToFlowTestRange that = (MesToFlowTestRange) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (productId != null ? !productId.equals(that.productId) : that.productId != null) return false;
        if (step01Rnage != null ? !step01Rnage.equals(that.step01Rnage) : that.step01Rnage != null) return false;
        if (step02Range != null ? !step02Range.equals(that.step02Range) : that.step02Range != null) return false;
        if (step03Range != null ? !step03Range.equals(that.step03Range) : that.step03Range != null) return false;
        if (step04Range != null ? !step04Range.equals(that.step04Range) : that.step04Range != null) return false;
        if (extraField01 != null ? !extraField01.equals(that.extraField01) : that.extraField01 != null) return false;
        if (screws != null ? !screws.equals(that.screws) : that.screws != null) return false;
        if (plc2Robot != null ? !plc2Robot.equals(that.plc2Robot) : that.plc2Robot != null) return false;
        if (fires != null ? !fires.equals(that.fires) : that.fires != null) return false;
        return extraField02 != null ? extraField02.equals(that.extraField02) : that.extraField02 == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (productId != null ? productId.hashCode() : 0);
        result = 31 * result + (step01Rnage != null ? step01Rnage.hashCode() : 0);
        result = 31 * result + (step02Range != null ? step02Range.hashCode() : 0);
        result = 31 * result + (step03Range != null ? step03Range.hashCode() : 0);
        result = 31 * result + (step04Range != null ? step04Range.hashCode() : 0);
        result = 31 * result + (extraField01 != null ? extraField01.hashCode() : 0);
        result = 31 * result + (extraField02 != null ? extraField02.hashCode() : 0);
        result = 31 * result + (screws != null ? screws.hashCode() : 0);
        result = 31 * result + (plc2Robot != null ? plc2Robot.hashCode() : 0);
        result = 31 * result + (fires != null ? fires.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MesToFlowTestRange{" +
                "id=" + id +
                ", productId='" + productId + '\'' +
                ", step01Rnage='" + step01Rnage + '\'' +
                ", step02Range='" + step02Range + '\'' +
                ", step03Range='" + step03Range + '\'' +
                ", step04Range='" + step04Range + '\'' +
                ", extraField01='" + extraField01 + '\'' +
                ", extraField02='" + extraField02 + '\'' +
                ", screws='" + screws + '\'' +
                ", plc2Robot='" + plc2Robot + '\'' +
                ", fires='" + fires + '\'' +
                '}';
    }
}