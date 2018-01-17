package com.mj.beko.repository;

import com.mj.beko.domain.ProductCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Ricardo on 2017/8/24.
 */
@Repository
public interface ProductCodeRepository extends JpaRepository<ProductCode,Long>,JpaSpecificationExecutor{
    /**
     * 分页查询
     * @param offset
     * @param size
     * @return
     */
    @Query(value = "select * from t_product_code ORDER BY id DESC OFFSET :offset ROW  FETCH NEXT :size ROW ONLY",nativeQuery = true)
    List<ProductCode> queryByPage(@Param("offset") int offset, @Param("size") int size);

    /**
     *根据epsCode查找匹配的productInfo
     * @param epsCode
     * @return
     */
    @Query(value = "select top 1 * from t_product_code where eps_code=:epsCode order by id DESC",nativeQuery = true)
    ProductCode getProductCodeByEpsCode(@Param("epsCode") String epsCode);

    /**
     * 根据状态查询最老的一条记录
     * @param status
     * @return
     */
    @Query(value = "select top 1 * from t_product_code where status = ?1 and eps_code is null order by create_date", nativeQuery = true)
    ProductCode getOldProductCodeByStatus(String status);

    @Query(value = "select top 1 * from t_product_code where status = ?1 order by create_date", nativeQuery = true)
    ProductCode getOldProductCodeOnlyByStatus(String status);

    /**
     * 通过下底盘条码查询productCode
     * @param bottomPlateBarCode
     * @return
     */
    @Query(value = "select top 1 * from t_product_code where product_code =:bottomPlateBarCode order by id",nativeQuery = true)
    ProductCode getProductCodeByBottomPlateBarCode(@Param("bottomPlateBarCode") String bottomPlateBarCode);

    /**
     * 通过eps条码查询当前的数据库中有没有扫到epsCode或者已经给打印机发过条码的记录
     * @param epsCode
     * @return
     */
    @Query(value = "select top 1 * from t_product_code where eps_code=:epsCode and (status ='1' or status ='2')",nativeQuery = true)
    ProductCode getProductCodeByEpsCodeAndStatus(@Param("epsCode") String epsCode);

    /**
     * get product code by bottomPlateCode
     * @param bottomPlateBarcode
     * @return
     */
    @Query(value = "select  top 1 * from t_product_code where product_code=:bottomPlateBarcode",nativeQuery = true)
    ProductCode getProductCodeByBottomPlateCode(@Param("bottomPlateBarcode") String bottomPlateBarcode);
}
