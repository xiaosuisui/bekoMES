package com.mj.beko.repository;

import com.mj.beko.domain.Product;
import com.mj.beko.domain.ProductResourceFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Created by MOUNTAIN on 2017/10/25.
 */
public interface ProductResourceFileRepository extends JpaRepository<ProductResourceFile,Long>,JpaSpecificationExecutor{
    /**
     * 分页查询
     * @param offset
     * @param size
     * @return
     */
    @Query(value = "select * from t_product_resource_file ORDER BY id DESC OFFSET :offset ROW  FETCH NEXT :size ROW ONLY",nativeQuery = true)
    List<ProductResourceFile> queryByPage(@Param("offset") int offset, @Param("size") int size);

    /**
     * 通过productNo查找ProductResourceFile
     */
    Optional<ProductResourceFile> findOneByProductNo(String productNo);

    List<ProductResourceFile> findByWorkstationIdAndProductNoAndType(Long workstationId, String productNo, String type);
}
