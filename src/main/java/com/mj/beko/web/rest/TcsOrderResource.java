package com.mj.beko.web.rest;

import com.mj.beko.domain.Order;
import com.mj.beko.domain.TcsOrder;
import com.mj.beko.service.TcsOrderService;
import com.mj.beko.util.HttpResponseHeader;
import com.mj.beko.util.PageUtil;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by Ricardo on 2017/8/24.
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class TcsOrderResource {
    @Autowired
    private TcsOrderService tcsOrderService;
    //带参数的查询分页
    @GetMapping("/tcs-orders")
    public ResponseEntity<List<TcsOrder>> orderByPage(int page, int size, String tcsOrderName, String stationNo){
        log.info("调度订单带参数的分页查询,{}{}",page,tcsOrderName);
        Page<TcsOrder> tcsOrdersPage =tcsOrderService.findAllByPageAndCondition(tcsOrderName,stationNo,page,size);
        List<TcsOrder> tcsOrders=tcsOrdersPage.getContent();
        return new ResponseEntity<List<TcsOrder>>(tcsOrders,HttpResponseHeader.getResponseHeader(),HttpStatus.OK);
    }
    //获取总记录数(带查询条件)
    @GetMapping("/getAllTcsOrdersByCondition")
    public long getCountByCondition(String tcsOrderName,String stationNo){
        log.info("调度订单模块带参数的查询总记录数{}",tcsOrderName);
        long result =tcsOrderService.getAllCountByCondition(tcsOrderName,stationNo);
        log.info("返回的结果为{}",result);
        return result;
    }
}
