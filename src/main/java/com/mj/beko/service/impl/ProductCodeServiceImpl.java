package com.mj.beko.service.impl;

import com.mj.beko.domain.Product;
import com.mj.beko.domain.ProductCode;
import com.mj.beko.domain.TcsOrder;
import com.mj.beko.repository.ProductCodeRepository;
import com.mj.beko.service.ProductCodeService;
import com.mj.beko.service.ProductService;
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
public class ProductCodeServiceImpl implements ProductCodeService {
    @Autowired
    private ProductCodeRepository productCodeRepository;

    @Override
    public ProductCode save(ProductCode productCode) {
        log.info("save a productCode{}",productCode);
        return productCodeRepository.save(productCode);
    }
    @Override
    public void delete(ProductCode productCode) {

    }

    @Override
    public List<ProductCode> query() {
        log.info("查询所有的productCode");
        return productCodeRepository.findAll();
    }

    @Override
    public List<ProductCode> queryByPage(int page, int size) {
        log.info("分页查询productCode{}{}", page, size);
        return productCodeRepository.queryByPage(page * size, size);
    }

    @Override
    public String getAllCountProductCode() {
        log.info("获取总记录数 productCode");
        return String.valueOf(productCodeRepository.count());
    }

    @Override
    public Page<ProductCode> findAllByPageAndCondition(String productNo, String orderNo, int page, int size) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(page, size, sort);
        Specification<ProductCode> specification = new Specification<ProductCode>() {
            @Override
            public Predicate toPredicate(Root<ProductCode> root,
                                         CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //条件判断
                if(null!=productNo && !"".equals(productNo)&& !"null".equals(productNo) && !productNo.equals("undefined")) {
                    Predicate _productNo = criteriaBuilder.equal(root.get("productNo"), productNo);
                    predicates.add(_productNo);
                }
                if(null!=orderNo && !"".equals(orderNo)&& !"null".equals(orderNo) &&!orderNo.equals("undefined")){
                    Predicate _orderNo = criteriaBuilder.equal(root.get("orderNo"), orderNo);
                    predicates.add(_orderNo);
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
            }
        };
        return productCodeRepository.findAll(specification, pageable);
    }
    @Override
    public long getAllCountByCondition(String productNo, String orderNo) {
        Specification<ProductCode> specification = new Specification<ProductCode>() {
            @Override
            public Predicate toPredicate(Root<ProductCode> root,
                                         CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //条件判断
                if(null!=productNo && !"".equals(productNo) &&!"null".equals(productNo) && !productNo.equals("undefined") && !productNo.isEmpty()) {
                    Predicate _productnNo =criteriaBuilder.equal(root.get("productNo"), productNo);
                    predicates.add(_productnNo);
                }
                if(null!=orderNo && !"".equals(orderNo) && !"null".equals(orderNo) && !orderNo.equals("undefined") && !orderNo.isEmpty()){
                    Predicate _orderNo = criteriaBuilder.equal(root.get("orderNo"), orderNo);
                    predicates.add(_orderNo);
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
            }
        };
        return productCodeRepository.count(specification);
    }

    /**
     * 通过epsCode查找productCode
     * @param epsCode
     * @return
     */
    @Override
    public ProductCode getProductCodeByEpsCode(String epsCode) {
        ProductCode productCode =productCodeRepository.getProductCodeByEpsCode(epsCode);
        return productCode;
    }

    /**
     * 根据状态查询最老的一条记录
     * @param status
     * @return
     */
    @Override
    public ProductCode getOldProductCodeByStatus(String status) {
        return productCodeRepository.getOldProductCodeByStatus(status);
    }

    @Override
    public ProductCode getOldProductCodeOnlyByStatus(String status) {
        return productCodeRepository.getOldProductCodeOnlyByStatus(status);
    }

    @Override
    public ProductCode getProductCodeByBottomPlateBarCode(String bottomPlateBarCode) {
        log.info("通过下底盘条码查询 bottomPlateCode is {}",bottomPlateBarCode);
        return productCodeRepository.getProductCodeByBottomPlateBarCode(bottomPlateBarCode);
    }

    /**
     * 通过eps条码查询对应的productCode 状态为1 或者2的
     * @param epsCode
     * @return
     */
    @Override
    public ProductCode getProductCodeByEpsCodeAndStatus(String epsCode) {
        log.info("get Product Code by epsCode, is,{}",epsCode);
        return productCodeRepository.getProductCodeByEpsCodeAndStatus(epsCode);
    }

    @Override
    public ProductCode getProductCodeByBottomPlateCode(String bottomPlateBarCode) {
        log.info("get productCode by bottomPlateCode,value is,{}",bottomPlateBarCode);
        return productCodeRepository.getProductCodeByBottomPlateCode(bottomPlateBarCode);
    }
}
