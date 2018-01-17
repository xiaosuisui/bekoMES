package com.mj.beko.repository;

import com.mj.beko.domain.Pallet;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Created by Ricardo on 2017/8/23.
 */
@Repository
public interface PalletRepository extends JpaRepository<Pallet,Long>,JpaSpecificationExecutor{
    /**
     * 通过编号查询
     * @param palletNo
     * @return
     */
    Optional<Pallet> findOneByPalletNo(String palletNo);
    /**
     * 分页查询 pallet
     * @param offset
     * @param size
     * @return
     */
    @Query(value = "select * from t_pallet ORDER BY id DESC OFFSET :offset ROW  FETCH NEXT :size ROW ONLY",nativeQuery = true)
    List<Pallet> queryByPage(@Param("offset") int offset, @Param("size") int size);

    @Modifying
    @Query(value = "UPDATE t_pallet SET bottom_place_code =?4,product_no=?3,current_order_no=?2 WHERE pallet_no=?1",nativeQuery = true)
    int setCurrentOrderNoAndProductNoAndBottomPlaceCode(String palletNo,String currentOrderNo, String productNo, String bottomPlaceCode);

    Pallet findPalletByPalletNo(String palletNo);

    /**
     * 根据托盘号清除托盘绑定的数据
     * @param palletNo
     */
    @Modifying
    @Query(value = "update t_pallet set bottom_place_code = null,product_no = null,current_order_no = null WHERE pallet_no=?1", nativeQuery = true)
    void clearPalletData(String palletNo);

    /**
     * 通过下底盘条码查找对应的产品信息
     * @param bottomPlateCode
     * @return
     */
    @Query(value = "select top 1 * from t_pallet where bottom_place_code=:bottomPlateCode ",nativeQuery = true)
    Pallet getPalletByBottomPlateCode(@Param("bottomPlateCode") String bottomPlateCode);
}
