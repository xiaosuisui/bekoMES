package com.mj.beko.service;

import com.mj.beko.domain.Product;
import com.mj.beko.domain.ProductVm;

import java.util.Optional;

/**
 * Created by Ricardo on 2017/8/24.
 */
public interface ProductService extends BaseService<Product> {
    /**
     * 获取所有的记录数
     * @return
     */
    String getAllCountProduct();
    /**
     * 创建Product
     */
    Product createProduct(ProductVm productVm);
    /**
     * 根据id删除Product
     */
    void deleteProduct(Long id);
    /**
     * 根据id查找Product
     */
    Product getProductById(Long id);

    Optional<Product> update(ProductVm productVm);
}
