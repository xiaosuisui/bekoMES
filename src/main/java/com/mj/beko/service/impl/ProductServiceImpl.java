package com.mj.beko.service.impl;

import com.mj.beko.domain.Product;
import com.mj.beko.domain.ProductVm;
import com.mj.beko.listener.PictureClearPublisher;
import com.mj.beko.repository.ProductRepository;
import com.mj.beko.service.ProductService;
import com.mj.beko.util.StrOperatorUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Ricardo on 2017/8/24.
 */
@Service
@Transactional
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private PictureClearPublisher demoPublisher;

    @Override
    public Product save(Product product) {
        log.info("save product{}",product);
        return productRepository.save(product);
    }
    @Override
    public void delete(Product product) {

    }
    @Override
    public List<Product> query() {
        log.info("查询所有的产品");
        return productRepository.findAll();
    }
    @Override
    public List<Product> queryByPage(int page, int size) {
        log.info("分页查询product,{}{}", page, size);
        return productRepository.queryByPage(page * size, size);
    }
    @Override
    public String getAllCountProduct() {
        log.info("查询所有的记录数");
        return String.valueOf(productRepository.count());
    }

    @Override
    public Product createProduct(ProductVm productVm) {
        Product product = new Product(productVm.getProductNo(),productVm.getProductName(),productVm.getType(),productVm.getQrcode());
        /*判断图片*/
        if (StrOperatorUtil.strIsBlank(productVm.getImage())) {
            product.setPicPath(null);
        } else {
            String path = request.getSession().getServletContext().getRealPath("upload");
            byte[] imageBytes = base64Decoding(productVm.getImage());
            String localPath = UUID.randomUUID() + "_" + productVm.getFileName();
            try{
                Files.write(Paths.get(path, localPath), imageBytes, StandardOpenOption.CREATE);
                product.setPicPath(localPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.delete(id);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findOne(id);
    }

    @Override
    public Optional<Product> update(ProductVm productVm) {
        return Optional.of(productRepository.findOne(productVm.getId())).map(
                product -> {
                    //先判断当前的图片是否被修改
                    if (StrOperatorUtil.strIsBlank(productVm.getPicPath())) {
                        demoPublisher.publish(product.getPicPath());
                       /*判断图片*/
                        if (StrOperatorUtil.strIsBlank(productVm.getImage())) {
                            product.setPicPath(null);
                        } else {
                            String path = request.getSession().getServletContext().getRealPath("upload");
                            byte[] imageBytes = base64Decoding(productVm.getImage());
                            String localPath = UUID.randomUUID() + "_" + productVm.getFileName();
                            try {
                                Files.write(Paths.get(path, localPath), imageBytes, StandardOpenOption.CREATE);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            product.setPicPath(localPath);
                        }
                    }
                    product.setProductNo(productVm.getProductNo());
                    product.setProductName(productVm.getProductName());
                    product.setType(productVm.getType());
                    product.setQrcode(productVm.getQrcode());
                    return product;
                });
    }

    private byte[] base64Decoding(String image) {
        if (StrOperatorUtil.strIsNotBlank(image)) {
            return Base64.getDecoder().decode(image.split("base64,")[1]);
        } else {
            return null;
        }
    }
}
