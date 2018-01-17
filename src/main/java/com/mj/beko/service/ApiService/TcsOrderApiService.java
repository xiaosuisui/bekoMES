package com.mj.beko.service.ApiService;

import com.mj.beko.domain.TcsOrder;

import java.util.List;

/**
 * Created by Ricardo on 2017/11/26.
 */
public interface TcsOrderApiService {
    /**
     * 查找最新的10条记录for滚筒线和流利架物料区
     * @return
     */
    List<TcsOrder> getTopTenRecord();

    /**
     * 查找最新的10条的EPS的记录
     * @return
     */
    List<TcsOrder> getTopTenRecordForEps();

    /**
     * 查询物料小车的最新的10条记录
     * @return
     */
    List<TcsOrder> getTopTenRecordForSupport();
}
