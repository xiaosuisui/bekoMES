package com.mj.beko.service.impl;

import com.mj.beko.domain.DownTimeData;
import com.mj.beko.domain.OrderUpdateLog;
import com.mj.beko.repository.DownTimeRepository;
import com.mj.beko.service.DownTimeDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
 * Created by Ricardo on 2017/11/13.
 */
@Service
@Slf4j
@Transactional
public class DownTimeDataServiceImpl implements DownTimeDataService {
    @Autowired
    private DownTimeRepository downTimeRepository;
    @Override
    public DownTimeData save(DownTimeData downTimeData) {
        log.info("save downTime");
        return downTimeRepository.save(downTimeData);
    }

    @Override
    public void delete(DownTimeData downTimeData) {

    }

    @Override
    public List<DownTimeData> query() {
        return null;
    }

    @Override
    public List<DownTimeData> queryByPage(int page, int size) {
        return null;
    }

    @Override
    public Page<DownTimeData> findAllByDownTimeCondition(String workstation, int page, int size) {
        log.info("条件查询所有的downTime");
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(page, size, sort);
        Specification<DownTimeData> specification = new Specification<DownTimeData>() {
            @Override
            public Predicate toPredicate(Root<DownTimeData> root,
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
        return downTimeRepository.findAll(specification, pageable);
    }

    @Override
    public long getAllDownTimeCountByCondition(String workstation) {
        Specification<DownTimeData> specification = new Specification<DownTimeData>() {
            @Override
            public Predicate toPredicate(Root<DownTimeData> root,
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
        return downTimeRepository.count(specification);
    }

    @Override
    public DownTimeData findOneById(Long id) {
        return downTimeRepository.findOne(id);
    }

    @Override
    public List<DownTimeData> getDownTimeTopFour() {
        log.info("查询最新的四条记录");
        return downTimeRepository.findDownDataTopFour();
    }
}
