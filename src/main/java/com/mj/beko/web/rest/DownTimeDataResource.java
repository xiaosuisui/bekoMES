package com.mj.beko.web.rest;
import com.mj.beko.domain.DownTimeData;
import com.mj.beko.service.DownTimeDataService;
import com.mj.beko.util.DateTimeFormatUtil;
import com.mj.beko.util.HeaderUtil;
import com.mj.beko.util.HttpResponseHeader;
import com.mj.beko.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import static org.hibernate.id.IdentifierGenerator.ENTITY_NAME;

/**
 * Created by Ricardo on 2017/11/13.
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class DownTimeDataResource {
    @Autowired
    private DownTimeDataService downTimeDataService;
    /*推送*/
    @Inject
    private SimpMessagingTemplate template;
    /**
     * 保存
     *
     * @param downTimeData
     * @return
     * @throws URISyntaxException
     */
    @PostMapping("/downTimeData")
    public ResponseEntity<DownTimeData> createPallet(@RequestBody DownTimeData downTimeData) throws URISyntaxException {
        log.info("add a record of downTimaData");
        if (downTimeData.getId() != null) {
            return ResponseEntity.badRequest()
                  .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new downTimeData cannot already have an ID")).body(null);
        }
        //修改当前的时间为登录时间
/*        downTimeData.setOperator("testUser");*/
        downTimeData.setCreateTime(DateTimeFormatUtil.getCurrentDateTime());
        DownTimeData newDownTimeData = downTimeDataService.save(downTimeData);
        //推送downTimeData到LineLeader的 screen 和tablet上
        template.convertAndSend("/topic/lineLeaderScreen/downTimeData",newDownTimeData);
        return ResponseEntity.created(new URI("/api/downTimeData/" + newDownTimeData.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, newDownTimeData.getId().toString()))
                .body(newDownTimeData);
    }
    @GetMapping("/downTimeData/{id}")
    public ResponseEntity<DownTimeData> getOrder(@PathVariable Long id) {
        log.debug("REST request to get downTimeData : {}", id);
        DownTimeData downTimeData = downTimeDataService.findOneById(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(downTimeData));
    }

    /**
     * 修改
     * @param downTimeData
     * @return
     * @throws URISyntaxException
     */
    @PutMapping("/downTimeData")
    public ResponseEntity<DownTimeData> updataPallets(@RequestBody DownTimeData downTimeData) throws URISyntaxException {
        log.debug("REST request to update downTimeData : {}", downTimeData);
        if (downTimeData.getId() == null) {
            return createPallet(downTimeData);
        }
        DownTimeData result = downTimeDataService.save(downTimeData);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, downTimeData.getId().toString()))
                .body(result);
    }
    //获取总记录数(带查询条件)
    @GetMapping("/getAllDownTimeDataByCondition")
    public long getCountByCondition(String workstation){
        log.info("downTimeD的查询总记录数{}",workstation);
        long result =downTimeDataService.getAllDownTimeCountByCondition(workstation);
        log.info("返回的结果为{}",result);
        return result;
    }
    @GetMapping("/downTimeData")
    public ResponseEntity<List<DownTimeData>> orderByPage(int page, int size, String workstation){
        log.info("downTime据带参数的分页查询,{}{}",page,workstation);
        Page<DownTimeData> downTimeDataPage =downTimeDataService.findAllByDownTimeCondition(workstation,page,size);
        List<DownTimeData> downTimeDatas=downTimeDataPage.getContent();
        return new ResponseEntity<List<DownTimeData>>(downTimeDatas, HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
    }
}
