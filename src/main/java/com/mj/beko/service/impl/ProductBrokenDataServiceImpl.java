package com.mj.beko.service.impl;

import com.mj.beko.domain.ProductBrokenData;
import com.mj.beko.repository.ProductBrokenDataRepository;
import com.mj.beko.service.ProductBrokenDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Created by Ricardo on 2018/1/13.
 */
@Service
@Transactional
@Slf4j
public class ProductBrokenDataServiceImpl implements ProductBrokenDataService {
    @Autowired
    private ProductBrokenDataRepository productBrokenDataRepository;
    @Override
    public ProductBrokenData getProductBrokenDataByBottomPlateCode(String bottomPlateBarCode) {
        log.info("get broken product info by bottomPlateBarCode",bottomPlateBarCode);
        return productBrokenDataRepository.getProductBrokenDataByBottomPlateBarCode(bottomPlateBarCode);
    }
}
