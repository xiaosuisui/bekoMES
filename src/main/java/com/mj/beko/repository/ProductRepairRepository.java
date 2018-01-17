package com.mj.beko.repository;

import com.mj.beko.domain.ProductRepair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2017/10/23/023.
 */

@Repository
public interface ProductRepairRepository extends JpaRepository<ProductRepair,Long> ,JpaSpecificationExecutor{

    @Query(value = "select * from t_product_repair ORDER BY id DESC OFFSET :offset ROW  FETCH NEXT :size ROW ONLY",nativeQuery = true)
    List<ProductRepair> queryByPage(@Param("offset") int offset , @Param("size") int size);

    /**
     * 根据下底盘条码和State查询返修记录数
     * @param bottomPlateBarcode
     * @param state
     * @return
     */
    @Query(value = "select count(0) from t_product_repair where bottom_place_code = ?1 and state = ?2",nativeQuery = true)
    int getCountByBarvodeAndState(String bottomPlateBarcode, String state);

    /**
     * 查询下底盘条码的返修记录,并且原因为空的记录,填写原因
     * @param bottomPlateBarCode
     * @return
     */
    @Query(value = "select top 1 * from t_product_repair where bottom_place_code=:bottomPlateBarCode and state=:state and repair_reason is null ",nativeQuery = true)
    ProductRepair getProductRepairByBottomBarCode(@Param("bottomPlateBarCode") String bottomPlateBarCode,@Param("state") String state);
}
