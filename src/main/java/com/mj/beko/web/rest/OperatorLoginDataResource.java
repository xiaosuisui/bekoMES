package com.mj.beko.web.rest;

import com.mj.beko.domain.OperatorLoginData;
import com.mj.beko.domain.Pallet;
import com.mj.beko.service.OperatorLoginDataService;
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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import static org.hibernate.id.IdentifierGenerator.ENTITY_NAME;
/**
 * Created by Ricardo on 2017/11/6.
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class OperatorLoginDataResource {

    @Autowired
    OperatorLoginDataService operatorLoginDataService;
    /**
     * 保存
     * @param operatorLoginData
     * @return
     * @throws URISyntaxException
     */
    @PostMapping("/operatorLoginData")
    public ResponseEntity<OperatorLoginData> createPallet(@RequestBody OperatorLoginData operatorLoginData) throws URISyntaxException {
        log.info("add a record of operatorLogin");
        if (operatorLoginData.getId() != null) {
            return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new operatorLogin cannot already have an ID")).body(null);
        }
        //修改当前的时间为登录时间
        operatorLoginData.setOperationTime(Timestamp.valueOf(getCurrentTime()));
       /* operatorLoginData.setOperator("testUser");*/
        OperatorLoginData result = operatorLoginDataService.save(operatorLoginData);
        return ResponseEntity.created(new URI("/api/operatorLoginData/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
                .body(result);
    }
    @GetMapping("/operatorLoginData/{id}")
    public ResponseEntity<OperatorLoginData> getOrder(@PathVariable Long id) {
        log.debug("REST request to get operatorLoginData : {}", id);
        OperatorLoginData operatorLoginData = operatorLoginDataService.findOneById(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(operatorLoginData));
    }
    /**
     * 修改
     * @param operatorLoginData
     * @return
     * @throws URISyntaxException
     */
    @PutMapping("/operatorLoginData")
    public ResponseEntity<OperatorLoginData> updataPallets(@RequestBody OperatorLoginData operatorLoginData) throws URISyntaxException {
        log.debug("REST request to update operatorLoginData : {}", operatorLoginData);
        if (operatorLoginData.getId() == null) {
            return createPallet(operatorLoginData);
        }
        OperatorLoginData result = operatorLoginDataService.save(operatorLoginData);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, operatorLoginData.getId().toString()))
                .body(result);
    }
    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping("/operatorLoginData/{id}")
    public ResponseEntity<Void> deletePallets(@PathVariable Long id){
        operatorLoginDataService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
    //获取总记录数(带查询条件)
    @GetMapping("/getAllOperatorLoginByCondition")
    public long getCountByCondition(String operator,String workstation){
        log.info("带员工登录参数的查询总记录数{}",operator);
        long result =operatorLoginDataService.getAllCountByCondition(operator,workstation);
        log.info("返回的结果为{}",result);
        return operatorLoginDataService.getAllCountByCondition(operator,workstation);
    }
    /**
     * 分页带条件查询
     * @param page
     * @param size
     * @param operator
     * @param workstation
     * @return
     */
    @GetMapping("/operatorLoginData")
    public ResponseEntity<List<OperatorLoginData>> orderByPage(int page, int size, String operator, String workstation){
        log.info("操作者数据带参数的分页查询,{}{}",page,operator);
        Page<OperatorLoginData> operatorLoginPage =operatorLoginDataService.findAllByPageAndCondition(operator,workstation,page,size);
        List<OperatorLoginData> operatorLoginDatas=operatorLoginPage.getContent();
        return new ResponseEntity<List<OperatorLoginData>>(operatorLoginDatas, HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
    }
    //获取当前时间
    public String getCurrentTime(){

        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}
