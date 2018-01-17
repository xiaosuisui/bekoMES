package com.mj.beko.service.impl;

import com.mj.beko.domain.ProductionData;
import com.mj.beko.domain.TestStationData;
import com.mj.beko.repository.ProductionDataRepository;
import com.mj.beko.service.ProductCodeService;
import com.mj.beko.service.ProductionDataService;
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
 * Created by Ricardo on 2017/11/11.
 */
@Service
@Slf4j
@Transactional
public class ProductionDataServiceImpl implements ProductionDataService {
    @Autowired
    private ProductionDataRepository productionDataRepository;

    /**
     * 保存
     * @param productionData
     * @return
     */
    @Override
    public ProductionData save(ProductionData productionData) {
        log.info("保存生产数据");
        return productionDataRepository.save(productionData);
    }

    /**
     * 通过Id查询
     * @param id
     * @return
     */
    @Override
    public ProductionData findOne(Long id) {
        log.info("通过Id查询生产数据");
        return productionDataRepository.findOne(id);
    }

    @Override
    public Page<ProductionData> findAllProductionDataByPageAndCondition(String productNo, String barCode, int page, int size) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(page, size, sort);
        Specification<ProductionData> specification = new Specification<ProductionData>() {
            @Override
            public Predicate toPredicate(Root<ProductionData> root,
                                         CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //条件判断
                if(null!=productNo && !"".equals(productNo)&& !"null".equals(productNo) && !productNo.equals("undefined")) {
                    Predicate _productNo = criteriaBuilder.equal(root.get("productNo"), productNo);
                    predicates.add(_productNo);
                }
                if(null!=barCode && !"".equals(barCode)&& !"null".equals(barCode) &&!barCode.equals("undefined")){
                    Predicate _barCode = criteriaBuilder.equal(root.get("barCode"), barCode);
                    predicates.add(_barCode);
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
            }
        };
        return productionDataRepository.findAll(specification, pageable);
    }

    @Override
    public long getAllCountByCondition(String productNo, String barCode) {
        Specification<ProductionData> specification = new Specification<ProductionData>() {
            @Override
            public Predicate toPredicate(Root<ProductionData> root,
                                         CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //条件判断
                if(null!=productNo && !"".equals(productNo) &&!"null".equals(productNo) && !productNo.equals("undefined") && !productNo.isEmpty()) {
                    Predicate _productNo = criteriaBuilder.equal(root.get("productNo"), productNo);
                    predicates.add(_productNo);
                }
                if(null!=barCode && !"".equals(barCode) && !"null".equals(barCode) && !barCode.equals("undefined") && !barCode.isEmpty()){
                    Predicate _barCode = criteriaBuilder.equal(root.get("barCode"), barCode);
                    predicates.add(_barCode);
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
            }
        };
        return productionDataRepository.count(specification);
    }
}
