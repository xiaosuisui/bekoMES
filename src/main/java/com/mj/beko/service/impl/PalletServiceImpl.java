package com.mj.beko.service.impl;
import com.mj.beko.domain.Order;
import com.mj.beko.domain.Pallet;
import com.mj.beko.repository.PalletRepository;
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
 * Created by Ricardo on 2017/8/23.
 */
@Service
@Transactional
@Slf4j
public class PalletServiceImpl implements PalletService {
    private static final String currentOrderNo = null;
    private static final String productNo = null;
    private static final String bottomPlaceCode = null;
    @Autowired
    private PalletRepository palletRepository;
    @Override
    public Pallet save(Pallet pallet) {
        log.info("save pallet{}",pallet);
        return palletRepository.save(pallet);
    }
    @Override
    public void delete(Pallet pallet) {

    }
    @Override
    public List<Pallet> query() {
        return null;
    }

    @Override
    public List<Pallet> queryByPage(int page, int size) {
        log.info("分页查询托盘{}{}", page, size);
        return palletRepository.queryByPage(page * size, size);
    }
    /**
     * 获取托盘的总记录数
     * @return
     */
    @Override
    public String getAllCountPallet() {
        log.info("获取总记录数 pallet");
        return String.valueOf(palletRepository.count());
    }
    /**
     * 通过Id查询
     * @param id
     * @return
     */
    @Override
    public Pallet getPallet(Long id) {
        log.info("通过Id查询pallet{}",id);
        return palletRepository.findOne(id);
    }

    @Override
    public void delete(Long id) {
        palletRepository.delete(id);
    }

    @Override
    public int palletBindingProInfo(String palletNo, String currentOrderNo, String productNo, String bottomPlaceCode) {
        log.info("托盘与产品信息绑定");
        return palletRepository.setCurrentOrderNoAndProductNoAndBottomPlaceCode(palletNo, currentOrderNo, productNo, bottomPlaceCode);

    }

    @Override
    public int UnbindingPalletInfo(String palletNo) {
        if (palletNo == null) {
            return 0;
        }
        return palletRepository.setCurrentOrderNoAndProductNoAndBottomPlaceCode(palletNo, currentOrderNo, productNo, bottomPlaceCode);
    }

    @Override
    public Pallet findPalletByPalletNo(String palletNo) {
        return palletRepository.findPalletByPalletNo(palletNo);
    }

    @Override
    public long getAllCountByCondition(String palletName, String palletNo) {
        Specification<Pallet> specification = new Specification<Pallet>() {
            @Override
            public Predicate toPredicate(Root<Pallet> root,
                                         CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //条件判断
                if(null!=palletName && !"".equals(palletName) &&!"null".equals(palletName) && !palletName.equals("undefined") && !palletName.isEmpty()) {
                    Predicate _palletName = criteriaBuilder.equal(root.get("palletName"), palletName);
                    predicates.add(_palletName);
                }
                if(null!=palletNo && !"".equals(palletNo) && !"null".equals(palletNo) && !palletNo.equals("undefined") && !palletNo.isEmpty()){
                    Predicate _palletNo = criteriaBuilder.equal(root.get("palletNo"), palletNo);
                    predicates.add(_palletNo);
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
            }
        };
        return palletRepository.count(specification);
    }

    @Override
    public Page<Pallet> findAllByPageAndCondition(final String palletName, final String palletNo, int page, int size) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(page, size, sort);
        Specification<Pallet> specification = new Specification<Pallet>() {
            @Override
            public Predicate toPredicate(Root<Pallet> root,
                                         CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //条件判断
                if(null!=palletName && !"".equals(palletName)&& !"null".equals(palletName) && !palletName.equals("undefined")) {
                    Predicate _palletName = criteriaBuilder.equal(root.get("palletName"), palletName);
                    predicates.add(_palletName);
                }
                if(null!=palletNo && !"".equals(palletNo)&& !"null".equals(palletNo) &&!palletNo.equals("undefined")){
                    Predicate _palletNo = criteriaBuilder.equal(root.get("palletNo"), palletNo);
                    predicates.add(_palletNo);
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
            }
        };
        return palletRepository.findAll(specification, pageable);
    }

    /**
     * 根据托盘号清除托盘绑定的数据
     * @param palletNo
     */
    @Override
    public void clearPalletData(String palletNo) {
        palletRepository.clearPalletData(palletNo);
    }

    @Override
    public Pallet getPalletByBottomPlateCode(String bottomPlateCode) {
        return palletRepository.getPalletByBottomPlateCode(bottomPlateCode);
    }
}
