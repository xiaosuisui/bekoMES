package com.mj.beko.web.rest;

import com.mj.beko.domain.Product;
import com.mj.beko.domain.ProductVm;
import com.mj.beko.repository.ProductRepository;
import com.mj.beko.service.ProductService;
import com.mj.beko.util.HeaderUtil;
import com.mj.beko.util.HttpResponseHeader;
import com.mj.beko.util.PageUtil;
import com.mj.beko.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * Created by Ricardo on 2017/8/24.
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class ProductResource {
    private static final String ENTITY_NAME = "product";
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    /**
     * 分页查询
     * @param pageUtil
     * @return
     */
    @GetMapping("/products")
    public ResponseEntity<List<Product>> queryByPage(PageUtil pageUtil) {
        log.debug("REST request to get a page of Products");
        List<Product> products = productService.queryByPage(pageUtil.getPage(),pageUtil.getSize());
        HttpHeaders responseHeaders= HttpResponseHeader.getResponseHeader();
        return new ResponseEntity<>(products, responseHeaders, HttpStatus.OK);
    }
    /**
     * 获取总记录数
     * @return
     */
    @GetMapping("/getAllCountProduct")
    public ResponseEntity<String> getAllCountProduct(){
        return new ResponseEntity<String>(productService.getAllCountProduct(),HttpResponseHeader.getResponseHeader(),HttpStatus.OK);
    }

    /**
     * 新增一个product
     *
     * @param product the product to create
     * @return the ResponseEntity with status 201 (Created) and with body the new product, or with status 400 (Bad Request) if the product has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestBody ProductVm product) throws URISyntaxException {

        log.debug("REST request to save Product : {}", product);

        if (product.getId() != null) {
            return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idExists", "A new product cannot already have an ID")).body(null);
        } else if (productRepository.findOneByProductNo(product.getProductNo()).isPresent()) {
            return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "productExists", "productNo already in use")).body(null);
        } else {
            Product newProduct = productService.createProduct(product);
            return new ResponseEntity<Product>(newProduct, HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
        }
    }
    /**
     * 修改一个product
     *
     * @param product the product to create
     * @return the ResponseEntity with status 201 (Created) and with body the new product, or with status 400 (Bad Request) if the product has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/products")
    public ResponseEntity<Product> updateProduct(@RequestBody ProductVm product) throws URISyntaxException {

        log.debug("REST request to update Product : {}", product);
        Optional<Product> existingProduct = productRepository.findOneByProductNo(product.getProductNo());
        if (existingProduct.isPresent()&&!existingProduct.get().getId().equals(product.getId())) {
            return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "productExists", "productNo already in use")).body(null);
        }
        Optional<Product> updatedProduct = productService.update(product);
        return ResponseUtil.wrapOrNotFound(updatedProduct, HeaderUtil.createAlert("product.updated", product.getProductNo()));
    }
    /**
     * DELETE  /products/:id : delete the "id" product.
     *
     * @param id the id of the product to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.debug("REST request to delete Product : {}", id);
        productService.deleteProduct(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
    /**
     * 通过id，返回实体
     * @param id
     * @return
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getUser(@PathVariable Long id){
        log.info("request to get a User by{}", id);
        return new ResponseEntity<Product>(productService.getProductById(id), HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
    }

    /**
     * productNo的唯一性检测
     * @param request
     * @return
     */
    @PostMapping("/check/productNo")
    public ResponseEntity<Boolean> checkLogin(HttpServletRequest request){
        String productNo = request.getParameter("value");
        log.info("check productNo::{}", productNo);
        Optional<Product> product = productRepository.findOneByProductNo(productNo);
        return new ResponseEntity<Boolean>(!product.isPresent(), HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
    }
}
