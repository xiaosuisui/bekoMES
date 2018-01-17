package com.mj.beko.service.impl;

import com.mj.beko.domain.Pallet;
import com.mj.beko.domain.TestStationData;
import com.mj.beko.repository.TestStationDataRespository;
import com.mj.beko.service.PalletService;
import com.mj.beko.service.TestStationDataService;
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
@Transactional
@Slf4j
public class TestStationServiceImpl implements TestStationDataService {

    @Autowired
    private TestStationDataRespository testStationDataRespository;
    @Autowired
    private PalletService palletService;

    @Override
    public TestStationData save(TestStationData testStationData) {
        log.info("保存测试工位数据");
        return testStationDataRespository.save(testStationData);
    }

    @Override
    public TestStationData findOne(Long id) {
        log.info("通过Id查找测试工位");
        return testStationDataRespository.findOne(id);
    }

    @Override
    public Page<TestStationData> findAllTestDataByPageAndCondition(String productNo, String barCode, int page, int size) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(page, size, sort);
        Specification<TestStationData> specification = new Specification<TestStationData>() {
            @Override
            public Predicate toPredicate(Root<TestStationData> root,
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
        return testStationDataRespository.findAll(specification, pageable);
    }

    @Override
    public long getAllCountByCondition(String productNo, String barCode) {
        Specification<TestStationData> specification = new Specification<TestStationData>() {
            @Override
            public Predicate toPredicate(Root<TestStationData> root,
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
        return testStationDataRespository.count(specification);
    }

    /**
     * 根据下底盘条码获取最近一次的气密检测结果为"NOK"的数量
     * @param bottomPlaceCode
     * @return
     */
    @Override
    public int getAirtightNokCountByBottomPlaceCode(String bottomPlaceCode) {
        return testStationDataRespository.getAirtightNokCountByBottomPlaceCode(bottomPlaceCode);
    }

    /**
     * 根据下底盘条码获取最近一次的流量检测结果为"NOK"的数量
     * @param bottomPlaceCode
     * @return
     */
    @Override
    public int getFluxNokCountByBottomPlaceCode(String bottomPlaceCode) {
        return testStationDataRespository.getFluxNokCountByBottomPlaceCode(bottomPlaceCode);
    }

    /**
     * 根据条码查询当前的testStation 中状态为NOK的记录
     * @param barCode
     * @return
     */
    @Override
    public List<TestStationData> getTestNOKDataByBarcode(String barCode) {
        Specification<TestStationData> specification = new Specification<TestStationData>() {
            @Override
            public Predicate toPredicate(Root<TestStationData> root,
                                         CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //条件判断
                if(null!=barCode && !"".equals(barCode)&& !"null".equals(barCode) && !barCode.equals("undefined")) {
                    Predicate _barCode = criteriaBuilder.equal(root.get("barCode"), barCode);
                    predicates.add(_barCode);
                }
                    Predicate _result = criteriaBuilder.equal(root.get("result"), "NOK");
                    predicates.add(_result);
                return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
            }
        };
        return testStationDataRespository.findAll(specification);
    }

    /**
     * 查询测试工位中的结果为OK的标识
     * @param barCode
     * @param type
     * @return
     */
    @Override
    public List<TestStationData> getDiffTestStationResultMark(String barCode, String type) {
        log.info("get diff testStation data result from testStationDate table");
        return testStationDataRespository.getDiffTestStationResultMark(barCode,type);
    }

    /**
     * 根据下底盘条码获取打螺丝工位结果为"NOK"的数量
     * @param bottomPlaceCode
     * @return
     */
    @Override
    public int getScrewsNokCountByBottomPlaceCode(String bottomPlaceCode) {
        return testStationDataRespository.getScrewsNokCountByBottomPlaceCode(bottomPlaceCode);
    }

    /**
     * 通过托盘号查询电测试结果OK的标识
     * @param palletNo
     * @return
     */
    @Override
    public List<TestStationData> getElectricResultMarkByPalletNo(String palletNo) {
        //通过托盘号查询
        Pallet pallet=palletService.findPalletByPalletNo(palletNo);
        String bottomPlateBarCode=pallet.getBottomPlaceCode();
        Specification<TestStationData> specification = new Specification<TestStationData>() {
            @Override
            public Predicate toPredicate(Root<TestStationData> root,
                                         CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //条件判断

                Predicate _productNo = criteriaBuilder.equal(root.get("barCode"), bottomPlateBarCode);
                predicates.add(_productNo);
                Predicate _barCode = criteriaBuilder.equal(root.get("step"), "electric");
                predicates.add(_barCode);
                Predicate value = criteriaBuilder.equal(root.get("value"), "OK");
                predicates.add(value);
                return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
            }
        };
        
        return testStationDataRespository.findAll(specification);
    }
}
