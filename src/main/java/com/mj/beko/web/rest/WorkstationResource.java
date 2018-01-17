package com.mj.beko.web.rest;

import com.mj.beko.domain.Workstation;
import com.mj.beko.service.WorkstationService;
import com.mj.beko.util.HeaderUtil;
import com.mj.beko.util.HttpResponseHeader;
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

/**
 * Created by Ricardo on 2017/8/24.
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class WorkstationResource {

    private static final String ENTITY_NAME = "workstation";

    @Autowired
    private WorkstationService workstationService;

/* //**
     * 分页查询
     * @param pageUtil
     * @return
     *//*
    @GetMapping("/workstations")
    public ResponseEntity<List<Workstation>> queryByPage(PageUtil pageUtil) {
        if (pageUtil.getSize() == 0) {
            return new ResponseEntity<List<Workstation>>(workstationService.query(), HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
        }
        return new ResponseEntity<List<Workstation>>(workstationService.queryByPage(pageUtil.getPage(), pageUtil.getSize()), HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
    }*/

    /**
     * 查询记录数
     * @return
     */
    @GetMapping("/getAllCountWorkstations")
    public ResponseEntity<String> getAllCountWorkstations() {
        return new ResponseEntity<String>(workstationService.getAllCountWorkstaion(), HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
    }

    /**
     * 创建工位
     * @param workstation
     * @return
     * @throws URISyntaxException
     */
    @PostMapping("/workstations")
    public ResponseEntity<Workstation> createWorkstation(@RequestBody Workstation workstation) throws URISyntaxException {
        log.debug("REST request to save Workstation : {}", workstation);
        if (workstation.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new workstation cannot already have an ID")).body(null);
        }
        Workstation result = workstationService.save(workstation);
        return ResponseEntity.created(new URI("/api/workstations/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
                .body(result);
    }

    /**
     * 根据工位ID获取工位信息
     * @param id
     * @return
     */
    @GetMapping("/workstations/{id}")
    public ResponseEntity<Workstation> getWorkstation(@PathVariable Long id) {
        log.debug("REST request to get Workstation : {}", id);
        return new ResponseEntity<Workstation>(workstationService.findOne(id), HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
    }

    /**
     * 更新工位信息
     * @param workstation
     * @return
     * @throws URISyntaxException
     */
    @PutMapping("/workstations")
    public ResponseEntity<Workstation> updateWorkstation(@RequestBody Workstation workstation) throws URISyntaxException {
        log.debug("REST request to update Workstation : {}", workstation);
        if (workstation.getId() == null) {
            return createWorkstation(workstation);
        }
        Optional<Workstation> result = workstationService.updateWorkstation(workstation);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, workstation.getId().toString()))
                .body(result.get());
    }

    /**
     * 根据工位ID删除工位信息
     * @param id
     * @return
     */
    @DeleteMapping("/workstations/{id}")
    public ResponseEntity<Void> deleteWorkstation(@PathVariable Long id) {
        log.debug("REST request to delete Workstation : {}", id);
        workstationService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * 带条件分页查询
     * @param page
     * @param size
     * @param stationId
     * @param stationName
     * @return
     */
    @GetMapping("/workstations")
    public ResponseEntity<List<Workstation>> orderByPage(int page, int size, String stationId, String stationName){
        log.info("工位带参数的分页查询,{}{}", page, stationId);
        Page<Workstation> workstationsPage = workstationService.findAllByPageAndCondition(stationId, stationName, page, size);
        List<Workstation> workstations = workstationsPage.getContent();
        return new ResponseEntity<List<Workstation>>(workstations, HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
    }

    //获取总记录数(带查询条件)
    @GetMapping("/getAllWorkstationByCondition")
    public long getCountByCondition(String stationId, String stationName){
        log.info("工位模块带参数的查询总记录数{}", stationId);
        long result = workstationService.getAllCountByCondition(stationId, stationName);
        log.info("返回的结果为{}", result);
        return result;
    }

    /*
    *查询所有Workstation
    */
    @GetMapping("/allWorkstations")
    public ResponseEntity<List<Workstation>> findAll() {
        return new ResponseEntity<List<Workstation>>(workstationService.findAll(), HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
    }
}
