package com.mj.beko.service.impl;

import com.mj.beko.domain.CycleTimeTarget;
import com.mj.beko.domain.TvDataConfig;
import com.mj.beko.repository.TvDataConfigRepository;
import com.mj.beko.service.TvDataConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ricardo on 2017/11/17.
 * 电视配置参数的实现类
 */
@Service
@Transactional
@Slf4j
public class TvDataConfigServiceImpl implements TvDataConfigService {
    @Autowired
    private TvDataConfigRepository tvDataConfigRepository;
    @Override
    public TvDataConfig save(TvDataConfig tvDataConfig) {
        log.info("tvDataConfig save");
        return tvDataConfigRepository.save(tvDataConfig);
    }
    @Override
    public void delete(TvDataConfig tvDataConfig) {
        log.info("tvDataConfig delete");
        tvDataConfigRepository.delete(tvDataConfig);
    }
    @Override
    public List<TvDataConfig> query() {
        return null;
    }
    @Override
    public List<TvDataConfig> queryByPage(int page, int size) {
        return null;
    }

    @Override
    public TvDataConfig findOneById(Long id) {
        return tvDataConfigRepository.findOne(id);
    }

    @Override
    public long getAllTvDataConfigByCondition(String tvName) {
        Specification<TvDataConfig> specification = new Specification<TvDataConfig>() {
            @Override
            public Predicate toPredicate(Root<TvDataConfig> root,
                                         CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //条件判断
                if(null!=tvName && !"".equals(tvName) &&!"null".equals(tvName) && !tvName.equals("undefined") && !tvName.isEmpty()) {
                    Predicate _workstation = criteriaBuilder.equal(root.get("tvName"), tvName);
                    predicates.add(_workstation);
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
            }
        };
        return tvDataConfigRepository.count(specification);
    }

    @Override
    public Page<TvDataConfig> findAllTvDataConfigCondition(String tvName, int page, int size) {
        log.info("条件查询所有的cTvDataConfig");
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(page, size, sort);
        Specification<TvDataConfig> specification = new Specification<TvDataConfig>() {
            @Override
            public Predicate toPredicate(Root<TvDataConfig> root,
                                         CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //条件判断
                if(null!=tvName && !"".equals(tvName)&& !"null".equals(tvName) && !tvName.equals("undefined")) {
                    Predicate _productNo = criteriaBuilder.equal(root.get("tvName"), tvName);
                    predicates.add(_productNo);
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
            }
        };
        return tvDataConfigRepository.findAll(specification, pageable);
    }

    @Override
    public void delete(Long id) {
        tvDataConfigRepository.delete(id);
    }
}
