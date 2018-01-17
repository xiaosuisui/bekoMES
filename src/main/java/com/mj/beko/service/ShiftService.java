package com.mj.beko.service;

import com.mj.beko.domain.OperatorShift;
import com.mj.beko.domain.OperatorShiftDetail;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by Ricardo on 2017/12/1.
 */
public interface ShiftService extends BaseService<OperatorShift> {
    OperatorShift getOperatorShift(Long id);
    void delete(Long id);

    /**
     * 根据条件获取总记录数
     * @param name
     * @return
     */
    long getCountShiftByCondition(String name);

    /**
     * 条件获取所有的记录
     * @param name
     * @param page
     * @param size
     * @return
     */
    Page<OperatorShift> findAllShiftsByPageAndCondition(String name, int page, int size);

    /**
     * 获取当前系统中使用的shift
     * @return
     */
    OperatorShift getCurrentShift();

    /**
     * 查询当前shift的开始时间和结束时间
     * @param ShiftName
     * @return
     */
    List<OperatorShiftDetail> getOperatorShiftByShiftName(Long shiftId, String ShiftName);
}
