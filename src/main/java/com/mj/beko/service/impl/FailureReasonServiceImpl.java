package com.mj.beko.service.impl;

import com.mj.beko.domain.DownTimeData;
import com.mj.beko.domain.FailureReason;
import com.mj.beko.repository.FailureReasonRepository;
import com.mj.beko.service.FailureReasonService;
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
 * Created by Ricardo on 2017/11/14.
 */
@Service
@Slf4j
@Transactional
public class FailureReasonServiceImpl implements FailureReasonService {
    @Autowired
    private FailureReasonRepository failureReasonRepository;
    @Override
    public FailureReason save(FailureReason failureReason) {
        log.info("save failure reason");
        return failureReasonRepository.save(failureReason);
    }
    @Override
    public void delete(FailureReason failureReason) {
        log.info("delete failure reason");
        failureReasonRepository.delete(failureReason);
    }

    @Override
    public List<FailureReason> query() {
        return null;
    }

    @Override
    public List<FailureReason> queryByPage(int page, int size) {
        return null;
    }

    @Override
    public Page<FailureReason> findAllFailureReasonCondition(String workstation, int page, int size) {
        log.info("条件查询所有的failure reason");
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(page, size, sort);
        Specification<FailureReason> specification = new Specification<FailureReason>() {
            @Override
            public Predicate toPredicate(Root<FailureReason> root,
                                         CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //条件判断
                if(null!=workstation && !"".equals(workstation)&& !"null".equals(workstation) && !workstation.equals("undefined")) {
                    Predicate _workstation = criteriaBuilder.equal(root.get("workstation"), workstation);
                    predicates.add(_workstation);
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
            }
        };
        return failureReasonRepository.findAll(specification, pageable);
    }

    @Override
    public long getAllFailureReasonByCondition(String workstation) {
        Specification<FailureReason> specification = new Specification<FailureReason>() {
            @Override
            public Predicate toPredicate(Root<FailureReason> root,
                                         CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //条件判断
                if(null!=workstation && !"".equals(workstation) &&!"null".equals(workstation) && !workstation.equals("undefined") && !workstation.isEmpty()) {
                    Predicate _workstation = criteriaBuilder.equal(root.get("workstation"), workstation);
                    predicates.add(_workstation);
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
            }
        };
        return failureReasonRepository.count(specification);
    }

    @Override
    public FailureReason findOneById(Long id) {
        return failureReasonRepository.findOne(id);
    }

    @Override
    public void delete(Long id) {
        failureReasonRepository.delete(id);
    }

}
