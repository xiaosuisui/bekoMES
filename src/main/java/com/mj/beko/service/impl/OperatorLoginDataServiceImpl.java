package com.mj.beko.service.impl;

import com.mj.beko.domain.OperatorLoginData;
import com.mj.beko.domain.Pallet;
import com.mj.beko.repository.OperatorLoginDataRepository;
import com.mj.beko.service.OperatorLoginDataService;
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
 * Created by Ricardo on 2017/11/6.
 */
@Service
@Slf4j
@Transactional
public class OperatorLoginDataServiceImpl implements OperatorLoginDataService {
    @Autowired
    private OperatorLoginDataRepository operatorLoginDataRepository;

    /**
     * 保存
     * @param operatorLoginData
     * @return
     */
    @Override
    public OperatorLoginData save(OperatorLoginData operatorLoginData) {
        log.info("save{}",operatorLoginData.getOperator());
        return operatorLoginDataRepository.save(operatorLoginData);
    }

    @Override
    public void delete(OperatorLoginData operatorLoginData) {
        log.info("delete{}",operatorLoginData.getOperator());
        operatorLoginDataRepository.delete(operatorLoginData.getId());
    }

    @Override
    public List<OperatorLoginData> query() {
        return null;
    }
    @Override
    public List<OperatorLoginData> queryByPage(int page, int size) {
        return null;
    }

    @Override
    public OperatorLoginData findOneById(Long id) {
        log.info("query operatorLoginData by{}",id);
        return operatorLoginDataRepository.findOneById(id);
    }

    @Override
    public void delete(Long id) {
        log.info("delete operatorLogin data{}",id);
        operatorLoginDataRepository.delete(id);
    }
    /**
     * 条件查询总记录数
     * @param operator
     * @param workstation
     * @return
     */
    @Override
    public long getAllCountByCondition(String operator, String workstation) {
        Specification<OperatorLoginData> specification = new Specification<OperatorLoginData>() {
            @Override
            public Predicate toPredicate(Root<OperatorLoginData> root,
                                         CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //条件判断
                if(null!=operator && !"".equals(operator) &&!"null".equals(operator) && !operator.equals("undefined") && !operator.isEmpty()) {
                    Predicate _operator= criteriaBuilder.equal(root.get("operator"), operator);
                    predicates.add(_operator);
                }
                if(null!=workstation && !"".equals(workstation) && !"null".equals(workstation) && !workstation.equals("undefined") && !workstation.isEmpty()){
                    Predicate _workstation = criteriaBuilder.equal(root.get("workstation"), workstation);
                    predicates.add(_workstation);
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
            }
        };
        return operatorLoginDataRepository.count(specification);
    }

    @Override
    public String findOneByWorkStation(String workstation) {
        log.info("通过工位查询当前登录用户名");
        OperatorLoginData operatorLoginData =operatorLoginDataRepository.findOnByWorkstation(workstation);
        return operatorLoginData.getOperator();
    }

    @Override
    public Page<OperatorLoginData> findAllByPageAndCondition(final String operator, final String workstation, int page, int size) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(page, size, sort);
        Specification<OperatorLoginData> specification = new Specification<OperatorLoginData>() {
            @Override
            public Predicate toPredicate(Root<OperatorLoginData> root,
                                         CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //条件判断
                if(null!=operator && !"".equals(operator)&& !"null".equals(operator) && !operator.equals("undefined")) {
                    Predicate _operator = criteriaBuilder.equal(root.get("operator"), operator);
                    predicates.add(_operator);
                }
                if(null!=workstation && !"".equals(workstation)&& !"null".equals(workstation) &&!workstation.equals("undefined")){
                    Predicate _workstation = criteriaBuilder.equal(root.get("workstation"), workstation);
                    predicates.add(_workstation);
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
            }
        };
        return operatorLoginDataRepository.findAll(specification, pageable);
    }
}
