package com.mj.beko.repository;

import com.mj.beko.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * Created by Ricardo on 2017/8/23.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long>,JpaSpecificationExecutor {

    /**
     * 分页查询
     * @param offset
     * @param size
     * @return
     */
    @Query(value = "select * from t_order ORDER BY id DESC OFFSET :offset ROW  FETCH NEXT :size ROW ONLY", nativeQuery = true)
    List<Order> queryByPage(@Param("offset") int offset, @Param("size") int size);

    /**
     * 获取下一个工单
     * @param orderNo
     * @return
     */
    @Query(value = "SELECT TOP 1 * FROM t_order WHERE status = 0 AND order_no != ?1 ORDER BY operation_date_time", nativeQuery = true)
    Order getNextOrder(String orderNo);

    /**
     * 获取当前工单
     * @return
     */
    @Query(value = "SELECT TOP 1 * FROM t_order o WHERE status = 0 OR status = 1 and o.quantity > o.online_number ORDER BY status DESC,operation_date_time", nativeQuery = true)
    Order getCurrentOrder();

    /**
     * 根据订单编号修改上线数量
     * @param orderNo
     */
    @Modifying
    @Query(value = "UPDATE t_order SET online_number = online_number + 1 WHERE order_no = ?1", nativeQuery = true)
    void updateOnlineNumByOrderNo(String orderNo);

    /**
     * 根据订单编号修改订单状态
     * @param orderNo
     * @param status
     * @param startDate
     */
    @Modifying
    @Query(value = "UPDATE t_order SET status = ?2, start_date = ?3 WHERE order_no = ?1", nativeQuery = true)
    void updateOrderStatusByOrderNo(String orderNo, String status, Timestamp startDate);

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
    @Query(value = "select * from t_order where order_no in (?1, ?2) order by operation_date_time", nativeQuery = true)
    List<Order> getOrderListByOrderNos(String currentOrderNo, String nextOrderNo);

    /**
     * 查询订单列表,暂定为20个单子
     * status in(0,create 1,start,4 工艺,3完成)
     * @return
     */
    @Query(value = "select top 20 * from t_order where status in (0,4) order by operation_date_time ",nativeQuery = true)
    List<Order> getOrderListForShiftTarget();

    @Query(value = "select top 6 * from t_order where status in (0,1,4) order by operation_date_time ",nativeQuery = true)
    List<Order> getSixOrderListForTvScreen();

    /**
     * 根据orderNo修改坏件数量
     * @param currentOrderNo
     */
    @Modifying
    @Query(value = "UPDATE t_order SET broken_number = broken_number + 1 WHERE order_no = ?1", nativeQuery = true)
    void updateBrokenNumByOrderNo(String currentOrderNo);

    /**
     * 根据订单号修改订单表中的完成数量
     * @param orderNo
     */
    @Modifying
    @Query(value = "UPDATE t_order SET completion_number = completion_number + 1 WHERE order_no = ?1", nativeQuery = true)
    void updateCompletionNumberByOrderNo(String orderNo);

    /**
     * 当工艺拉取成功时根据订单号将订单状态改为0
     * @param orderNo
     */
    @Modifying
    @Query(value = "UPDATE t_order SET status = '0' WHERE order_no = ?1", nativeQuery = true)
    void updateOrderStatusByOrderNoWhenOperationOk(String orderNo);

    /**
     * 当completion_number+broken_number=quantity时根据订单号将订单结束
     * @param orderNo
     */
    @Modifying
    @Query(value = "UPDATE t_order SET status = '3' WHERE order_no = ?1 and quantity = completion_number + broken_number", nativeQuery = true)
    int finishOrderByOrderNo(String orderNo);

    @Query(value = "select top 1 * from t_order where order_no=:orderNo ",nativeQuery = true)
    Order getOneOrderByOrderNo(@Param("orderNo") String orderNo);

    //查看当前订单下的订单列表,订单中Id大于当前订单,按照操作时间排序的单子列表,状态为创建开始或者未开始状态的。
    @Query(value = "select top 100 * from t_order where status in(0,1,4) and id>:orderId order by operation_date_time ",nativeQuery = true)
    List<Order> getNextOrderListForCountMaterialChange(@Param("orderId") long orderId);
}
