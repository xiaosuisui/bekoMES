package com.mj.beko.service.impl;

import com.mj.beko.domain.OrderUpdateLog;
import com.mj.beko.domain.Pallet;
import com.mj.beko.domain.TcsOrder;
import com.mj.beko.repository.OrderUpdateLogRepository;
import com.mj.beko.service.OrderUpdateLogService;
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
 * Created by Ricardo on 2017/11/9.
 */
@Service
@Slf4j
@Transactional
public class OrderUpdateLogServiceImpl implements OrderUpdateLogService {
    @Autowired
    private OrderUpdateLogRepository orderUpdateLogRepository;
    @Override
    public Page<OrderUpdateLog> findAllByUpdateLogCondition(String username, String operatorType, int page, int size) {
        log.info("条件查询所有的日志操作");
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(page, size, sort);
        Specification<OrderUpdateLog> specification = new Specification<OrderUpdateLog>() {
            @Override
            public Predicate toPredicate(Root<OrderUpdateLog> root,
                                         CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //条件判断
                if(null!=username && !"".equals(username)&& !"null".equals(username) && !username.equals("undefined")) {
                    Predicate _userName = criteriaBuilder.equal(root.get("username"), username);
                    predicates.add(_userName);
                }
                if(null!=operatorType && !"".equals(operatorType)&& !"null".equals(operatorType) &&!operatorType.equals("undefined")){
                    Predicate _operatorType = criteriaBuilder.equal(root.get("operatorType"), operatorType);
                    predicates.add(_operatorType);
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
            }
        };
        return orderUpdateLogRepository.findAll(specification, pageable);
    }

    @Override
    public long getAllCountByCondition(String username, String operatorType) {
        Specification<OrderUpdateLog> specification = new Specification<OrderUpdateLog>() {
            @Override
            public Predicate toPredicate(Root<OrderUpdateLog> root,
                                         CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //条件判断
                if(null!=username && !"".equals(username) &&!"null".equals(username) && !username.equals("undefined") && !username.isEmpty()) {
                    Predicate _userName = criteriaBuilder.equal(root.get("username"), username);
                    predicates.add(_userName);
                }
                if(null!=operatorType && !"".equals(operatorType) && !"null".equals(operatorType) && !operatorType.equals("undefined") && !operatorType.isEmpty()){
                    Predicate _operatorType = criteriaBuilder.equal(root.get("operatorType"), operatorType);
                    predicates.add(_operatorType);
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
            }
        };
        return orderUpdateLogRepository.count(specification);
    }

    @Override
    public OrderUpdateLog getOrderUpdateLog(Long id) {
        log.info("通过Id查询订单模块修改");
        return orderUpdateLogRepository.findOne(id);
    }

    @Override
    public OrderUpdateLog save(OrderUpdateLog orderUpdateLog) {
        log.info("save orderUpdateLog，{}",orderUpdateLog);
        return orderUpdateLogRepository.save(orderUpdateLog);
    }
    @Override
    public void delete(OrderUpdateLog orderUpdateLog) {

    }
    @Override
    public List<OrderUpdateLog> query() {
        return null;
    }
    @Override
    public List<OrderUpdateLog> queryByPage(int page, int size) {
        return null;
    }
}
