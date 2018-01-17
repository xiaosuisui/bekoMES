package com.mj.beko.service;

import com.mj.beko.domain.Order;
import org.springframework.data.domain.Page;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * Created by Ricardo on 2017/8/23.
 */
public interface OrderService extends BaseService<Order> {
    /**
     * 查询所有订单数量
     * @return
     */
    String getAllUserCount();

    /**
     * 通过Id查询Order
     * @param id
     * @return
     */
    Order getOrder(Long id);

    /**
     * 通过Id删除订单
     * @param id
     */
    void delete(Long id);

    /**
     * 判断当前上线产品属于哪个订单,并获得下一个订单
     *
     */
    Map<String,Order> getCurrentOrderAndNextOrder();

    /**
     * 根据订单编号修改上线数量
     * @param orderNo
     */
    void updateOnlineNumByOrderNo(String orderNo);

    /**
     * 根据订单编号修改订单状态
     * @param orderNo
     * @param status
     * @param startDate
     */
    void updateOrderStatusByOrderNo(String orderNo, String status, Timestamp startDate);

    /**
     * 分页条件查询
     * @param orderNo
     * @param productNo
     * @param page
     * @param size
     * @return
     */
    Page<Order> findAllByPageAndCondition(String orderNo, String productNo, int page, int size,String currentState);

    /**
     * 获取条件查询下的总记录数
     * @param orderNo
     * @param productNo
     * @return
     */
    long getAllCountByCondition(String orderNo,String productNo,String currentState);

    /**
     * 根据状态查询订单列表
     * @param status
     * @return
     */
    List<Order> findOrdersByStatus(String status);

    /**
     * 根据多个订单号查询订单列表
     * @param currentOrderNo
     * @param nextOrderNo
     * @return
     */
    List<Order> getOrderListByOrderNos(String currentOrderNo, String nextOrderNo);
    //查询当前的订单列表(不包含正在生产订单的订单列表)
    List<Order> getOrderListForShiftTarget();

    /**
     * 根据orderNo修改坏件数量
     * @param currentOrderNo
     */
    void updateBrokenNumByOrderNo(String currentOrderNo);
    //查询系统中的前6个即将生产的订单
     List<Order> getSixOrderListForTvScreen();

    /**
     * 根据订单号修改订单表中的完成数量
     * @param orderNo
     */
    void updateCompletionNumberByOrderNo(String orderNo);

    /**
     * 当工艺拉取成功时根据订单号将订单状态改为0
     * @param orderNo
     */
    void updateOrderStatusByOrderNoWhenOperationOk(String orderNo);

    /**
     * 当completion_number+broken_number=quantity时根据订单号将订单结束
     * @param orderNo
     */
    int finishOrderByOrderNo(String orderNo);

    /**
     *
     * @param orderNo
     * 通过orderNo查询Order
     * @return
     */
    Order getOneOrderByOrderNo(String orderNo);
    //判断当前订单下的订单跟当前订单的当前工位的物料类型一致的数量,托盘的标准容量(palletCapility)
    Map<String,String> getSameMaterialQuantityAndNextOrderNo(Order order,int leftNumber,int palletCapility,String stationName);
}
