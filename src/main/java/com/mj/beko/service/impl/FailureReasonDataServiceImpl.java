package com.mj.beko.service.impl;

import com.mj.beko.domain.DownTimeData;
import com.mj.beko.domain.FailureReasonData;
import com.mj.beko.domain.Pallet;
import com.mj.beko.domain.TestStationData;
import com.mj.beko.repository.FailureReasonDataRepository;
import com.mj.beko.repository.FailureReasonRepository;
import com.mj.beko.service.FailureReasonDataService;
import com.mj.beko.service.PalletService;
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
 * Created by Ricardo on 2017/11/16.
 */
@Transactional
@Service
@Slf4j
public class FailureReasonDataServiceImpl implements FailureReasonDataService {
    @Autowired
    private FailureReasonDataRepository failureReasonDataRepository;
    @Autowired
    PalletService palletService;
    @Override
    public FailureReasonData save(FailureReasonData failureReasonData) {
        log.info("save failureReason Data");
        return failureReasonDataRepository.save(failureReasonData);
    }

    @Override
    public void delete(FailureReasonData failureReasonData) {
        log.info("delete failureReason data");
        failureReasonDataRepository.delete(failureReasonData);

    }

    @Override
    public List<FailureReasonData> query() {
        return null;
    }

    @Override
    public List<FailureReasonData> queryByPage(int page, int size) {
        return null;
    }

    @Override
    public Page<FailureReasonData> findFailureReasonDataByCondition(String workstation, String barCode, int page, int size) {
        log.info("条件查询所有的failreasonData");
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(page, size, sort);
        Specification<FailureReasonData> specification = new Specification<FailureReasonData>() {
            @Override
            public Predicate toPredicate(Root<FailureReasonData> root,
                                         CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //条件判断
                if(null!=workstation && !"".equals(workstation)&& !"null".equals(workstation) && !workstation.equals("undefined")) {
                    Predicate _workstation = criteriaBuilder.equal(root.get("workstation"), workstation);
                    predicates.add(_workstation);
                }
                //条件判断
                if(null!=barCode && !"".equals(barCode)&& !"null".equals(barCode) && !barCode.equals("undefined")) {
                    Predicate _barCode = criteriaBuilder.equal(root.get("barCode"), barCode);
                    predicates.add(_barCode =_barCode);
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
            }
        };
        return failureReasonDataRepository.findAll(specification, pageable);
    }

    @Override
    public long getAllFailureReasonDataByCondition(String workstation, String barCode) {
        Specification<FailureReasonData> specification = new Specification<FailureReasonData>() {
            @Override
            public Predicate toPredicate(Root<FailureReasonData> root,
                                         CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //条件判断
                if(null!=workstation && !"".equals(workstation) &&!"null".equals(workstation) && !workstation.equals("undefined") && !workstation.isEmpty()) {
                    Predicate _workstation = criteriaBuilder.equal(root.get("workstation"), workstation);
                    predicates.add(_workstation);
                }
                //条件判断
                if(null!=barCode && !"".equals(barCode) &&!"null".equals(barCode) && !workstation.equals("undefined") && !barCode.isEmpty()) {
                    Predicate _barCode = criteriaBuilder.equal(root.get("barCode"), barCode);
                    predicates.add(_barCode);
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
            }
        };
        return failureReasonDataRepository.count(specification);
    }

    @Override
    public FailureReasonData findOneById(Long id) {
        return failureReasonDataRepository.findOne(id);
    }

    /**
     * 根据下底盘条码和工位名称查询记录数
     * @param bottomPlateBarcode
     * @param stationName
     * @return
     */
    @Override
    public int getCountByBottomPlateBarcodeAndStation(String bottomPlateBarcode, String stationName) {
        return failureReasonDataRepository.getCountByBottomPlateBarcodeAndStation(bottomPlateBarcode, stationName);
    }

    /**
     * 查询失败原因中状态为0的数据
     * @param barCode
     * @return
     */
    @Override
    public List<FailureReasonData> getFailureReasonByCode(String barCode) {
        Specification<FailureReasonData> specification = new Specification<FailureReasonData>() {
            @Override
            public Predicate toPredicate(Root<FailureReasonData> root,
                                         CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //条件判断
                if(null!=barCode && !"".equals(barCode)&& !"null".equals(barCode) && !barCode.equals("undefined")) {
                    Predicate _barCode = criteriaBuilder.equal(root.get("barCode"), barCode);
                    Predicate _status =criteriaBuilder.equal(root.get("status"),"0");
                    predicates.add(_barCode);
                    predicates.add(_status);
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
            }
        };
        return failureReasonDataRepository.findAll(specification);
    }

    /**
     * 更新失败原因状态从0到1
     * @param failureReasonDataList
     */
    @Override
    public void updateFailureReasonData(List<FailureReasonData> failureReasonDataList) {
        if(failureReasonDataList!=null){
            for(FailureReasonData failureReasonData:failureReasonDataList){
                failureReasonData.setStatus("1");
                failureReasonDataRepository.saveAndFlush(failureReasonData);
            }
        }
    }

    @Override
    public List<FailureReasonData> getKnobsAndBurnResultMarkByPalletNo(String palletNo, String point) {
        //通过托盘号查询
        Pallet pallet=palletService.findPalletByPalletNo(palletNo);
        String bottomPlateBarCode=pallet.getBottomPlaceCode();
        Specification<FailureReasonData> specification = new Specification<FailureReasonData>() {
            @Override
            public Predicate toPredicate(Root<FailureReasonData> root,
                                         CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //条件判断
                Predicate _productNo = criteriaBuilder.equal(root.get("barCode"), bottomPlateBarCode);
                predicates.add(_productNo);
                Predicate _barCode = criteriaBuilder.equal(root.get("point"), point);
                predicates.add(_barCode);
                Predicate value = criteriaBuilder.equal(root.get("status"), "OK");
                predicates.add(value);
                return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
            }
        };
        return failureReasonDataRepository.findAll(specification);
    }

    @Override
    public List<FailureReasonData> getKnobsAndBurnTestResult(String palletNo, String workStation) {
        //通过托盘号查询
        Pallet pallet=palletService.findPalletByPalletNo(palletNo);
        String bottomPlateBarCode=pallet.getBottomPlaceCode();
        Specification<FailureReasonData> specification = new Specification<FailureReasonData>() {
            @Override
            public Predicate toPredicate(Root<FailureReasonData> root,
                                         CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //条件判断
                Predicate _barCode = criteriaBuilder.equal(root.get("barCode"), bottomPlateBarCode);
                predicates.add(_barCode);
                Predicate _workStation = criteriaBuilder.equal(root.get("workstation"), workStation);
                predicates.add(_workStation);
                Predicate value = criteriaBuilder.equal(root.get("status"), "0");
                predicates.add(value);
                return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
            }
        };
        return failureReasonDataRepository.findAll(specification);
    }
}
