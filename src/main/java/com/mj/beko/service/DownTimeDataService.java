package com.mj.beko.service;

import com.mj.beko.domain.DownTimeData;
import com.mj.beko.domain.OperatorLoginData;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by Ricardo on 2017/11/13.
 */
public interface DownTimeDataService extends BaseService<DownTimeData> {
    /**
     * 通过条件查询
     * @param page
     * @param size
     * @return
     */
    Page<DownTimeData> findAllByDownTimeCondition(String workstation, int page, int size);

    /**
     * 条件查询记录数
     * @return
     */

    long getAllDownTimeCountByCondition(String workstation);

    /**
     * 通过Id查询
     * @param id
     * @return
     */
    DownTimeData findOneById(Long id);

    /**
     * 查询最新的四条记录
     * @return
     */
    List<DownTimeData> getDownTimeTopFour();
}
