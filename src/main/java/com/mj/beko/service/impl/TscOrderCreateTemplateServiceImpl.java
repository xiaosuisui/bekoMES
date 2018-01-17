package com.mj.beko.service.impl;

import com.mj.beko.domain.TscOrderCreateTemplate;
import com.mj.beko.repository.TscOrderCreateTemplateRepository;
import com.mj.beko.service.TscOrderCreateTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Ricardo on 2017/8/24.
 */
@Service
@Transactional
@Slf4j
public class TscOrderCreateTemplateServiceImpl implements TscOrderCreateTemplateService {
    @Autowired
    private TscOrderCreateTemplateRepository tscOrderCreateTemplateRepository;
    /**
     * Save a tscOrderCreateTemplate.
     *
     * @param tscOrderCreateTemplate the entity to save
     * @return the persisted entity
     */
    @Override
    public TscOrderCreateTemplate save(TscOrderCreateTemplate tscOrderCreateTemplate) {
        log.debug("Request to save TscOrderCreateTemplate : {}", tscOrderCreateTemplate);
        return tscOrderCreateTemplateRepository.save(tscOrderCreateTemplate);
    }

    /**
     *  Get all the tscOrderCreateTemplates.
     *
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<TscOrderCreateTemplate> findAll() {
        log.debug("Request to get all TscOrderCreateTemplates");
        return tscOrderCreateTemplateRepository.findAll();
    }

    /**
     *  Get one tscOrderCreateTemplate by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public TscOrderCreateTemplate findOne(Long id) {
        log.debug("Request to get TscOrderCreateTemplate : {}", id);
        return tscOrderCreateTemplateRepository.findOne(id);
    }

    /**
     *  Delete the  tscOrderCreateTemplate by id.
     *
     *  @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete TscOrderCreateTemplate : {}", id);
        tscOrderCreateTemplateRepository.delete(id);
    }
    /**
     * 通过name和 function查询调拨单的参数
     * @param name
     * @param functionName
     * @return
     */
    @Override
   /* @Cacheable(value = "foo")*/
    public List<TscOrderCreateTemplate> findAllByNameAndFunctionName(String name,String functionName) {
        log.debug("通过name标识{}和功能类型{}查询调度单的参数",name,functionName);
        return tscOrderCreateTemplateRepository.findAllByNameAndFunctionName(name,functionName);
    }

}
