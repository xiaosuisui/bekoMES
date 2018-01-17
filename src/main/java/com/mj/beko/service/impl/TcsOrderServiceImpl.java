package com.mj.beko.service.impl;

import com.mj.beko.domain.Pallet;
import com.mj.beko.domain.TcsOrder;
import com.mj.beko.domain.Workstation;
import com.mj.beko.repository.TcsOrderRepository;
import com.mj.beko.service.TcsOrderService;
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
 * Created by Ricardo on 2017/8/24.
 */
@Service
@Transactional
@Slf4j
public class TcsOrderServiceImpl implements TcsOrderService {
    @Autowired
    private TcsOrderRepository tcsOrderRepository;
    /**
     * Save a tcsOrder.
     *
     * @param tcsOrder the entity to save
     * @return the persisted entity
     */
    @Override
    public TcsOrder save(TcsOrder tcsOrder) {
        log.debug("Request to save TcsOrder : {}", tcsOrder);
        return tcsOrderRepository.save(tcsOrder);
    }

    /**
     *  Get all the tcsOrders.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TcsOrder> findAll(Pageable pageable) {
        log.debug("Request to get all TcsOrders");
        return tcsOrderRepository.findAll(pageable);
    }

    /**
     *  Get one tcsOrder by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public TcsOrder findOne(Long id) {
        log.debug("Request to get TcsOrder : {}", id);
        return tcsOrderRepository.findOne(id);
    }

    /**
     *  Delete the  tcsOrder by id.
     *
     *  @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete TcsOrder : {}", id);
        tcsOrderRepository.delete(id);
    }

    @Override
    public List<TcsOrder> findAllTcsOrderByPage(int page, int size) {
        return tcsOrderRepository.findAllTcsOrderByPage(page * size, size);
    }
    //通过调拨单的名字查找对应的实体
    @Override
    public List<TcsOrder> findAllByTcsOrderName(String name){
        return tcsOrderRepository.findAllByTcsOrderName(name);
    }

    //更新调拨单数据
    @Override
    public TcsOrder saveAndFlush(TcsOrder tcsOrder) {

        return tcsOrderRepository.saveAndFlush(tcsOrder);
    }

    @Override
    public String getAllTcsOrder(){
        return String.valueOf(tcsOrderRepository.count());
    }

    @Override
    public Page<TcsOrder> findAllByPageAndCondition(String tcsOrderName, String stationNo, int page, int size) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(page, size, sort);
        Specification<TcsOrder> specification = new Specification<TcsOrder>() {
            @Override
            public Predicate toPredicate(Root<TcsOrder> root,
                                         CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //条件判断
                if(null!=tcsOrderName && !"".equals(tcsOrderName)&& !"null".equals(tcsOrderName) && !tcsOrderName.equals("undefined")) {
                    Predicate _tcsOrder = criteriaBuilder.equal(root.get("tcsOrderName"), tcsOrderName);
                    predicates.add(_tcsOrder);
                }
                if(null!=stationNo && !"".equals(stationNo)&& !"null".equals(stationNo) &&!stationNo.equals("undefined")){
                    Predicate _stationNo = criteriaBuilder.equal(root.get("stationNo"), stationNo);
                    predicates.add(_stationNo);
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
            }
        };
        return tcsOrderRepository.findAll(specification, pageable);
    }

    @Override
    public long getAllCountByCondition(String tcsOrderName, String stationNo) {
        Specification<TcsOrder> specification = new Specification<TcsOrder>() {
            @Override
            public Predicate toPredicate(Root<TcsOrder> root,
                                         CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //条件判断
                if(null!=tcsOrderName && !"".equals(tcsOrderName) &&!"null".equals(tcsOrderName) && !tcsOrderName.equals("undefined") && !tcsOrderName.isEmpty()) {
                    Predicate _tcsOrderName = criteriaBuilder.equal(root.get("tcsOrderName"), tcsOrderName);
                    predicates.add(_tcsOrderName);
                }
                if(null!=stationNo && !"".equals(stationNo) && !"null".equals(stationNo) && !stationNo.equals("undefined") && !stationNo.isEmpty()){
                    Predicate _stationNo = criteriaBuilder.equal(root.get("stationNo"), stationNo);
                    predicates.add(_stationNo);
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
            }
        };
        return tcsOrderRepository.count(specification);
    }

    /**
     * 获取最新的一个EPS类型的小车调度单
     * @return
     */
    @Override
    public TcsOrder getLastEPpsTypeTcsOrder(String tcsOrderName) {
        log.info("获取最新的一个EPS type调度单");
        return tcsOrderRepository.getLastEpsTypeTcsOrder(tcsOrderName);
    }

    @Override
    public List<TcsOrder> getLastEpsDownTypeTcsOrder() {
        log.info("获取最新的双工位类型的tcsOrder");
        return tcsOrderRepository.getLastEpsDownTypeTcsOrder();
    }

    @Override
    public List<TcsOrder> getLatestTcsOrderForBottomAndTopPlate() {
        log.info("bottomPlate state 3 search......");
        return tcsOrderRepository.getLatestTcsOrderForTemplate();
    }
}
