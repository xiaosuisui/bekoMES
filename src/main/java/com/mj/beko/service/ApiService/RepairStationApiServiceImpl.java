package com.mj.beko.service.ApiService;

import com.mj.beko.domain.Pallet;
import com.mj.beko.domain.ProductRepair;
import com.mj.beko.repository.ProductRepairRepository;
import com.mj.beko.service.PalletService;
import com.mj.beko.service.ProductRepairService;
import com.mj.beko.util.DateTimeFormatUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ricardo on 2017/11/22.
 */
@Service
@Transactional
@Slf4j
public class RepairStationApiServiceImpl implements RepairStationApiService {
    /*推送*/
    @Inject
    private SimpMessagingTemplate template;
    @Autowired
    private PalletService palletService;
    @Autowired
    private ProductRepairService productRepairService;
    @Autowired
    private ProductRepairRepository productRepairRepository;
    /**
     * 推送托盘号到返修工位01
     * @param palletNo
     * 工位名称暂时跟系统配置相同,之后统一改掉
     */
    @Override
    public void pushPalletNoToRepairStation01(String palletNo) {
        Map<String,String> map =new HashMap<String,String>();
        map.put("palletNo",palletNo);
        template.convertAndSend("/topic/Repair01/palletNo",map);
    }

    @Override
    public void createOneProductRepair(String palletNo, String reason,String status) {
        Pallet pallet=palletService.findPalletByPalletNo(palletNo);
        //如果不存在下底盘条码。表明该托盘未绑定产品
        if(pallet.getBottomPlaceCode()==null || "".equals(pallet.getBottomPlaceCode())) return;
        //如果没有选择原因，则生成一个空记录
        if("".equals(reason)||reason==null){
            ProductRepair productRepair=new ProductRepair();
            productRepair.setBottomPlaceCode(pallet.getBottomPlaceCode());
            productRepair.setProductNo(pallet.getProductNo());
            productRepair.setState(status);
            productRepair.setStartTime(DateTimeFormatUtil.getCurrentDateTime());
            productRepairService.save(productRepair);
        }else if(!"".equals(reason) && reason!=null){
            //不为空时表示从平板电脑传过来，由人工工位填写的
            ProductRepair productRepair=productRepairService.getProductRepairByBottomBarCode(pallet.getBottomPlaceCode(),status);
            //如果存在该记录,则把填写的原因记录到对应的字段里
            if(productRepair!=null){
                productRepair.setRepairReason(reason);
                productRepairRepository.saveAndFlush(productRepair);
            }else{
                ProductRepair repair=new ProductRepair();
                repair.setBottomPlaceCode(pallet.getBottomPlaceCode());
                repair.setProductNo(pallet.getProductNo());
                repair.setStartTime(DateTimeFormatUtil.getCurrentDateTime());
                repair.setState(status);
                repair.setRepairReason(reason);
                productRepairService.save(repair);
            }
        }

    }
}
