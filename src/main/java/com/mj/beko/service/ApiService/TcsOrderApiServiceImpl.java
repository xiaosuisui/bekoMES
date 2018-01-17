package com.mj.beko.service.ApiService;

import com.mj.beko.domain.TcsOrder;
import com.mj.beko.repository.TcsOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Ricardo on 2017/11/26.
 */
@Service
@Transactional
@Slf4j
public class TcsOrderApiServiceImpl implements TcsOrderApiService {
    @Autowired
    private TcsOrderRepository tcsOrderRepository;
    @Override
    public List<TcsOrder> getTopTenRecord() {
        log.info("find top ten record tcsOrder");
        return tcsOrderRepository.findTopTenTcsOrder();
    }
    //EPS最近的10条记录
    @Override
    public List<TcsOrder> getTopTenRecordForEps() {
        log.info("get recent 10 record for eps");
        return tcsOrderRepository.getTopTenRecordForEps();
    }
   //plate 最新10条记录
    @Override
    public List<TcsOrder> getTopTenRecordForSupport() {
        log.info("get recent 10 record for plate");
        return tcsOrderRepository.getTopTenRecordForSupport();
    }
}
