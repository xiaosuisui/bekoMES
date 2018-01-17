package com.mj.beko.web.rest;

import com.mj.beko.domain.CycleTimeTarget;
import com.mj.beko.domain.DownTimeData;
import com.mj.beko.service.CycleTimeTargetService;
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
public class CycleTimeTargetResource {
    @Autowired
    private CycleTimeTargetService cycleTimeTargetService;
    @PostMapping("/cycleTimeTarget")
    public ResponseEntity<CycleTimeTarget> createPallet(@RequestBody CycleTimeTarget cycleTimeTarget) throws URISyntaxException {
        log.info("add a record of cycleTimeTarget");
        if (cycleTimeTarget.getId() != null) {
            return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new cycleTimeTarget cannot already have an ID")).body(null);
        }
        //修改当前的时间为登录时间
        cycleTimeTarget.setUpdateTime(DateTimeFormatUtil.getCurrentDateTime());
        CycleTimeTarget result = cycleTimeTargetService.save(cycleTimeTarget);
        return ResponseEntity.created(new URI("/api/cycleTimeTarget/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
                .body(result);
    }
    @GetMapping("/cycleTimeTarget/{id}")
    public ResponseEntity<CycleTimeTarget> getCycleTimeTarget(@PathVariable Long id) {
        log.debug("REST request to get cycleTimeTarget : {}", id);
        CycleTimeTarget cycleTimeTarget = cycleTimeTargetService.findOneById(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(cycleTimeTarget));
    }
    @PutMapping("/cycleTimeTarget")
    public ResponseEntity<CycleTimeTarget> updataPallets(@RequestBody CycleTimeTarget cycleTimeTarget) throws URISyntaxException {
        log.debug("REST request to update cycleTimeTarget : {}", cycleTimeTarget);
        if (cycleTimeTarget.getId() == null) {
            return createPallet(cycleTimeTarget);
        }
        cycleTimeTarget.setUpdateTime(DateTimeFormatUtil.getCurrentDateTime());
        CycleTimeTarget result = cycleTimeTargetService.save(cycleTimeTarget);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, cycleTimeTarget.getId().toString()))
                .body(result);
    }
    //获取总记录数(带查询条件)
    @GetMapping("/getAllCycleTimeTargetByCondition")
    public long getCountByCondition(String productNo){
        log.info("cycleTimeTarget的查询总记录数{}",productNo);
        long result =cycleTimeTargetService.getAllCycleTimeTargetByCondition(productNo);
        log.info("返回的结果为{}",result);
        return result;
    }
    @GetMapping("/cycleTimeTarget")
    public ResponseEntity<List<CycleTimeTarget>> orderByPage(int page, int size, String productNo){
        log.info("cycleTimeTarget据带参数的分页查询,{}{}",page,productNo);
        Page<CycleTimeTarget> cycleTimeTargetDataPage =cycleTimeTargetService.findAllCycleTimeTargetCondition(productNo,page,size);
        List<CycleTimeTarget> cycleTimeTargetDatas=cycleTimeTargetDataPage.getContent();
        return new ResponseEntity<List<CycleTimeTarget>>(cycleTimeTargetDatas, HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
    }
    @DeleteMapping("/cycleTimeTarget/{id}")
    public ResponseEntity<Void> deleteCycleTimeTarget(@PathVariable Long id) {
        log.debug("REST request to delete cycleTimeTarget : {}", id);
        cycleTimeTargetService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
