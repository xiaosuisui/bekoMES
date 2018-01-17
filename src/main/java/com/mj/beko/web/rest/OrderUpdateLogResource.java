package com.mj.beko.web.rest;

import com.mj.beko.domain.OrderUpdateLog;
import com.mj.beko.domain.Pallet;
import com.mj.beko.domain.TcsOrder;
import com.mj.beko.service.OrderUpdateLogService;
import com.mj.beko.util.HeaderUtil;
import com.mj.beko.util.HttpResponseHeader;
import com.mj.beko.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import static org.hibernate.id.IdentifierGenerator.ENTITY_NAME;

/**
 * Created by Ricardo on 2017/11/9.
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class OrderUpdateLogResource {
    @Autowired
    private OrderUpdateLogService orderUpdateLogService;
    //带参数的查询分页
    @GetMapping("/orderUpdateLog")
    public ResponseEntity<List<OrderUpdateLog>> orderByPage(int page, int size, String username, String operatorType){
        log.info("订单日志带参数的分页查询,{}{}",page,username);
        Page<OrderUpdateLog> orderUpdatePage =orderUpdateLogService.findAllByUpdateLogCondition(username,operatorType,page,size);
        List<OrderUpdateLog> orderUpdateLogs=orderUpdatePage.getContent();
        return new ResponseEntity<List<OrderUpdateLog>>(orderUpdateLogs, HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
    }
    //获取总记录数(带查询条件)
    @GetMapping("/getAllOrderUpdateLogByCondition")
    public long getCountByCondition(String username,String operatorType){
        log.info("订单修改日志模块带参数的查询总记录数{}",username);
        long result =orderUpdateLogService.getAllCountByCondition(username,operatorType);
        log.info("返回的结果为{}",result);
        return result;
    }
    @PostMapping("/orderUpdateLog")
    public ResponseEntity<OrderUpdateLog> createPallet(@RequestBody OrderUpdateLog orderUpdateLog) throws URISyntaxException {
        log.info("添加一条订单模块修改记录");
        if (orderUpdateLog.getId() != null) {
            return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new pallet cannot already have an ID")).body(null);
        }
        OrderUpdateLog result = orderUpdateLogService.save(orderUpdateLog);
        return ResponseEntity.created(new URI("/api/pallets/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
                .body(result);
    }
    @GetMapping("/orderUpdateLog/{id}")
    public ResponseEntity<OrderUpdateLog> getOrder(@PathVariable Long id) {
        log.debug("REST request to get orderUpdateLog : {}", id);
        OrderUpdateLog orderUpdateLog = orderUpdateLogService.getOrderUpdateLog(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(orderUpdateLog));
    }
}
