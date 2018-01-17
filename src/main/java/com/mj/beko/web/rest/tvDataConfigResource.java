package com.mj.beko.web.rest;

import com.mj.beko.domain.CycleTimeTarget;
import com.mj.beko.domain.TvDataConfig;
import com.mj.beko.service.CycleTimeTargetService;
import com.mj.beko.service.TvDataConfigService;
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
public class tvDataConfigResource {
    @Autowired
    private TvDataConfigService tvDataConfigService;
    @PostMapping("/tvDataConfig")
    public ResponseEntity<TvDataConfig> createPallet(@RequestBody TvDataConfig tvDataConfig) throws URISyntaxException {
        log.info("add a record of tvDataConfig");
        if (tvDataConfig.getId() != null) {
            return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new tvDataConfig cannot already have an ID")).body(null);
        }
        //修改当前的时间为登录时间
        tvDataConfig.setCreateDate(DateTimeFormatUtil.getCurrentDateTime());
        TvDataConfig result = tvDataConfigService.save(tvDataConfig);
        return ResponseEntity.created(new URI("/api/tvDataConfig/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
                .body(result);
    }
    @GetMapping("/tvDataConfig/{id}")
    public ResponseEntity<TvDataConfig> getCycleTimeTarget(@PathVariable Long id) {
        log.debug("REST request to get tvDataConfig : {}", id);
        TvDataConfig tvDataConfig = tvDataConfigService.findOneById(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(tvDataConfig));
    }
    @PutMapping("/tvDataConfig")
    public ResponseEntity<TvDataConfig> updataPallets(@RequestBody TvDataConfig tvDataConfig) throws URISyntaxException {
        log.debug("REST request to update cycleTimeTarget : {}", tvDataConfig);
        if (tvDataConfig.getId() == null) {
            return createPallet(tvDataConfig);
        }
        tvDataConfig.setCreateDate(DateTimeFormatUtil.getCurrentDateTime());
        TvDataConfig result = tvDataConfigService.save(tvDataConfig);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, tvDataConfig.getId().toString()))
                .body(result);
    }
    //获取总记录数(带查询条件)
    @GetMapping("/getAlltvDataConfigByCondition")
    public long getCountByCondition(String tvName){
        log.info("cycleTimeTarget的查询总记录数{}",tvName);
        long result =tvDataConfigService.getAllTvDataConfigByCondition(tvName);
        log.info("返回的结果为{}",result);
        return result;
    }
    @GetMapping("/tvDataConfig")
    public ResponseEntity<List<TvDataConfig>> orderByPage(int page, int size, String tvName){
        log.info("cycleTimeTarget据带参数的分页查询,{}{}",page,tvName);
        Page<TvDataConfig> tvDataConfigs =tvDataConfigService.findAllTvDataConfigCondition(tvName,page,size);
        List<TvDataConfig> cycleTimeTargetDatas=tvDataConfigs.getContent();
        return new ResponseEntity<List<TvDataConfig>>(cycleTimeTargetDatas, HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
    }
    @DeleteMapping("/tvDataConfig/{id}")
    public ResponseEntity<Void> deleteCycleTimeTarget(@PathVariable Long id) {
        log.debug("REST request to delete tvDataConfig : {}", id);
        tvDataConfigService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
