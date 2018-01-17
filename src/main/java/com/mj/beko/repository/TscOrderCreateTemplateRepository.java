package com.mj.beko.repository;

import com.mj.beko.domain.TscOrderCreateTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Ricardo on 2017/8/24.
 */
@Repository
public interface TscOrderCreateTemplateRepository extends JpaRepository<TscOrderCreateTemplate,Long> {

    /*通过key和功能类型查询*/
    public List<TscOrderCreateTemplate> findAllByNameAndFunctionName(String name, String functionName);
}