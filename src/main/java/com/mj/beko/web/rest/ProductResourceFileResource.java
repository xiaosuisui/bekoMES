package com.mj.beko.web.rest;

import com.mj.beko.domain.Order;
import com.mj.beko.domain.ProductResourceFile;
import com.mj.beko.domain.ProductResourceFileVm;
import com.mj.beko.service.ProductResourceFileService;
import com.mj.beko.util.HeaderUtil;
import com.mj.beko.util.HttpResponseHeader;
import com.mj.beko.util.PageUtil;
import com.mj.beko.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * Created by MOUNTAIN on 2017/10/25.
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class ProductResourceFileResource {

    private static final String ENTITY_NAME = "ProductResourceFile";

    @Autowired
    private ProductResourceFileService productResourceFileService;

/*    *//**
     * 分页查询
     *
     * @param pageUtil
     * @return
     *//*
    @GetMapping("/productResourceFiles")
    public ResponseEntity<List<ProductResourceFile>> queryByPage(PageUtil pageUtil) {
        log.debug("REST request to get a page of ProductResourceFiles");
        List<ProductResourceFile> productResourceFiles = productResourceFileService.queryByPage(pageUtil.getPage(), pageUtil.getSize());
        HttpHeaders responseHeaders = HttpResponseHeader.getResponseHeader();
        return new ResponseEntity<>(productResourceFiles, responseHeaders, HttpStatus.OK);
    }*/

    /**
     * 获取总记录数
     *
     * @return
     */
    @GetMapping("/getAllCountProductResourceFile")
    public ResponseEntity<String> getAllCountProductResourceFile() {
        return new ResponseEntity<String>(productResourceFileService.getAllCountProductResourceFile(), HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
    }

    /**
     * 通过id，返回实体
     *
     * @param id
     * @return
     */
    @GetMapping("/productResourceFiles/{id}")
    public ResponseEntity<ProductResourceFile> getUser(@PathVariable Long id) {
        log.info("request to get a productResourceFile by{}", id);
        return new ResponseEntity<ProductResourceFile>(productResourceFileService.getProductById(id), HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
    }

    /**
     * 新增一个实体
     */
    @PostMapping("/productResourceFiles")
    public ResponseEntity<ProductResourceFile> createProductResourceFile(@RequestBody ProductResourceFileVm productResourceFileVm) throws URISyntaxException {

        log.debug("REST request to save ProductResourceFile : {}", productResourceFileVm);

        if (productResourceFileVm.getId() != null) {
            return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idExists", "A new productResourceFile cannot already have an ID")).body(null);
        } else {
            ProductResourceFile newProductResourceFile = productResourceFileService.createProductResourceFile(productResourceFileVm);
            return new ResponseEntity<ProductResourceFile>(newProductResourceFile, HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
        }
    }

    /**
     * 修改一个实体
     */
    @PutMapping("/productResourceFiles")
    public ResponseEntity<ProductResourceFile> updateProductResourceFile(@RequestBody ProductResourceFileVm productResourceFileVm) throws URISyntaxException {

        log.debug("REST request to update ProductResourceFile : {}", productResourceFileVm);
        Optional<ProductResourceFile> updatedProduct = productResourceFileService.updateProductResourceFile(productResourceFileVm);
        return ResponseUtil.wrapOrNotFound(updatedProduct, HeaderUtil.createAlert("product.updated", productResourceFileVm.getProductNo()));
    }

    /**
     * 通过id，删除实体
     *
     * @param id
     * @return
     */
    @DeleteMapping("/productResourceFiles/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.debug("REST request to delete ProductResourceFiles : {}", id);
        productResourceFileService.deleteProduct(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * productNo的唯一性检测
     *
     * @param request
     * @return
     */
    @PostMapping("/check/productResourceFileProductNo")
    public ResponseEntity<Boolean> checkUnique(HttpServletRequest request) {
        String productNo = request.getParameter("value");
        log.info("check productNo::{}", productNo);
        Optional<ProductResourceFile> productResourceFile = productResourceFileService.findOneByProductNo(productNo);
        return new ResponseEntity<Boolean>(!productResourceFile.isPresent(), HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
    }
    //带参数的查询分页
    @GetMapping("/productResourceFiles")
    public ResponseEntity<List<ProductResourceFile>> orderByPage(int page, int size, String productNo){
        log.info("产品资源带参数的分页查询,{}{}",page,productNo);
        Page<ProductResourceFile> ordersPage =productResourceFileService.findAllByPageAndCondition(productNo,page,size);
        List<ProductResourceFile> orders=ordersPage.getContent();
        return new ResponseEntity<List<ProductResourceFile>>(orders,HttpResponseHeader.getResponseHeader(),HttpStatus.OK);
    }
    //获取总记录数(带查询条件)
    @GetMapping("/getAllProductResouresByCondition")
    public long getCountByCondition(String orderNo,String productNo){
        log.info("产品资源模块带参数的查询总记录数{}",orderNo);
        long result =productResourceFileService.getAllCountByCondition(productNo);
        log.info("返回的结果为{}",result);
        return result;
    }

}
