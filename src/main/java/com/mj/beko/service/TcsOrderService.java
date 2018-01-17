package com.mj.beko.service;

import com.mj.beko.domain.Order;
import com.mj.beko.domain.TcsOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by Ricardo on 2017/8/24.
 */
public interface TcsOrderService {

    TcsOrder save(TcsOrder tcsOrder);

    Page<TcsOrder> findAll(Pageable pageable);

    TcsOrder findOne(Long id);

    void delete(Long id);

    /*自定义分页查询*/
    List<TcsOrder> findAllTcsOrderByPage(int page, int size);

    //通过调拨单的名称查找对应的实体
    List<TcsOrder> findAllByTcsOrderName(String name);

    //更新调拨单数据
    TcsOrder saveAndFlush(TcsOrder tcsOrder);

    //查询总记录数
    String getAllTcsOrder();
    /**
     * 通过条件查询
     * @param tcsOrderName
     * @param stationNo
     * @param page
     * @param size
     * @return
     */
    Page<TcsOrder> findAllByPageAndCondition(String tcsOrderName, String stationNo, int page, int size);

    /**
     * 条件查询记录数
     * @param tcsOrderName
     * @param stationNo
     * @return
     */
    long getAllCountByCondition(String tcsOrderName,String stationNo);

    /**
     * 获取最新的一个EPS类型的调度单
     * @return
     */
    TcsOrder getLastEPpsTypeTcsOrder(String tcsOrderName);

    //查询最新的EPSdown类型的调度单,判断其状态是否完成
    List<TcsOrder> getLastEpsDownTypeTcsOrder();

    //查询系统中BBottomPlate类型未完成且状态为3的调度单
    List<TcsOrder> getLatestTcsOrderForBottomAndTopPlate();
}
