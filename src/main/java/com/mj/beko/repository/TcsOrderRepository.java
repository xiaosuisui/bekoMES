package com.mj.beko.repository;

import com.mj.beko.domain.TcsOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.jws.WebParam;
import java.util.List;

/**
 * Spring Data JPA repository for the TcsOrder entity.
 */
@Repository
public interface TcsOrderRepository extends JpaRepository<TcsOrder, Long> ,JpaSpecificationExecutor {

    @Query(value = "select * from tcs_order ORDER BY id DESC OFFSET :offset ROW  FETCH NEXT :size ROW ONLY", nativeQuery = true)
    List<TcsOrder> findAllTcsOrderByPage(@Param("offset") int offset, @Param("size") int size);

    /*通过调拨单名称查询对应的实体*/
    List<TcsOrder> findAllByTcsOrderName(String name);

    /**
     * 查询滚筒和流利架的前10条记录
     *
     * @return
     */
    @Query(value = "select top 10 * from tcs_order where(function_type='GUNTONG' OR function_type='LIULIJIA') order by id desc ", nativeQuery = true)
    List<TcsOrder> findTopTenTcsOrder();

    @Query(value = "select top 1 * from tcs_order where (function_type='EPSDOWN' OR function_type='EPSUP') AND tcs_order_name !=:tcsOrderName  order by id desc ", nativeQuery = true)
    TcsOrder getLastEpsTypeTcsOrder(@Param("tcsOrderName") String tcsOrderName);

    @Query(value = "select * from tcs_order where function_type='EPSDOWN' AND state != '5' AND state != '4' order by id desc", nativeQuery = true)
    List<TcsOrder> getLastEpsDownTypeTcsOrder();

    @Query(value = "select top 10 * from tcs_order where function_type='EPSUP'OR function_type='EPSDOWN' order by id desc ", nativeQuery = true)
    List<TcsOrder> getTopTenRecordForEps();

    @Query(value = "select top 10 * from tcs_order where function_type='BOTTOMANDTOPPLATE' order by id desc", nativeQuery = true)
    List<TcsOrder> getTopTenRecordForSupport();

    @Query(value = "select top 10 * from tcs_order where function_type='BOTTOMANDTOPPLATE' and state='3' order by  id desc", nativeQuery = true)
    List<TcsOrder> getLatestTcsOrderForTemplate();
}
