package com.mj.beko.service.impl;

import com.mj.beko.domain.Pallet;
import com.mj.beko.domain.ProductResourceFile;
import com.mj.beko.domain.ProductResourceFileVm;
import com.mj.beko.repository.ProductResourceFileRepository;
import com.mj.beko.service.ProductResourceFileService;
import com.mj.beko.util.StrOperatorUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * Created by MOUNTAIN on 2017/10/25.
 */
@Service
@Slf4j
@Transactional
public class ProductResourceFileServiceImpl implements ProductResourceFileService{
    @Autowired
    private ProductResourceFileRepository productResourceFileRepository;

    @Autowired
    private HttpServletRequest request;

    @Override
    public Object save(Object o) {
        return null;
    }

    @Override
    public void delete(Object o) {

    }

    @Override
    public List query() {
        return null;
    }

    @Override
    public List queryByPage(int page, int size) {
        log.info("分页查询productResourceFile,{}{}", page, size);
        return productResourceFileRepository.queryByPage(page * size, size);
    }

    @Override
    public String getAllCountProductResourceFile() {
        log.info("查询所有的记录数");
        return String.valueOf(productResourceFileRepository.count());
    }

    @Override
    public ProductResourceFile getProductById(Long id) {
        log.info("find ProductResourceFile by id");
        return productResourceFileRepository.findOne(id);
    }

    @Override
    public void deleteProduct(Long id) {
        //删除旧文件
        ProductResourceFile productResourceFile = productResourceFileRepository.findOne(id);
        String oldFilePath = request.getSession().getServletContext().getRealPath("upload")+"\\prfs\\"+productResourceFile.getProductNo()+"\\"+productResourceFile.getWorkstationId()+"\\"+productResourceFile.getType()+"\\"+productResourceFile.getStorageLocation();
        File oldFile = new File(oldFilePath);
        if (oldFile.exists()) {
            oldFile.delete();
        }
        productResourceFileRepository.delete(id);
    }

    @Override
    public Optional<ProductResourceFile> findOneByProductNo(String productNo) {
        return productResourceFileRepository.findOneByProductNo(productNo);
    }

