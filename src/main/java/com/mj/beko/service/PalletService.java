package com.mj.beko.service;

import com.mj.beko.domain.Pallet;
import org.springframework.data.domain.Page;

/**
 * Created by Ricardo on 2017/8/23.
 */
public interface PalletService extends BaseService<Pallet> {
    /**
     * 查询所有的记录
     * @return
     */
    String getAllCountPallet();

    /**
     * 通过Id查询
     * @param id
     * @return
     */
    Pallet getPallet(Long id);

    void delete(Long id);
    /**
     *托盘绑定产品信息
     */
    int palletBindingProInfo(String palletNo, String currentOrderNo, String productNo, String bottomPlaceCode);

    /**
     *解绑托盘与产品信息
     */
    int UnbindingPalletInfo(String palletNo);

    /**
     * 根据托盘号查询托盘信息
     * @param palletNo
     * @return
     */
    Pallet findPalletByPalletNo(String palletNo);
    /**
     * 条件查询总记录数
     * @param palletName
     * @param palletNo
     * @return
     */
    long getAllCountByCondition(String palletName,String palletNo);

    /**
     * 条件查询
     * @param palletName
     * @param palletNo
     * @param page
     * @param size
     * @return
     */
    Page<Pallet> findAllByPageAndCondition(String palletName, String palletNo, int page, int size);

    /**
     * 根据托盘号清除托盘绑定的数据
     * @param palletNo
     */
    void clearPalletData(String palletNo);
    /**
     * 通过下底盘条码得到产品信息
     * @param bottomPlateCode
     * @return
     */
    Pallet getPalletByBottomPlateCode(String bottomPlateCode);
}
