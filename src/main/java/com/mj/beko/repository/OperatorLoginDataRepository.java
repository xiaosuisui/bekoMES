package com.mj.beko.repository;

import com.mj.beko.domain.OperatorLoginData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Created by Ricardo on 2017/11/6.
 */
@Repository
public interface OperatorLoginDataRepository extends JpaRepository<OperatorLoginData,Long>,JpaSpecificationExecutor {
    /**
     * 通过Id查询
     * @param id
     * @return
     */
    OperatorLoginData findOneById(Long id);
    /**
     *查询工位的当前的登录用户
     * @param workstation
     * @return
     */
    @Query(value = "select Top 1 * from t_operator_login where workstation=:workstation and operation='loginIn'order by id desc",nativeQuery = true)
    OperatorLoginData findOnByWorkstation(@Param("workstation") String workstation);
}
