package com.mj.beko.web.rest;

import com.alibaba.fastjson.JSON;
import com.mj.beko.domain.OperatorShift;
import com.mj.beko.domain.OperatorShiftDetail;
import com.mj.beko.repository.ShiftDetailRepository;
import com.mj.beko.repository.ShiftRepository;
import com.mj.beko.service.ShiftService;
import com.mj.beko.util.HeaderUtil;
import com.mj.beko.util.HttpResponseHeader;
import com.mj.beko.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.hibernate.id.IdentifierGenerator.ENTITY_NAME;

/**
 * Created by Ricardo on 2017/12/1.
 */
@RestController
@Slf4j
@RequestMapping("/api")
public class ShiftResource {
    @Autowired
    private ShiftService shiftService;
    @Autowired
    private ShiftRepository shiftRepository;
    @Autowired
    private ShiftDetailRepository shiftDetailRepository;

/*    @PostMapping("/shifts")
    public ResponseEntity<OperatorShift> createPallet(@RequestBody OperatorShift operatorShift) throws URISyntaxException {
        log.info("添加一条托盘记录");
        if (operatorShift.getId() != null) {
            return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new operatorShift cannot already have an ID")).body(null);
        }
        OperatorShift result = shiftService.save(operatorShift);
        return ResponseEntity.created(new URI("/api/shifts/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
                .body(result);
    }*/
    @PostMapping("/shifts")
    public ResponseEntity<OperatorShift> createPallet(HttpServletRequest request) throws URISyntaxException {
        boolean flag=false;
        //request 接收
        Map<String,String[]> map=request.getParameterMap();
        OperatorShift operatorShift=new OperatorShift();
        operatorShift.setName((String)request.getParameter("name"));
        operatorShift.setDescription(request.getParameter("description"));
        operatorShift.setActive(flag=("true".equals(request.getParameter("active")))?true:false);
        String[] operatorShiftDetails =map.get("operatorShiftDetails");
        Set<OperatorShiftDetail> set =new HashSet<OperatorShiftDetail>();
        //遍历
        if(operatorShiftDetails==null ||operatorShiftDetails.length==0){
            operatorShift.setOperatorShiftDetails(null);
        }
        if(operatorShiftDetails!=null &&operatorShiftDetails.length>0){
            for(String str:operatorShiftDetails){
                OperatorShiftDetail operatorShiftDetail =new OperatorShiftDetail();
                Map<String,String> strMap=(Map<String,String>)JSON.parse(str);
                operatorShiftDetail.setName(strMap.get("name"));
                operatorShiftDetail.setContentType(strMap.get("contentType"));
                operatorShiftDetail.setStartTime(strMap.get("startTime"));
                operatorShiftDetail.setEndTime(strMap.get("endTime"));
                operatorShiftDetail.setCountTime(Integer.parseInt(String.valueOf(strMap.get("countTime"))));
                shiftDetailRepository.save(operatorShiftDetail);
                set.add(operatorShiftDetail);
            }
            operatorShift.setOperatorShiftDetails(set);
        }
        //把json字符串转成数组
        OperatorShift result =shiftRepository.saveAndFlush(operatorShift);
        //删除之前的空节点
        shiftDetailRepository.deleteNullShiftDetail();
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, operatorShift.getId().toString()))
                .body(result);
    }
    @GetMapping("/shifts/{id}")
    public ResponseEntity<OperatorShift> getOrder(@PathVariable Long id) {
        log.debug("REST request to get operatorShift : {}", id);
        OperatorShift operatorShift = shiftService.getOperatorShift(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(operatorShift));
    }

    @PutMapping("/shifts")
    public ResponseEntity<OperatorShift> updataPallets(HttpServletRequest request) throws URISyntaxException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        boolean flag=false;
        //request 接收
        Map<String,String[]> map=request.getParameterMap();
        String id=request.getParameter("id");
        OperatorShift operatorShift=shiftService.getOperatorShift(Long.parseLong(id));
        String[] operatorShiftDetails =map.get("operatorShiftDetails");
        Set<OperatorShiftDetail> set =new HashSet<OperatorShiftDetail>();
        //遍历
        if(operatorShiftDetails==null ||operatorShiftDetails.length==0){
            operatorShift.setOperatorShiftDetails(null);
        }
        if(operatorShiftDetails!=null &&operatorShiftDetails.length>0){
            for(String str:operatorShiftDetails){
                OperatorShiftDetail operatorShiftDetail =new OperatorShiftDetail();
                Map<String,String> strMap=(Map<String,String>)JSON.parse(str);
                operatorShiftDetail.setName(strMap.get("name"));
                operatorShiftDetail.setContentType(strMap.get("contentType"));
                operatorShiftDetail.setStartTime(strMap.get("startTime"));
                operatorShiftDetail.setEndTime(strMap.get("endTime"));
                operatorShiftDetail.setCountTime(Integer.parseInt(String.valueOf(strMap.get("countTime"))));
                shiftDetailRepository.save(operatorShiftDetail);
                set.add(operatorShiftDetail);
            }
            operatorShift.setOperatorShiftDetails(set);
        }
        operatorShift.setName((String)request.getParameter("name"));
        operatorShift.setDescription(request.getParameter("description"));
        operatorShift.setActive(flag=(request.getParameter("active").equals("true"))?true:false);
        //把json字符串转成数组
        OperatorShift result =shiftRepository.saveAndFlush(operatorShift);
        //删除之前的空节点
        shiftDetailRepository.deleteNullShiftDetail();
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, operatorShift.getId().toString()))
                .body(result);
    }

    @DeleteMapping("/shifts/{id}")
    public ResponseEntity<Void> deletePallets(@PathVariable Long id){
        shiftService.delete(id);
        shiftDetailRepository.deleteNullShiftDetail();
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
    //获取总记录数(带查询条件)
    @GetMapping("/getAllShiftsByCondition")
    public long getCountShiftByCondition(String name){
        log.info("shift get all count{}",name);
        long result =shiftService.getCountShiftByCondition(name);
        log.info("返回的结果为{}",result);
        return result;
    }
    //带参数的查询分页
    @GetMapping("/shifts")
    public ResponseEntity<List<OperatorShift>> orderByPage(int page, int size, String name){
        log.info("订单带参数的分页查询,{}{}",page,name);
        Page<OperatorShift> palletPage =shiftService.findAllShiftsByPageAndCondition(name,page,size);
        List<OperatorShift> pallets=palletPage.getContent();
        return new ResponseEntity<List<OperatorShift>>(pallets,HttpResponseHeader.getResponseHeader(),HttpStatus.OK);
    }
    @GetMapping("/getDetailByShiftId")
    public ResponseEntity<List<OperatorShiftDetail>> getDetailByShiftId(String id){
        log.info("query shiftDetail by shiftId");
        List<OperatorShiftDetail> list =shiftDetailRepository.getOperatorShiftDetailByShiftId(id);
        return new ResponseEntity<List<OperatorShiftDetail>>(list,HttpResponseHeader.getResponseHeader(),HttpStatus.OK);
    }
    public String getTimeFormat(String currentTime){
        SimpleDateFormat format =new SimpleDateFormat("HH:mm:ss");
        try {
            Date date=format.parse(currentTime);
             return  format.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
