package com.mj.beko.repository;

import com.mj.beko.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Created by Ricardo on 2017/8/24.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    /**
     * 分页查询
     * @param offset
     * @param size
     * @return
     */
    @Query(value = "select * from t_product ORDER BY id DESC OFFSET :offset ROW  FETCH NEXT :size ROW ONLY",nativeQuery = true)
    List<Product> queryByPage(@Param("offset") int offset, @Param("size") int size);
    /**
     * 通过productNo查找product
     */
    Optional<Product> findOneByProductNo(String productNo);
}
