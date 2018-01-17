package com.mj.beko.web.rest;

import com.mj.beko.domain.Pallet;
import com.mj.beko.domain.ProductCode;
import com.mj.beko.service.ProductCodeService;
import com.mj.beko.service.ProductService;
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
 * Created by Ricardo on 2017/8/24.
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class ProductCodeResource {
    @Autowired
    private ProductCodeService productCodeService;
    //获取总记录数(带查询条件)
    @GetMapping("/getAllProductCodeByCondition")
    public long getCountByCondition(String productNo,String orderNo){
        log.info("productCode模块带参数的查询总记录数{}",productNo);
        long result =productCodeService.getAllCountByCondition(productNo,orderNo);
        log.info("返回的结果为{}",result);
        return result;
    }
    //带参数的查询分页
    @GetMapping("/product-codes")
    public ResponseEntity<List<ProductCode>> orderByPage(int page, int size, String productNo, String orderNo){
        log.info("productCode带参数的分页查询,{}{}",page,productNo);
        Page<ProductCode> productCodestPage =productCodeService.findAllByPageAndCondition(productNo,orderNo,page,size);
        List<ProductCode> productCodes=productCodestPage.getContent();
        return new ResponseEntity<List<ProductCode>>(productCodes,HttpResponseHeader.getResponseHeader(),HttpStatus.OK);
    }

    /**
     * 获取总记录数
     * @return
     */
    @GetMapping("/getAllCountProductCode")
    public ResponseEntity<String> getAllCountProductCode(){
        return new ResponseEntity<String>(productCodeService.getAllCountProductCode(),HttpResponseHeader.getResponseHeader(),HttpStatus.OK);
    }


}
