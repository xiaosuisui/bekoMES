package com.mj.beko.web.rest;

import com.mj.beko.domain.DownTimeData;
import com.mj.beko.domain.FailureReasonData;
import com.mj.beko.service.FailureReasonDataService;
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
 * Created by Ricardo on 2017/11/16.
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class FailureReasonDataResource {
    @Autowired
    private FailureReasonDataService failureReasonDataService;
    @PostMapping("/failureReasonData")
    public ResponseEntity<FailureReasonData> createPallet(@RequestBody FailureReasonData failureReasonData) throws URISyntaxException {
        log.info("add a record of failureReasonData");
        if (failureReasonData.getId() != null) {
            return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new failureReasonData cannot already have an ID")).body(null);
        }
        //修改当前的时间为登录时间
        failureReasonData.setCreateTime(DateTimeFormatUtil.getCurrentDateTime());
        FailureReasonData result = failureReasonDataService.save(failureReasonData);
        return ResponseEntity.created(new URI("/api/failureReasonData/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
                .body(result);
    }
    @GetMapping("/failureReasonData/{id}")
    public ResponseEntity<FailureReasonData> getOrder(@PathVariable Long id) {
        log.debug("REST request to get downTimeData : {}", id);
        FailureReasonData failureReasonData = failureReasonDataService.findOneById(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(failureReasonData));
    }
    //获取总记录数(带查询条件)
    @GetMapping("/getAllFailureReasonDataByCondition")
    public long getCountByCondition(String workstation,String barCode){
        log.info("failureReasonData的查询总记录数{}",workstation);
        long result =failureReasonDataService.getAllFailureReasonDataByCondition(workstation,barCode);
        log.info("返回的结果为{}",result);
        return result;
    }
    @GetMapping("/failureReasonData")
    public ResponseEntity<List<FailureReasonData>> orderByPage(int page, int size, String workstation,String barCode){
        log.info("failureReasonData据带参数的分页查询,{}{}",page,workstation);
        Page<FailureReasonData> failureReasonDataPage =failureReasonDataService.findFailureReasonDataByCondition(workstation,barCode,page,size);
        List<FailureReasonData> downTimeDatas=failureReasonDataPage.getContent();
        return new ResponseEntity<List<FailureReasonData>>(downTimeDatas, HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
    }
}