    @Override
    public ProductResourceFile createProductResourceFile(ProductResourceFileVm productResourceFileVm) {
        ProductResourceFile productResourceFile = new ProductResourceFile();
        productResourceFile.setProductNo(productResourceFileVm.getProductNo());
        productResourceFile.setWorkstationId(productResourceFileVm.getWorkstationId());
        productResourceFile.setType(productResourceFileVm.getType());
        /*判断文件*/
        if (StrOperatorUtil.strIsBlank(productResourceFileVm.getUploadFile())) {
            productResourceFile.setStorageLocation(null);
        } else {
            //文件保存路径规则：upload/prfs/{productNo}/{workstationId}/{type}/新文件名
            String path = request.getSession().getServletContext().getRealPath("upload")+"\\prfs\\"+productResourceFileVm.getProductNo()+"\\"+productResourceFile.getWorkstationId()+"\\"+productResourceFile.getType();
            byte[] fileBytes = base64Decoding(productResourceFileVm.getUploadFile());
            //生成新文件名
            String localPath = UUID.randomUUID() + "_" + productResourceFileVm.getFileName();
            try{
                //创建文件夹目录
                File dirs = new File(path);
                if (!dirs.exists()) {
                    dirs.mkdirs();
                }
                Files.write(Paths.get(path,localPath), fileBytes, StandardOpenOption.CREATE);
                productResourceFile.setStorageLocation(localPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return productResourceFileRepository.save(productResourceFile);
    }

    @Override
    public Optional<ProductResourceFile> updateProductResourceFile(ProductResourceFileVm productResourceFileVm) {
        return Optional.of(productResourceFileRepository.findOne(productResourceFileVm.getId())).map(
                productResourceFile -> {
                    //先判断当前的图片是否被修改
                    //此模块是用来上传工位资源文件的，在页面保存操作里已经做了上传文件的非空校验因此资源文件存在即StorageLocation不为空
                    String path = request.getSession().getServletContext().getRealPath("upload")+"\\prfs\\"+productResourceFileVm.getProductNo()+"\\"+productResourceFileVm.getWorkstationId()+"\\"+productResourceFileVm.getType();
                    byte[] fileBytes = base64Decoding(productResourceFileVm.getUploadFile());
                    String localPath = UUID.randomUUID() + "_" + productResourceFileVm.getFileName();
                    try{
                        File dirs = new File(path);
                        if (!dirs.exists()) {
                            dirs.mkdirs();
                        }
                        Files.write(Paths.get(path,localPath), fileBytes, StandardOpenOption.CREATE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //删除旧文件
                    String oldFilePath = request.getSession().getServletContext().getRealPath("upload")+"\\prfs\\"+productResourceFile.getProductNo()+"\\"+productResourceFile.getWorkstationId()+"\\"+productResourceFile.getType()+"\\"+productResourceFile.getStorageLocation();
                    File oldFile = new File(oldFilePath);
                    if (oldFile.exists()) {
                        oldFile.delete();
                    }
                    productResourceFile.setStorageLocation(localPath);
                    productResourceFile.setProductNo(productResourceFileVm.getProductNo());
                    productResourceFile.setWorkstationId(productResourceFileVm.getWorkstationId());
                    productResourceFile.setType(productResourceFileVm.getType());
                    return productResourceFile;
                });
    }

    @Override
    public String getVideoURLbyWorkstationIdAndProductNo(Long workstationId, String productNo) {
        String type = "Video";
        List<ProductResourceFile> productResourceFileList = productResourceFileRepository.findByWorkstationIdAndProductNoAndType(workstationId,productNo,type);
        if (productResourceFileList.size() > 0) {
            ProductResourceFile productResourceFile = productResourceFileList.get(0);
            //判断productResourceFile中storageLocation是否为空
            if (StringUtils.isBlank(productResourceFile.getStorageLocation())) {
                return null;
            }
            return "upload/prfs/"+productResourceFile.getProductNo()+"/"+productResourceFile.getWorkstationId()+"/"+productResourceFile.getType()+"/"+productResourceFile.getStorageLocation();
        }
        return null;
    }

    @Override
    public String getComponentPhotoURLbyWorkstationIdAndProductNo(Long workstationId, String productNo) {
        String type = "Picture";
        List<ProductResourceFile> productResourceFileList  = productResourceFileRepository.findByWorkstationIdAndProductNoAndType(workstationId,productNo,type);
        if (productResourceFileList.size() > 0) {
            ProductResourceFile productResourceFile = productResourceFileList.get(0);
            //判断productResourceFile中storageLocation是否为空
            if (StringUtils.isBlank(productResourceFile.getStorageLocation())) {
                return null;
            }
            return "upload/prfs/"+productResourceFile.getProductNo()+"/"+productResourceFile.getWorkstationId()+"/"+productResourceFile.getType()+"/"+productResourceFile.getStorageLocation();
        }
        return null;
    }

    @Override
    public Page<ProductResourceFile> findAllByPageAndCondition(String productNo, int page, int size) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(page, size, sort);
        Specification<ProductResourceFile> specification = new Specification<ProductResourceFile>() {
            @Override
            public Predicate toPredicate(Root<ProductResourceFile> root,
                                         CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //条件判断
                if(null!=productNo && !"".equals(productNo)&& !"null".equals(productNo) && !productNo.equals("undefined")) {
                    Predicate _productNo = criteriaBuilder.equal(root.get("productNo"), productNo);
                    predicates.add(_productNo);
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
            }
        };
        return productResourceFileRepository.findAll(specification, pageable);
    }

    @Override
    public long getAllCountByCondition(String productNo) {
        Specification<ProductResourceFile> specification = new Specification<ProductResourceFile>() {
            @Override
            public Predicate toPredicate(Root<ProductResourceFile> root,
                                         CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //条件判断
                if(null!=productNo && !"".equals(productNo) &&!"null".equals(productNo) && !productNo.equals("undefined") && !productNo.isEmpty()) {
                    Predicate _productNo = criteriaBuilder.equal(root.get("productNo"), productNo);
                    predicates.add(_productNo);
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
            }
        };
        return productResourceFileRepository.count(specification);
    }

    private byte[] base64Decoding(String image) {
        if (StrOperatorUtil.strIsNotBlank(image)) {
            return Base64.getDecoder().decode(image.split("base64,")[1]);
        } else {
            return null;
        }
    }
}
