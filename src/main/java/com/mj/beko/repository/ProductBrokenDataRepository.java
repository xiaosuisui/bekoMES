package com.mj.beko.repository;

import com.mj.beko.domain.Pallet;
import com.mj.beko.domain.ProductBrokenData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Created by Ricardo on 2018/1/13.
 * 查询坏件的数量
 */
@Repository
public interface ProductBrokenDataRepository extends JpaRepository<ProductBrokenData,Long>,JpaSpecificationExecutor {

    @Query(value = "select top 1 * from t_product_broken_data where bottom_plate_bar_code=:bottomPlateBarCode",nativeQuery = true)
    ProductBrokenData getProductBrokenDataByBottomPlateBarCode(@Param("bottomPlateBarCode") String bottomPlateBarCode);
}
