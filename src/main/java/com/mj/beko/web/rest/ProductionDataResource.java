package com.mj.beko.web.rest;

import com.mj.beko.domain.ProductionData;
import com.mj.beko.domain.TestStationData;
import com.mj.beko.repository.ProductionDataRepository;
import com.mj.beko.service.ProductionDataService;
import com.mj.beko.util.DateTimeFormatUtil;
import com.mj.beko.util.HeaderUtil;
import com.mj.beko.util.HttpResponseHeader;
import com.mj.beko.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import static org.hibernate.id.IdentifierGenerator.ENTITY_NAME;

/**
 * Created by Ricardo on 2017/11/11.
 */
@RestController
@Slf4j
@RequestMapping("/api")
public class ProductionDataResource {

    @Autowired
    private  ProductionDataService productionDataService;
    /**
     * 保存
     * @param productionData
     * @return
     * @throws URISyntaxException
     */
    @PostMapping("/production-data")
    public ResponseEntity<ProductionData> createPallet(@RequestBody ProductionData productionData) throws URISyntaxException {
        log.info("add a record of productionData");
        if (productionData.getId() != null) {
            return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new productionData cannot already have an ID")).body(null);
        }
        productionData.setCreateTime(DateTimeFormatUtil.getCurrentDateTime());
        //设置当前记录的时间
        ProductionData result = productionDataService.save(productionData);
        return ResponseEntity.created(new URI("/api/production-data/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
                .body(result);
    }
    @GetMapping("/production-data/{id}")
    public ResponseEntity<ProductionData> getOrder(@PathVariable Long id) {
        log.debug("REST request to get productionData : {}", id);
        ProductionData productionData = productionDataService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(productionData));
    }

    /**
     * 通过条件查询
     * @param page
     * @param size
     * @param productNo
     * @param barCode
     * @return
     */
    @GetMapping("/production-data")
    public ResponseEntity<List<ProductionData>> orderByPage(int page, int size, String productNo, String barCode){
        log.info("生产数据据带参数的分页查询,{}{}",page,barCode);
        Page<ProductionData> productionDataPage =productionDataService.findAllProductionDataByPageAndCondition(productNo,barCode,page,size);
        List<ProductionData> testStationDatas =productionDataPage.getContent();
        return new ResponseEntity<List<ProductionData>>(testStationDatas, HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
    }
    @GetMapping("/getAllProductionDataByCondition")
    public long getCountByCondition(String productNo,String barCode){
        log.info("生产数据参数的查询总记录数{}",barCode);
        long result =productionDataService.getAllCountByCondition(productNo,barCode);
        log.info("返回的结果为{}",result);
        return result;
    }
}
