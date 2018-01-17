package com.mj.beko.service;

import com.mj.beko.domain.OrderUpdateLog;
import com.mj.beko.domain.TcsOrder;
import org.springframework.data.domain.Page;

/**
 * Created by Ricardo on 2017/11/9.
 */
public interface OrderUpdateLogService extends BaseService<OrderUpdateLog>{
    /**
     * 通过条件查询
     * @param username
     * @param operatorType
     * @param page
     * @param size
     * @return
     */
    Page<OrderUpdateLog> findAllByUpdateLogCondition(String username, String operatorType, int page, int size);

    /**
     * 条件查询记录数
     * @param username
     * @param operatorType
     * @return
     */

    long getAllCountByCondition(String username,String operatorType);

     OrderUpdateLog getOrderUpdateLog(Long id);
   }
