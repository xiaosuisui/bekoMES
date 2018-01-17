package com.mj.beko.service;

import com.mj.beko.domain.ProductRepair;
import com.mj.beko.domain.ProductResourceFile;
import com.mj.beko.domain.ProductResourceFileVm;
import org.springframework.data.domain.Page;

import java.util.Optional;

/**
 * Created by MOUNTAIN on 2017/10/25.
 */
public interface ProductResourceFileService extends BaseService{
    /**
     * 获取所有的记录数
     * @return
     */
    String getAllCountProductResourceFile();

    /**
     * 根据id查找ProductResourceFile
     */
    ProductResourceFile getProductById(Long id);

    /**
     * 根据id删除ProductResourceFile
     */
    void deleteProduct(Long id);

    /**
     * 通过productNo查找ProductResourceFile
     */
    Optional<ProductResourceFile> findOneByProductNo(String productNo);

    /**
     * 创建ProductResourceFile
     */
    ProductResourceFile createProductResourceFile(ProductResourceFileVm productResourceFileVm);

    /**
     * 修改ProductResourceFile
     */
    Optional<ProductResourceFile> updateProductResourceFile(ProductResourceFileVm productResourceFileVm);

    /*
    *通过workstationId和productNo和type获取一体机操作视频的url
    * @param workstationId 是Workstation的id
    * @param productNo
    * @return String VideoURL
    */
    String getVideoURLbyWorkstationIdAndProductNo(Long workstationId,String productNo);

    /*
    *通过workstationId和productNo和type获取一体组件图片的url
    * @param workstationId 是Workstation的id
    * @param productNo
    * @return String ComponentPhotoURL
    */
    String getComponentPhotoURLbyWorkstationIdAndProductNo(Long workstationId,String productNo);
    /**
     * 分页条件查询记录
     * @param productNo
     * @param page
     * @param size
     * @return
     */
    Page<ProductResourceFile> findAllByPageAndCondition(String productNo, int page, int size);
    /**
     * 分页查询总记录数
     * @param productNo
     * @return
     */
    long getAllCountByCondition(String productNo);
}
