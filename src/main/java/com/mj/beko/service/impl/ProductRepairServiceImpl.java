package com.mj.beko.service.impl;

import com.mj.beko.domain.Pallet;
import com.mj.beko.domain.ProductRepair;
import com.mj.beko.repository.ProductRepairRepository;
import com.mj.beko.service.ProductRepairService;
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
 * Created by Administrator on 2017/10/23/023.
 */
@Service
@Transactional
@Slf4j
public class ProductRepairServiceImpl  implements ProductRepairService{

    @Autowired
    private ProductRepairRepository productRepairRepository;

    @Override
    public List<ProductRepair> query() {
        log.info("Query for all repair products");
        return productRepairRepository.findAll();
        }

    @Override
    public List<ProductRepair> queryByPage(int page, int size) {
        log.info("Paging query rebuilds the product");
        return productRepairRepository.queryByPage(page*size,size);
    }

    @Override
    public Page<ProductRepair> findAllByPageAndCondition(String productNo, int page, int size) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(page, size, sort);
        Specification<ProductRepair> specification = new Specification<ProductRepair>() {
            @Override
            public Predicate toPredicate(Root<ProductRepair> root,
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
        return productRepairRepository.findAll(specification, pageable);
    }

    @Override
    public long getAllCountByCondition(String productNo) {
        Specification<ProductRepair> specification = new Specification<ProductRepair>() {
            @Override
            public Predicate toPredicate(Root<ProductRepair> root,
                                         CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //条件判断
                if(null!=productNo && !"".equals(productNo) &&!"null".equals(productNo) && !productNo.equals("undefined") && !productNo.isEmpty()) {
                    Predicate _productNo = criteriaBuilder.equal(root.get("productNo"), productNo);
                    predicates.add(_productNo);
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
            }
        };
        return productRepairRepository.count(specification);
    }

    @Override
    public ProductRepair save(ProductRepair productRepair) {
        log.info("save productRepair");
        return productRepairRepository.save(productRepair);
    }

    /**
     * 根据下底盘条码和State查询返修记录数
     * @param bottomPlateBarcode
     * @param state
     * @return
     */
    @Override
    public int getCountByBarvodeAndState(String bottomPlateBarcode, String state) {
        return productRepairRepository.getCountByBarvodeAndState(bottomPlateBarcode, state);
    }

    @Override
    public ProductRepair getProductRepairByBottomBarCode(String bottomPlateBarCode,String state) {
        log.info("bottomPlate get productRepair");
        return productRepairRepository.getProductRepairByBottomBarCode(bottomPlateBarCode, state);
    }

    @Override
    public String getAllCountProductRepair() {
        log.info("To obtain the total number of records of the repaired products");
        return String.valueOf(productRepairRepository.count());
    }
}


