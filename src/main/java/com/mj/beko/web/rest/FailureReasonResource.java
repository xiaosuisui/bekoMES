package com.mj.beko.web.rest;

import com.mj.beko.domain.CycleTimeTarget;
import com.mj.beko.domain.FailureReason;
import com.mj.beko.service.FailureReasonService;
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
 * Created by Ricardo on 2017/11/14.
 */
@RestController
@Slf4j
@RequestMapping("/api")
public class FailureReasonResource {
    @Autowired
    private FailureReasonService failureReasonService;
    @PostMapping("/failureReason")
    public ResponseEntity<FailureReason> createPallet(@RequestBody FailureReason failureReason) throws URISyntaxException {
        log.info("add a record of failureReason");
        if (failureReason.getId() != null) {
            return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new failureReason cannot already have an ID")).body(null);
        }
        //修改当前的时间为登录时间
        failureReason.setCreateTime(DateTimeFormatUtil.getCurrentDateTime());
        FailureReason result = failureReasonService.save(failureReason);
        return ResponseEntity.created(new URI("/api/failureReason/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
                .body(result);
    }
    @GetMapping("/failureReason/{id}")
    public ResponseEntity<FailureReason> getCycleTimeTarget(@PathVariable Long id) {
        log.debug("REST request to get failureReason : {}", id);
        FailureReason failureReason = failureReasonService.findOneById(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(failureReason));
    }
    @PutMapping("/failureReason")
    public ResponseEntity<FailureReason> updataPallets(@RequestBody FailureReason failureReason) throws URISyntaxException {
        log.debug("REST request to update failureReason : {}", failureReason);
        if (failureReason.getId() == null) {
            return createPallet(failureReason);
        }
        failureReason.setCreateTime(DateTimeFormatUtil.getCurrentDateTime());
        FailureReason result = failureReasonService.save(failureReason);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, failureReason.getId().toString()))
                .body(result);
    }
    //获取总记录数(带查询条件)
    @GetMapping("/getFailureReasonByCondition")
    public long getCountByCondition(String workstation){
        log.info("failurereason的查询总记录数{}",workstation);
        long result =failureReasonService.getAllFailureReasonByCondition(workstation);
        log.info("返回的结果为{}",result);
        return result;
    }
    @GetMapping("/failureReason")
    public ResponseEntity<List<FailureReason>> orderByPage(int page, int size, String workstation){
        log.info("failureReason据带参数的分页查询,{}{}",page,workstation);
        Page<FailureReason> cycleTimeTargetDataPage =failureReasonService.findAllFailureReasonCondition(workstation,page,size);
        List<FailureReason> cycleTimeTargetDatas=cycleTimeTargetDataPage.getContent();
        return new ResponseEntity<List<FailureReason>>(cycleTimeTargetDatas, HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
    }
    @DeleteMapping("/failureReason/{id}")
    public ResponseEntity<Void> deleteCycleTimeTarget(@PathVariable Long id) {
        log.debug("REST request to delete cycleTimeTarget : {}", id);
        failureReasonService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

}
