package com.mj.beko.service.impl;

import com.mj.beko.domain.ConsumedParts;
import com.mj.beko.domain.Order;
import com.mj.beko.repository.ConsumedPartsRepository;
import com.mj.beko.repository.OrderRepository;
import com.mj.beko.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by Ricardo on 2017/8/23.
 */
@Service
@Slf4j
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ConsumedPartsRepository consumedPartsRepository;

    @Override
    public String getAllUserCount() {
        log.info("获取Order所有订单数量");
        return String.valueOf(orderRepository.count());
    }
    @Override
    public Order save(Order order) {

        return orderRepository.save(order);
    }

    @Override
    public void delete(Order order) {
        orderRepository.delete(order);
    }

    @Override
    public List<Order> query() {
        return null;
    }

    @Override
    public List<Order> queryByPage(int page, int size) {
        return orderRepository.queryByPage(page * size, size);
    }

    @Override
    public Order getOrder(Long id) {
        return orderRepository.findOne(id);
    }

    @Override
    public void delete(Long id) {
        orderRepository.delete(id);
    }

    /**
     * 判断当前上线产品属于哪个订单,并获得下一个订单
     * @return
     */
    @Override
    public Map<String, Order> getCurrentOrderAndNextOrder() {
        Map<String, Order> result = new HashMap<>();
        Order currentOrder = null;
        Order nextOrder = null;

        //获得当前订单：查询订单表里状态为0或者1，上线数量小于quantity数量，并且按照订单计划时间升序后的第一条
        currentOrder = orderRepository.getCurrentOrder();

        //获取下一个订单
        if (currentOrder != null) {
            nextOrder = orderRepository.getNextOrder(currentOrder.getOrderNo());
            if (nextOrder == null) {
                nextOrder = new Order();
            }
        }
        result.put("currentOrder", currentOrder);
        result.put("nextOrder", nextOrder);
        return result;
    }
    /**
     * 根据订单编号修改上线数量
     * @param orderNo
     */
    @Override
    public void updateOnlineNumByOrderNo(String orderNo) {
        orderRepository.updateOnlineNumByOrderNo(orderNo);
    }

    /**
     * 根据订单编号修改订单状态
     * @param orderNo
     * @param status
     * @param startDate
     */
    @Override
    public void updateOrderStatusByOrderNo(String orderNo, String status, Timestamp startDate) {
        orderRepository.updateOrderStatusByOrderNo(orderNo, status, startDate);
    }
    //分页并且带条件查询
    @Override
    public Page<Order> findAllByPageAndCondition(final String orderNo, final String productNo, int page, int size, String currentState) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(page, size, sort);
        Specification<Order> specification = new Specification<Order>() {
            @Override
            public Predicate toPredicate(Root<Order> root,
                                         CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //条件判断
                if(null!=orderNo && !"".equals(orderNo)&& !"null".equals(orderNo) && !orderNo.equals("undefined")) {
                    Predicate _orderNo = criteriaBuilder.equal(root.get("orderNo"), orderNo);
                    predicates.add(_orderNo);
                }
                if(null!=productNo && !"".equals(productNo)&& !"null".equals(productNo) &&!productNo.equals("undefined")){
                    Predicate _productNo = criteriaBuilder.equal(root.get("productNo"), productNo);
                    predicates.add(_productNo);
                }
                if(currentState.contains("orderProduction")){
                   Predicate  _state=criteriaBuilder.and((root.get("status")).in(3));
                   predicates.add(_state);
                }
                if(("app.main.orders").equals(currentState)){
                    Predicate _orderState=criteriaBuilder.and(root.get("status").in(0,1,2));
                    predicates.add(_orderState);
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
            }
        };
        return orderRepository.findAll(specification, pageable);
    }
    /**
     * 分页查询总记录数
     * @param orderNo
     * @param productNo
     * @return
     */
    @Override
    public long getAllCountByCondition(String orderNo, String productNo,String currentState) {
        Specification<Order> specification = new Specification<Order>() {
            @Override
            public Predicate toPredicate(Root<Order> root,
                                         CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //条件判断
                if(null!=orderNo && !"".equals(orderNo) &&!"null".equals(orderNo) && !orderNo.equals("undefined") && !orderNo.isEmpty()) {
                    Predicate _orderNo = criteriaBuilder.equal(root.get("orderNo"), orderNo);
                    predicates.add(_orderNo);
                }
                if(null!=productNo && !"".equals(productNo) && !"null".equals(productNo) && !productNo.equals("undefined") && !productNo.isEmpty()){
                    Predicate _productNo = criteriaBuilder.equal(root.get("productNo"), productNo);
                    predicates.add(_productNo);
                }
                if(currentState.contains("orderProduction")){
                    Predicate  _state=criteriaBuilder.and(root.get("status").in(3));
                    predicates.add(_state);
                }
                if(("app.main.orders").equals(currentState)){
                    Predicate _orderState=criteriaBuilder.and(root.get("status").in (0,1,2));
                    predicates.add(_orderState);
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
            }
        };
        return orderRepository.count(specification);
    }

    /**
10     * 根据状态查询订单列表
     * @param status
     * @return
     */
    @Override
    public List<Order> findOrdersByStatus(String status){
        return orderRepository.findOrdersByStatus(status);
    }

    /**
     * 根据多个订单号查询订单列表
     * @param currentOrderNo
     * @param nextOrderNo
     * @return
     */
    @Override
    public List<Order> getOrderListByOrderNos(String currentOrderNo, String nextOrderNo){
        return orderRepository.getOrderListByOrderNos(currentOrderNo, nextOrderNo);
    }
    //获取当前的订单列表(并不包含)
    @Override
    public List<Order> getOrderListForShiftTarget() {
        log.info("获取当前的订单列表,不包含正在生产的订单编号");
        return orderRepository.getOrderListForShiftTarget();
    }

    /**
     * 根据orderNo修改坏件数量
     * @param currentOrderNo
     */
    @Override
    public void updateBrokenNumByOrderNo(String currentOrderNo) {
        orderRepository.updateBrokenNumByOrderNo(currentOrderNo);
    }

    @Override
    public List<Order> getSixOrderListForTvScreen() {
        log.info("获取当前的订单列表,six");
        return orderRepository.getSixOrderListForTvScreen();
    }
    /**
     * 根据订单号修改订单表中的完成数量
     * @param orderNo
     */
    @Override
    public void updateCompletionNumberByOrderNo(String orderNo) {
        orderRepository.updateCompletionNumberByOrderNo(orderNo);
    }

    /**
     * 当工艺拉取成功时根据订单号将订单状态改为0
     * @param orderNo
     */
    @Override
    public void updateOrderStatusByOrderNoWhenOperationOk(String orderNo) {
        orderRepository.updateOrderStatusByOrderNoWhenOperationOk(orderNo);
    }
    /**
     * 当completion_number+broken_number=quantity时根据订单号将订单结束
     * @param orderNo
     */
    @Override
    public int finishOrderByOrderNo(String orderNo) {
        return orderRepository.finishOrderByOrderNo(orderNo);
    }

    @Override
    public Order getOneOrderByOrderNo(String orderNo) {
        return orderRepository.getOneOrderByOrderNo(orderNo);
    }

    @Override
    public Map<String, String> getSameMaterialQuantityAndNextOrderNo(Order order,int leftNumber,int palletCapility ,String stationName) {
        Map<String,String> map =new HashMap<String,String>();
        int totalNumber=0;
        //查询当前productId的当前工位的物料名称
        String material=getMaterialNameForProductNoAndStation(order.getProductNo(),stationName);
        //查当前工单下的其他订单列表
        Long orderId=order.getId();
        List<Order> nextOrderList =orderRepository.getNextOrderListForCountMaterialChange(orderId);
        //遍历当前的订单列表查看是否有相同的类型并计算其数量(判断下一个订单跟当前的订单的物料是否一致,并计算下一个订单)
        //判断一下订单当前剩下的数量是否满足一个托盘生产
        if(leftNumber>=palletCapility){
            map.put("nowOrder",order.getOrderNo());
        }
        for(Order nextOrder:nextOrderList){
            String materialName=getMaterialNameForProductNoAndStation(nextOrder.getProductNo(),stationName);
            if(material.equals(materialName)){
              totalNumber+=nextOrder.getQuantity();
              //如果生产的物料大于等于托盘的容量,表明当前的订单就是需要暂时截止的单子
              if(totalNumber>(palletCapility-leftNumber) && leftNumber<palletCapility){
                  //nowOrder表示被切割的类型
                  map.put("nowOrder",nextOrder.getOrderNo());
              }
            }else{
                //下一个单子
                String nextorderNo=nextOrder.getOrderNo();
                //表示第一个开始跟上一个物料不相同的单子号
                map.put("orderNo",nextorderNo);
                break;
            }
        }
        map.put("totalNumber",String.valueOf(totalNumber));
        log.info("查询下一个订单，跳跃式查询。判断相同的类型");
        return map;
    }
    public String getMaterialNameForProductNoAndStation(String productNo,String workStation){
        StringBuffer materialName=new StringBuffer();
        List<ConsumedParts> consumedPartsList=consumedPartsRepository.getConsumPartsByProducntNoAndStation(productNo,workStation);
        if(consumedPartsList==null||consumedPartsList.size()<1){
            materialName.append("noMaterialName,");
        }else{
            for(ConsumedParts consumedParts:consumedPartsList){
                materialName.append(consumedParts.getPartName()+",");}
        }
        return materialName.toString();
    }
}
