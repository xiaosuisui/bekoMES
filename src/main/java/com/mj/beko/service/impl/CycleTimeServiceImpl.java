package com.mj.beko.service.impl;

import com.mj.beko.domain.CycleTimeTarget;
import com.mj.beko.domain.DownTimeData;
import com.mj.beko.repository.CycleTimeTargetRepository;
import com.mj.beko.service.CycleTimeTargetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ricardo on 2017/11/14.
 */
@Service
@Slf4j
public class CycleTimeServiceImpl implements CycleTimeTargetService {
    @Autowired
    private CycleTimeTargetRepository cycleTimeTargetRepository;
    @Override
    public CycleTimeTarget save(CycleTimeTarget cycleTimeTarget) {
        log.info("save cycleTimeTarget");
        return cycleTimeTargetRepository.save(cycleTimeTarget);
    }
    @Override
    public void delete(CycleTimeTarget cycleTimeTarget) {
        log.info("delete cycleTimeTarget");
        cycleTimeTargetRepository.delete(cycleTimeTarget);
    }

    @Override
    public List<CycleTimeTarget> query() {
        return null;
    }

    @Override
    public List<CycleTimeTarget> queryByPage(int page, int size) {
        return null;
    }

    @Override
    public Page<CycleTimeTarget> findAllCycleTimeTargetCondition(String productNo, int page, int size) {
        log.info("条件查询所有的cycleTimeTarget");
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(page, size, sort);
        Specification<CycleTimeTarget> specification = new Specification<CycleTimeTarget>() {
            @Override
            public Predicate toPredicate(Root<CycleTimeTarget> root,
                                         CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //条件判断
                if(null!=productNo && !"".equals(productNo)&& !"null".equals(productNo) && !productNo.equals("undefined")) {
                    Predicate _productNo = criteriaBuilder.equal(root.get("productNo"), productNo);
                    predicates.add(_productNo);
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
            }
        };
        return cycleTimeTargetRepository.findAll(specification, pageable);
    }

    @Override
    public long getAllCycleTimeTargetByCondition(String productNo) {
        Specification<CycleTimeTarget> specification = new Specification<CycleTimeTarget>() {
            @Override
            public Predicate toPredicate(Root<CycleTimeTarget> root,
                                         CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //条件判断
                if(null!=productNo && !"".equals(productNo) &&!"null".equals(productNo) && !productNo.equals("undefined") && !productNo.isEmpty()) {
                    Predicate _workstation = criteriaBuilder.equal(root.get("productNo"), productNo);
                    predicates.add(_workstation);
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
            }
        };
        return cycleTimeTargetRepository.count(specification);
    }
    @Override
    public CycleTimeTarget findOneById(Long id) {
        return cycleTimeTargetRepository.findOne(id);
    }

    @Override
    public void delete(Long id) {
        log.info("delete cycleTimeTarget");
        cycleTimeTargetRepository.delete(id);
    }
    /**
     * 通过产品Id query hour number
     * @param productId
     * @return
     */
    @Override
    public CycleTimeTarget getCycleTimeTargetByProductId(String productId) {
        log.info("get cycletTimeTarget by productId");
        CycleTimeTarget cycleTimeTarget=cycleTimeTargetRepository.getCycleTimeTargetByProductNo(productId);
        return cycleTimeTarget;
    }
}
