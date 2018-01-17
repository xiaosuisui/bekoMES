package com.mj.beko.web.rest;

import com.mj.beko.domain.Order;
import com.mj.beko.domain.OrderUpdateLog;
import com.mj.beko.repository.OrderRepository;
import com.mj.beko.security.SecurityUtils;
import com.mj.beko.service.OrderService;
import com.mj.beko.service.OrderUpdateLogService;
import com.mj.beko.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import static org.hibernate.id.IdentifierGenerator.ENTITY_NAME;

/**
 * Created by Ricardo on 2017/8/23.
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class OrderResource {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderUpdateLogService orderUpdateLogService;

    /**
     * 查询所有订单数量
     * @return
     */
    @GetMapping("/getAllOrders")
    public ResponseEntity<String> getAllCountOrder(){
        log.info("request get All order count");
        return new ResponseEntity<String>(orderService.getAllUserCount(), HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
    }
    /**
     * create Order
     * @param order
     * @return
     */
    @PostMapping("/orders")
    public ResponseEntity<Order> createOrder(@RequestBody Order order){
        if (order.getId() != null) {
            return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new order cannot already have an ID")).body(null);
        }
        return new ResponseEntity<Order>(orderService.save(order), HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id){
        log.debug("REST request to get Order : {}", id);
        Order order = orderService.getOrder(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(order));
    }

    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id){
        log.debug("REST request to delete Order : {}", id);
        Order order=orderService.getOrder(id);
        OrderUpdateLog orderUpdateLog =new OrderUpdateLog();
        orderUpdateLog.setUsername(SecurityUtils.getCurrentUserLogin());
        orderUpdateLog.setOperatorType("delete");
        orderUpdateLog.setModuleName("orderMangement");
        orderUpdateLog.setOperatorTime(DateTimeFormatUtil.getCurrentDateTime());
        orderUpdateLog.setOperatorValue("delete orderNo"+order.getOrderNo());
        orderUpdateLogService.save(orderUpdateLog);
        orderService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    @PutMapping("/orders")
    public ResponseEntity updateOrder(@RequestBody Order order){
        log.debug("REST request to update Order : {}", order);
        if(order.getId() == null){
            return createOrder(order);
        }else{
            OrderUpdateLog orderUpdateLog =new OrderUpdateLog();
            Order oldOrder=orderService.getOrder(order.getId());
            //获取之前的数量
            int quantity=oldOrder.getQuantity();
            //获取之前的状态
            String status=oldOrder.getStatus();
            //获取当前的登录用户名称
            orderUpdateLog.setModuleName("orderMangement");
            orderUpdateLog.setOperatorTime(DateTimeFormatUtil.getCurrentDateTime());
            orderUpdateLog.setUsername(SecurityUtils.getCurrentUserLogin());
            orderUpdateLog.setOperatorType("update");
            //如果当前的订单数量和状态修改后
            if(order.getQuantity()!=quantity||!order.getStatus().equals(status)){
                orderUpdateLog.setOperatorValue(order.getOrderNo()+" qty from "+quantity+" to "
                        +order.getQuantity()+", status from"+status+" to "+order.getStatus());
                orderUpdateLogService.save(orderUpdateLog);
            }
        }
        Order updateOrder = orderRepository.saveAndFlush(order);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, order.getId().toString()))
                .body(updateOrder);
    }

    /**
     * 分页查询pageUtil
     * @param
     * @return
     */
/*    @GetMapping("/orders")
    public ResponseEntity<List<Order>> queryByPage(PageUtil pageUtil){
        log.info("分页查询Order{}", pageUtil);
        return new ResponseEntity<List<Order>>(orderService.queryByPage(pageUtil.getPage(), pageUtil.getSize()), HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
    }*/
    //带参数的查询分页
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> orderByPage(int page,int size,String orderNo,String productNo,String currentState){
        log.info("订单带参数的分页查询,{}{}{}",page,orderNo,currentState);
         Page<Order> ordersPage =orderService.findAllByPageAndCondition(orderNo,productNo,page,size,currentState);
          List<Order> orders=ordersPage.getContent();
        return new ResponseEntity<List<Order>>(orders,HttpResponseHeader.getResponseHeader(),HttpStatus.OK);
    }
    //获取总记录数(带查询条件)
    @GetMapping("/getAllOrdersByCondition")
    public long getCountByCondition(String orderNo,String productNo,String currentState){
        log.info("订单模块带参数的查询总记录数{}{}",orderNo,currentState);
        long result =orderService.getAllCountByCondition(orderNo,productNo,currentState);
        log.info("返回的结果为{}",result);
        return result;
    }
}