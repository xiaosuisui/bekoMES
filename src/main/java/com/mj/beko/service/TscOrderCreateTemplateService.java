package com.mj.beko.service;

import com.mj.beko.domain.TscOrderCreateTemplate;

import java.util.List;

/**
 * Created by Ricardo on 2017/8/24.
 */
public interface TscOrderCreateTemplateService {
    /**
     * Save a tscOrderCreateTemplate.
     *
     * @param tscOrderCreateTemplate the entity to save
     * @return the persisted entity
     */
    TscOrderCreateTemplate save(TscOrderCreateTemplate tscOrderCreateTemplate);

    /**
     *  Get all the tscOrderCreateTemplates.
     *
     *  @return the list of entities
     */
    List<TscOrderCreateTemplate> findAll();

    /**
     *  Get the "id" tscOrderCreateTemplate.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    TscOrderCreateTemplate findOne(Long id);

    /**
     *  Delete the "id" tscOrderCreateTemplate.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);
    /*通过名称查询对应的调度参数*/
    List<TscOrderCreateTemplate> findAllByNameAndFunctionName(String name,String functionName);
}
