package com.mj.beko.web.rest;

import com.mj.beko.domain.Order;
import com.mj.beko.domain.ProductRepair;
import com.mj.beko.service.ProductRepairService;
import com.mj.beko.util.HttpResponseHeader;
import com.mj.beko.util.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by Administrator on 2017/10/24/024.
 */
@RestController
@RequestMapping("/api")
@Slf4j

public class ProductRepairResource {

    @Autowired
    private ProductRepairService productRepairService;

/*    @GetMapping("/product-repair")
    public ResponseEntity<List<ProductRepair>> queryByPage(PageUtil pageUtil){

        return new ResponseEntity<List<ProductRepair>>(productRepairService.queryByPage(pageUtil.getPage(),pageUtil.getSize()), HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
    }*/

    @GetMapping("/getAllCountProductRepair")
    public ResponseEntity<String> getAllCountProductRepair(){
        return new ResponseEntity<String>(productRepairService.getAllCountProductRepair(),HttpResponseHeader.getResponseHeader(),HttpStatus.OK);
    }
    //带参数的查询分页
    @GetMapping("/product-repair")
    public ResponseEntity<List<ProductRepair>> orderByPage(int page, int size, String productNo){
        log.info("产品返修模块带参数的分页查询,{}{}",page,productNo);
        Page<ProductRepair> productRepairsPage =productRepairService.findAllByPageAndCondition(productNo,page,size);
        List<ProductRepair> productRepairsorders=productRepairsPage.getContent();
        return new ResponseEntity<List<ProductRepair>>(productRepairsorders,HttpResponseHeader.getResponseHeader(),HttpStatus.OK);
    }
    //获取总记录数(带查询条件)
    @GetMapping("/getAllProductRepaireByCondition")
    public long getCountByCondition(String orderNo,String productNo){
        log.info("订单模块带参数的查询总记录数{}",orderNo);
        long result =productRepairService.getAllCountByCondition(productNo);
        log.info("返回的结果为{}",result);
        return result;
    }
}
