package com.mj.beko.web.rest;

import com.mj.beko.domain.OperatorLoginData;
import com.mj.beko.domain.TestStationData;
import com.mj.beko.service.TestStationDataService;
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
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.hibernate.id.IdentifierGenerator.ENTITY_NAME;

/**
 * Created by Ricardo on 2017/11/11.
 */
@RestController
@Slf4j
@RequestMapping("/api")
public class TestStationResource {
    @Autowired
    private TestStationDataService testStationDataService;

    @Autowired
    private SimpMessagingTemplate template;

    /**
     * 保存
     * @param testStationData
     * @return
     * @throws URISyntaxException
     */
    @PostMapping("/testStationData")
    public ResponseEntity<TestStationData> createPallet(@RequestBody TestStationData testStationData) throws URISyntaxException {
        log.info("add a record of testStationData");
        if (testStationData.getId() != null) {
            return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new testStationData cannot already have an ID")).body(null);
        }
        testStationData.setCreateTime(DateTimeFormatUtil.getCurrentDateTime());
        //设置当前记录的时间
        TestStationData result = testStationDataService.save(testStationData);
        //通过不同的contentType推送到不同的界面上
        String contentType= result.getContentType();
        if(contentType!=null){
            template.convertAndSend("/topic/"+contentType+"/testStationData",result);
        }
        return ResponseEntity.created(new URI("/api/testStationData/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
                .body(result);
    }
    @GetMapping("/testStationData/{id}")
    public ResponseEntity<TestStationData> getOrder(@PathVariable Long id) {
        log.debug("REST request to get testStationData : {}", id);
        TestStationData testStationData = testStationDataService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(testStationData));
    }

    /**
     * 通过条件查询
     * @param page
     * @param size
     * @param productNo
     * @param barCode
     * @return
     */
    @GetMapping("/testStationData")
    public ResponseEntity<List<TestStationData>> orderByPage(int page, int size, String productNo, String barCode){
        log.info("测试工位数据据带参数的分页查询,{}{}",page,barCode);
        Page<TestStationData> testStationDataPage =testStationDataService.findAllTestDataByPageAndCondition(productNo,barCode,page,size);
        List<TestStationData> testStationDatas =testStationDataPage.getContent();
        return new ResponseEntity<List<TestStationData>>(testStationDatas, HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
    }
    @GetMapping("/getAllTestStationDataByCondition")
    public long getCountByCondition(String productNo,String barCode){
        log.info("测试工位数据参数的查询总记录数{}",barCode);
        long result =testStationDataService.getAllCountByCondition(productNo,barCode);
        log.info("返回的结果为{}",result);
        return result;
    }


}
