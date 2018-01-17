package com.mj.beko.service;

import com.mj.beko.domain.OperatorLoginData;
import org.springframework.data.domain.Page;

/**
 * Created by Ricardo on 2017/11/6.
 */
public interface OperatorLoginDataService extends BaseService<OperatorLoginData> {
    /**
     * 通过Id查询
     * @param id
     * @return
     */
    OperatorLoginData findOneById(Long id);
    /**
     * 删除
     * @param id
     */
    void delete(Long id);
    /**
     * 条件查询总记录数
     * @param operator
     * @param workstation
     * @return
     */
    long getAllCountByCondition(String operator,String workstation);

    /**
     * 通过工位查询当前的登录用户名
     * @param workstation
     * @return
     */
     String findOneByWorkStation(String workstation);

    /**
     * 分页条件查询员工操作记录信息
     * @param operator
     * @param workstation
     * @param page
     * @param size
     * @return
     */
    Page<OperatorLoginData> findAllByPageAndCondition(String operator, String workstation, int page, int size);



}

