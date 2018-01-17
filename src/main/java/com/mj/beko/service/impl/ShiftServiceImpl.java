package com.mj.beko.service.impl;

import com.mj.beko.domain.OperatorShift;
import com.mj.beko.domain.OperatorShiftDetail;
import com.mj.beko.domain.TcsOrder;
import com.mj.beko.repository.ShiftDetailRepository;
import com.mj.beko.repository.ShiftRepository;
import com.mj.beko.service.ShiftService;
import lombok.Data;
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
 * Created by Ricardo on 2017/12/1.
 */
@Slf4j
@Service
@Transactional
public class ShiftServiceImpl implements ShiftService {
    @Autowired
    private ShiftRepository shiftRepository;
    @Autowired
    private ShiftDetailRepository shiftDetailRepository;
    @Override
    public OperatorShift save(OperatorShift operatorShift) {
        return shiftRepository.save(operatorShift) ;
    }
    @Override
    public void delete(OperatorShift operatorShift) {
        shiftRepository.delete(operatorShift);
    }

    @Override
    public List<OperatorShift> query() {
        return null;
    }
    @Override
    public List<OperatorShift> queryByPage(int page, int size) {
        return null;
    }

    @Override
    public OperatorShift getOperatorShift(Long id) {
        return shiftRepository.findOne(id);
    }

    @Override
    public void delete(Long id) {
        shiftRepository.delete(id);
    }

    @Override
    public long getCountShiftByCondition(String name) {
        Specification<OperatorShift> specification = new Specification<OperatorShift>() {
            @Override
            public Predicate toPredicate(Root<OperatorShift> root,
                                         CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //条件判断
                if(null!=name && !"".equals(name) &&!"null".equals(name) && !name.equals("undefined") && !name.isEmpty()) {
                    Predicate _name = criteriaBuilder.equal(root.get("name"), name);
                    predicates.add(_name);
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
            }
        };
        return shiftRepository.count(specification);
    }

    @Override
    public Page<OperatorShift> findAllShiftsByPageAndCondition(String name, int page, int size) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(page, size, sort);
        Specification<OperatorShift> specification = new Specification<OperatorShift>() {
            @Override
            public Predicate toPredicate(Root<OperatorShift> root,
                                         CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //条件判断
                if(null!=name && !"".equals(name)&& !"null".equals(name) && !name.equals("undefined")) {
                    Predicate _name = criteriaBuilder.equal(root.get("name"), name);
                    predicates.add(_name);
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
            }
        };
        return shiftRepository.findAll(specification, pageable);
    }
    /**
     * 获取当前系统中的当前的shift
     * @return
     */
    @Override
    public OperatorShift getCurrentShift() {
        log.info("getCurrentShift。。。。active");
        return shiftRepository.getCurrentShift();
    }

    /**
     *通过shiftId 和 shiftName 获取对应的shiftDetailList
     * @param shiftId
     * @param ShiftName
     * @return
     */
    @Override
    public List<OperatorShiftDetail> getOperatorShiftByShiftName(Long shiftId, String ShiftName) {
        log.info("get current shift standOutput time,parameter is shiftId and shiftName");
        return shiftDetailRepository.getOperatorShiftByShiftName(shiftId,ShiftName);
    }
}
