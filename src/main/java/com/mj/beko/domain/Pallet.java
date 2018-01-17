package com.mj.beko.domain;

import com.mj.beko.domain.enu.PalletState;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Ricardo on 2017/8/23.
 */
@Entity
@Table(name = "t_pallet")
@Data
public class Pallet implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "pallet_name")
    private String palletName;

    @Column(name = "pallet_no")
    private String palletNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private PalletState state;

    @Column(name = "current_order_no")
    private String currentOrderNo;

    @Column(name = "product_no")
    private String productNo;

    @Column(name = "bottom_place_code")
    private String bottomPlaceCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Pallet pallet = (Pallet) o;

        if (id != null ? !id.equals(pallet.id) : pallet.id != null) return false;
        if (palletName != null ? !palletName.equals(pallet.palletName) : pallet.palletName != null) return false;
        if (palletNo != null ? !palletNo.equals(pallet.palletNo) : pallet.palletNo != null) return false;
        if (state != pallet.state) return false;
        if (currentOrderNo != null ? !currentOrderNo.equals(pallet.currentOrderNo) : pallet.currentOrderNo != null)
            return false;
        if (productNo != null ? !productNo.equals(pallet.productNo) : pallet.productNo != null) return false;
        return bottomPlaceCode != null ? bottomPlaceCode.equals(pallet.bottomPlaceCode) : pallet.bottomPlaceCode == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (palletName != null ? palletName.hashCode() : 0);
        result = 31 * result + (palletNo != null ? palletNo.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (currentOrderNo != null ? currentOrderNo.hashCode() : 0);
        result = 31 * result + (productNo != null ? productNo.hashCode() : 0);
        result = 31 * result + (bottomPlaceCode != null ? bottomPlaceCode.hashCode() : 0);
        return result;
    }
}
