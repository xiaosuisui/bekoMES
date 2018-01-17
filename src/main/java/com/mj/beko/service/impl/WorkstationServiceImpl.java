package com.mj.beko.service.impl;

import com.mj.beko.domain.Workstation;
import com.mj.beko.repository.WorkstationRepository;
import com.mj.beko.service.WorkstationService;
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
import java.util.Optional;

/**
 * Created by jc on 2017/8/23.
 */
@Service
@Transactional
@Slf4j
public class WorkstationServiceImpl implements WorkstationService {

    @Autowired
    private WorkstationRepository workstationRepository;

    @Override
    public Workstation save(Workstation workstation) {
        log.info("save workstaion");
        return workstationRepository.save(workstation);
    }

    @Override
    public void delete(Workstation workstation) {

    }

    @Override
    public List<Workstation> query() {
        return workstationRepository.findAll();
    }

    @Override
    public List<Workstation> queryByPage(int page, int size) {
        log.info("分页查询workstation{}{}", page, size);
        return workstationRepository.queryByPage(page * size, size);
    }

    @Override
    public String getAllCountWorkstaion() {
        log.info("查询workstation 所有记录");
        return String.valueOf(workstationRepository.count());
    }

    @Override
    public Workstation getWorkstationByStationId(String stationId) {
        log.debug("根据stationId查询工位信息 : {}", stationId);
        return workstationRepository.getWorkstationByStationId(stationId);
    }

    @Override
    public Workstation getWorkstationByStationName(String stationName) {
        log.debug("根据stationName查询工位信息 : {}", stationName);
        return workstationRepository.getWorkstationByStationName(stationName);
    }

    @Override
    public Workstation findOne(Long id) {
        log.debug("REST request to get Workstation : {}", id);
        return workstationRepository.findOne(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("REST request to delete Workstation : {}", id);
        workstationRepository.delete(id);
    }

    @Override
    public Optional<Workstation> updateWorkstation(Workstation workstation) {
        return Optional.of(workstationRepository.findOne(workstation.getId())).map(oldWorkStation -> {
            oldWorkStation.setStationId(workstation.getStationId());
            oldWorkStation.setStationName(workstation.getStationName());
            oldWorkStation.setStationDesc(workstation.getStationDesc());
            return oldWorkStation;
        });
    }

    @Override
    public List<Workstation> findAll() {
        return workstationRepository.findAll(new Sort("id"));
    }

    @Override
    public Page<Workstation> findAllByPageAndCondition(String stationId, String stationName, int page, int size) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(page, size, sort);
        Specification<Workstation> specification = new Specification<Workstation>() {
            @Override
            public Predicate toPredicate(Root<Workstation> root,
                                         CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //条件判断
                if(null != stationId && !"".equals(stationId) && !"null".equals(stationId) && !stationId.equals("undefined")) {
                    Predicate _stationId = criteriaBuilder.equal(root.get("stationId"), stationId);
                    predicates.add(_stationId);
                }
                if(null != stationName && !"".equals(stationName) && !"null".equals(stationName) && !stationName.equals("undefined")){
                    Predicate _stationName = criteriaBuilder.equal(root.get("stationName"), stationName);
                    predicates.add(_stationName);
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
            }
        };
        return workstationRepository.findAll(specification, pageable);
    }

    @Override
    public long getAllCountByCondition(String stationId, String stationName) {
        Specification<Workstation> specification = new Specification<Workstation>() {
            @Override
            public Predicate toPredicate(Root<Workstation> root,
                                         CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //条件判断
                if(null != stationId && !"".equals(stationId) && !"null".equals(stationId) && !stationId.equals("undefined") && !stationId.isEmpty()) {
                    Predicate _stationId = criteriaBuilder.equal(root.get("stationId"), stationId);
                    predicates.add(_stationId);
                }
                if(null != stationName && !"".equals(stationName) && !"null".equals(stationName) && !stationName.equals("undefined") && !stationName.isEmpty()){
                    Predicate _stationName = criteriaBuilder.equal(root.get("stationName"), stationName);
                    predicates.add(_stationName);
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
            }
        };
        return workstationRepository.count(specification);
    }
}
