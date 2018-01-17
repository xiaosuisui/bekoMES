package com.mj.beko.web.rest.screenApi;

import com.mj.beko.domain.DownTimeData;
import com.mj.beko.domain.Order;
import com.mj.beko.domain.OrderUpdateLog;
import com.mj.beko.domain.TcsOrder;
import com.mj.beko.opcua.OpcUaClientException;
import com.mj.beko.opcua.OpcUaClientTemplate;
import com.mj.beko.service.ApiService.CountStandOutputService;
import com.mj.beko.service.ApiService.CurrentShiftService;
import com.mj.beko.service.ApiService.TcsOrderApiService;
import com.mj.beko.service.DownTimeDataService;
import com.mj.beko.service.OrderService;
import com.mj.beko.service.OrderUpdateLogService;
import com.mj.beko.tcs.InitTcsOrderService;
import com.mj.beko.util.DateTimeFormatUtil;
import com.mj.beko.util.HttpResponseHeader;
import com.prosysopc.ua.client.UaClient;
import lombok.extern.slf4j.Slf4j;
import org.opcfoundation.ua.builtintypes.NodeId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ricardo on 2017/11/10.
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class MessageToScreenResourceApi {
    /*推送*/
    @Inject
    private SimpMessagingTemplate template;
    @Inject
    private OrderUpdateLogService orderUpdateLogService;
    @Autowired
    private DownTimeDataService downTimeDataService;
    @Inject
    private OpcUaClientTemplate opcUaClientTemplate;
    @Inject
    private TcsOrderApiService tcsOrderApiService;
    @Inject
    private InitTcsOrderService initTcsOrderService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CountStandOutputService countStandOutputService;
    @Autowired
    private CurrentShiftService currentShiftService;
    /**
     * 接收到一体机的超时时间，然后推送该消息到对应的tablet上,推送的通道
     * @param workstation
     * @return
     */
    @GetMapping("/pushDownTimeScreen")
    public String pushDownTimeScreen(String workstation,String standardTime,String nowTime){
        //通过工位来匹配对应的平板电脑的工位
        String tabletStation=getScreenAndTablet().get(workstation);
        Map<String,String> map =new HashMap<String,String>();
        map.put("msg","popDownTime");
        //推送的记录保存到日志中去(超时推送日志)
        OrderUpdateLog orderUpdateLog=new OrderUpdateLog();
        orderUpdateLog.setModuleName("timeoutPush");
        orderUpdateLog.setOperatorType(workstation+" timePopPush");
        orderUpdateLog.setOperatorValue("stand:"+standardTime +",newTime"+nowTime);
        orderUpdateLog.setOperatorTime(DateTimeFormatUtil.getCurrentDateTime());
        orderUpdateLogService.save(orderUpdateLog);
        template.convertAndSend("/topic/"+tabletStation+"/pushDownTimeScreen",map);
        return "0";
    }
    //建立一体机跟平板电脑之间的关联关系
    public Map<String,String> getScreenAndTablet(){
        Map<String,String> map =new HashMap<String,String>();
        map.put("BottomPlate","BottomPlate_tablet");
        map.put("TopPlate","TopPlate_tablet");
        map.put("Repair01","Repair01_tablet");
        map.put("Knobs","Knobs_tablet");
        map.put("FireTest1","FireTest1_tablet");
        map.put("FireTest2","FireTest2_tablet");
        map.put("Repair02","Repair02_tablet");
        map.put("VisionControl","VisionControl_tablet");
        map.put("RemoveElectricAndGas","RemoveElectricAndGas_tablet");
        return map;
    }
    @GetMapping("/afterWorkToLogout")
    public Map<String,String> afterWorkToLogout(String workstation){
        String screenWorkstation=workstation.split("_")[0];
        log.info("推送到"+screenWorkstation+"工位下班信息");
        Map<String,String> map = new HashMap<String,String>();
        map.put("workstation",screenWorkstation);
        template.convertAndSend("/topic/"+screenWorkstation+"/logout",map);
        Map<String,String> resultMap = new HashMap<String,String>();
        resultMap.put("result", "ok");
        return resultMap;
    }
    @GetMapping("/getDownTimeDataFour")
    public ResponseEntity<List<DownTimeData>> getDownTimeDataFour(){
        log.info("获取最新的4条记录");
        List<DownTimeData> downTimeDataList =downTimeDataService.getDownTimeTopFour();
        return new ResponseEntity<List<DownTimeData>>(downTimeDataList, HttpResponseHeader.getResponseHeader(), HttpStatus.OK);
    }
   // 1 返修 2 放行 第一个人工工位的控制点
    @GetMapping("/visionLeftOrRepair")
    public void visionLeftOrRepair(int action){
        UaClient uaClient = opcUaClientTemplate.getUaClientList().get(2);
        NodeId nodeId = new NodeId(3, "\"ITread\".\"zhongjianfangxing\"");
        try {
            boolean flag = opcUaClientTemplate.writeNodeValue(uaClient, nodeId, action);
        } catch (OpcUaClientException e) {
            e.printStackTrace();
        }
    }
    /**
     * 获取最新的10条 tcsOrder
     * @return
     */
    @GetMapping("/getTopTenTcsOrder")
    public ResponseEntity<List<TcsOrder>> getTopTenTcsOrder(){
        //根据不同的物料区查询不同的类型的调度单(EPS,流利架,物料车)
        List<TcsOrder> tcsOrders=tcsOrderApiService.getTopTenRecord();
        return new ResponseEntity<List<TcsOrder>>(tcsOrders,HttpResponseHeader.getResponseHeader(),HttpStatus.OK);
    }
    /**
     * 获取最新的10条 EPS tcsOrder
     * @return
     */
    @GetMapping("/getTopTenRecordForEps")
    public ResponseEntity<List<TcsOrder>> getTopTenRecordForEps(){
        //根据不同的物料区查询不同的类型的调度单(EPS,流利架,物料车)
        List<TcsOrder> tcsOrders=tcsOrderApiService.getTopTenRecordForEps();
        return new ResponseEntity<List<TcsOrder>>(tcsOrders,HttpResponseHeader.getResponseHeader(),HttpStatus.OK);
    }
    /**
     * 获取最新的10条 PLATE tcsOrder
     * @return
     */
    @GetMapping("/getTopTenRecordForPlate")
    public ResponseEntity<List<TcsOrder>> getTopTenRecordForSupport(){
        //根据不同的物料区查询不同的类型的调度单(EPS,流利架,物料车)
        List<TcsOrder> tcsOrders=tcsOrderApiService.getTopTenRecordForSupport();
        return new ResponseEntity<List<TcsOrder>>(tcsOrders,HttpResponseHeader.getResponseHeader(),HttpStatus.OK);
    }
    //生成小车的人工参与调度api
    @GetMapping("/createScrollerTcsOrderByOperator")
    public String getTcsOrderTemplate(@RequestParam("name") String name, @RequestParam("functionName")String functionName) throws IOException {
        initTcsOrderService.createTcsOrderSet(name, functionName);
        return "OK";
    }
    //获取当前工单和下一工单,前端界面登录成功后调用
    @GetMapping("/getCurrentOrderByScreen")
    public Order getCurrentOrder(){
        Map<String,Order> ordersMap =orderService.getCurrentOrderAndNextOrder();
        return  ordersMap.get("currentOrder");
    }
    @GetMapping("/getNextOrderByScreen")
    public Order getNextOrder(){
        Map<String,Order> ordersMap =orderService.getCurrentOrderAndNextOrder();
        return  ordersMap.get("nextOrder");
    }
    /**
     * 开始当前shift的电视的界面更新
     * @param shiftName
     */
    @GetMapping("/startCurrenShiftTvScreen")
    public String startCurrentShiftTvScreen(String shiftName){
        template.convertAndSend("/topic/tvScreen01/startCurrentTarget",shiftName);
        return shiftName+" start TVscreen";
    }
    /**
     * 获取TvScreen01报表的输出时间范围
     * @param shiftName
     * @return
     */
    @RequestMapping("/getStandOutputTimeRange")
    public Object  getStandOutputTimeRange(String shiftName){
        return countStandOutputService.getCountStandOutputTimeRange(shiftName);
    }
    /**
     * 判断logout是否可以结束shift
     * @param shiftName
     * @param date
     * @return
     */
    @RequestMapping("/getResultIfShiftCanEnd")
    public String getResultIfShiftCanEnd(String shiftName,String date){
        String result= currentShiftService.getResultIfShiftCanEnd(shiftName,date);
        if("ok".equals(result)){
            template.convertAndSend("/topic/tvScreen01/endCurrentTarget",result);
        }
        return result;
    }
}
