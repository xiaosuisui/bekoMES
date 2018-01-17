package com.mj.beko.web.rest.screenApi;

import com.mj.beko.codeScanner.MinaTcpSickReader;
import com.mj.beko.codeScanner.MinaTcpSickReaderForPacking01;
import com.mj.beko.codeScanner.MinaTcpSickReaderForPacking02;
import com.mj.beko.codeScanner.MinaTcpSickReaderForPacking03;
import com.mj.beko.domain.OperatorShift;
import com.mj.beko.domain.Pallet;
import com.mj.beko.domain.ProductCode;
import com.mj.beko.domain.dto.PrintLabelDto;
import com.mj.beko.httpclient.HttpTemplate;
import com.mj.beko.opcua.OpcUaClientException;
import com.mj.beko.opcua.OpcUaClientTemplate;
import com.mj.beko.opcualistener.third.*;
import com.mj.beko.repository.ProductCodeRepository;
import com.mj.beko.schedule.DynamicScheduledTask1;
import com.mj.beko.schedule.DynamicScheduledTask2;
import com.mj.beko.schedule.ScheduleGetProductPlanAndOperation;
import com.mj.beko.service.ApiService.ShiftTargetService;
import com.mj.beko.service.PalletService;
import com.mj.beko.service.ProductCodeService;
import com.mj.beko.service.ShiftService;
import com.mj.beko.tcs.InitTcsOrderService;
import com.prosysopc.ua.client.UaClient;
import lombok.extern.slf4j.Slf4j;
import org.opcfoundation.ua.builtintypes.NodeId;
import org.opcfoundation.ua.builtintypes.Variant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.inject.Inject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Ricardo on 2017/11/9.
 * data test api
 */
@RestController
@Slf4j
@RequestMapping("/api")
public class ScheduleCronResource {
    /*推送*/
    @Inject
    private SimpMessagingTemplate template;

    @Autowired
    private DynamicScheduledTask2 dynamicScheduledTask2;
    @Autowired
    private DynamicScheduledTask1 dynamicScheduledTask1;
    @Autowired
    private ShiftTargetService shiftTargetService;
    @Autowired
    private ShiftService shiftService;
    @Autowired
    private ProductCodeRepository productCodeRepository;
    @Autowired
    private ProductCodeService productCodeService;
    @Inject
    private TaskExecutor taskExecutor;

    @Inject
    private InitTcsOrderService initTcsOrderService;
    @Inject
    private OpcUaClientTemplate opcUaClientTemplate;
    @Autowired
    private PalletService palletService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Inject
    private ScheduleGetProductPlanAndOperation scheduleGetProductPlanAndOperation;
    @Autowired
    private MinaTcpSickReaderForPacking02 minaTcpSickReaderForPacking02;
    @Autowired
    private MinaTcpSickReaderForPacking03 minaTcpSickReaderForPacking03;
    @Autowired
    PackagingLabelMatchListener packagingLabelMatchListener;

    @Inject
    private HttpTemplate httpTemplate;


    private static final String PRINTLABEL = "/GasAutomationApi/api/Product/PrintProductLabel";

    @Inject
    private FireAndVisionAndRemoveGasLeftListener fireAndVisionAndRemoveGasLeftListener;
    @Inject
    private UpEpsPutDownFinishedListener printer2Listener;
    @Inject
    private DocumentArriveListener printer3Listener;
    @Inject
    private LastPrinterStartListener printer4Listener;
    private static final String EMPTY_POINT = "\"ITread\".\"baxian_empty\"";

    @GetMapping("/updateTask1")
    public void updateTest1(String cron){
        dynamicScheduledTask1.setCron(cron);
    }
    @GetMapping("/updateTask2")
    public void updateTes2(String cron){
        dynamicScheduledTask2.setCron(cron);
    }
    /*推送扫描成功的显示框*/
    @GetMapping("/pushBarCode")
    public void pushBarcode(){
        Map<String,String> barCodeMap =new HashMap<String,String>();
        barCodeMap.put("barcode","123456");
        barCodeMap.put("result","ok");
        template.convertAndSend("/topic/station01/barcodeMatch",barCodeMap);
    }
    @GetMapping("/pushOperation")
    public void pushOperation(){
        Map<String,String> map =new HashMap<String,String>();
        map.put("result","ok");
        template.convertAndSend("/topic/station01/operation",map);
    }
    @GetMapping("/pushDataToTest01")
    public void pushDataToTest01(String barCode,String result){
        Map<String,String> map =new HashMap<String,String>();
        map.put("barCode",barCode);
        map.put("productNo","1001");
        map.put("step","step01");
        map.put("contentType","LEAK01");
        map.put("value","1.00");
        map.put("result",result);
        template.convertAndSend("/topic/LeakageTest1/testStationData",map);
    }
    @GetMapping("/pushDataToTest02")
        public void pushDataToTest02(String barCode,String result){
        Map<String,String> map =new HashMap<String,String>();
        map.put("barCode",barCode);
        map.put("productNo","1001");
        map.put("step","step01");
        map.put("contentType","LEAK01");
        map.put("value","1.00");
        map.put("result",result);
        template.convertAndSend("/topic/LeakageTest2/testStationData",map);
    }
    @GetMapping("/pushDataToTest03")
    public void pushDataToTest03(String barCode,String result){
        Map<String,String> map =new HashMap<String,String>();
        map.put("barCode",barCode);
        map.put("productNo","1001");
        map.put("step","step01");
        map.put("contentType","flow01");
        map.put("value","1.00");
        map.put("result",result);
        template.convertAndSend("/topic/FlowTest1/testStationData",map);
    }
    @GetMapping("/pushDataToTest04")
    public void pushDataToTest04(String barCode,String result){
        Map<String,String> map =new HashMap<String,String>();
        map.put("barCode",barCode);
        map.put("productNo","1001");
        map.put("step","step01");
        map.put("contentType","flow02");
        map.put("value","1.00");
        map.put("result",result);
        template.convertAndSend("/topic/FlowTest2/testStationData",map);
    }
    @GetMapping("/pushDataToTest05")
    public void pushDataToTest05(String barCode,String result){
        Map<String,String> map =new HashMap<String,String>();
        map.put("barCode",barCode);
        map.put("productNo","1001");
        map.put("step","step01");
        map.put("contentType","electric");
        map.put("value","1.00");
        map.put("result",result);
        template.convertAndSend("/topic/ElectricTest/testStationData",map);
    }
    @GetMapping("/pushRepair")
    public void pushRepair(String palletNo){
        Map<String,String> map =new HashMap<String,String>();
        map.put("palletNo",palletNo);
        template.convertAndSend("/topic/repair/palletNo",map);
    }
    @GetMapping("/pushRepair01")
    public void pushRepair02(String palletNo){
        Map<String,String> map =new HashMap<String,String>();
        map.put("palletNo",palletNo);
        template.convertAndSend("/topic/repair01/palletNo",map);
    }
    @GetMapping("pushStopDownTime")
    public void pushRepair08(String message){
        Map<String,String> map =new HashMap<String,String>();
        map.put("palletNo",message);
        template.convertAndSend("/topic/BottomPlate/countAndAverageTime",map);
    }

    /**
     * 调拨单的参数,跟数据库中的一致,测试调拨单的发送接口
     * @param name
     * @param functionName（调拨单的类型）
     * @return
     * @throws IOException
     */
    @RequestMapping("/tscOrderTemplate")
    public String getTcsOrderTemplate(@RequestParam("name") String name, @RequestParam("functionName")String functionName) throws IOException {
        initTcsOrderService.createTcsOrderSet(name, functionName);
        return "OK";
    }
    @RequestMapping("/plcEpsGunDong")
    public String plcEpsGunDong() throws OpcUaClientException {
        UaClient uaClient = opcUaClientTemplate.getUaClientList().get(0);
        NodeId plcNode = new NodeId(3, "\"information\".\"G0_2\"");
        boolean flag = opcUaClientTemplate.writeNodeValue(uaClient, plcNode, 1);
        return "ok";
    }
    @RequestMapping("/getShiftTarget")
    public Map<String, String> getShiftTarget(){
        return shiftTargetService.getTargetQuantityAndShiftName();
    }

    @RequestMapping("/testPrint")
    public String testPrint(){
        try {
            //第一台打印机
            PrintLabelDto printLabelDto = new PrintLabelDto();
            printLabelDto.setProductNo("7783270115");
            printLabelDto.setLine("110");
            printLabelDto.setTagType(4);
            printLabelDto.setQuantity(1);
            printLabelDto.setPrinter(1);
            printLabelDto.setSerial("");
            printLabelDto.setOrder("12345678");
            ResponseEntity<String> responseEntity = httpTemplate.postForEntity(httpTemplate.getBekoApiHttpSchemeHierarchical()
                    + PRINTLABEL, printLabelDto, String.class);
            String serial = responseEntity.getBody().replace("\"", "");
            System.out.println("----------" + serial);
            printLabelDto.setSerial(serial);
            //第二台打印机
            printLabelDto.setTagType(3);
            httpTemplate.postForEntity(httpTemplate.getBekoApiHttpSchemeHierarchical() + PRINTLABEL, printLabelDto, String.class);
            //第三台打印机
            printLabelDto.setTagType(1);
            httpTemplate.postForEntity(httpTemplate.getBekoApiHttpSchemeHierarchical() + PRINTLABEL, printLabelDto, String.class);
            //第四台打印机
            printLabelDto.setPrinter(2);
            httpTemplate.postForEntity(httpTemplate.getBekoApiHttpSchemeHierarchical() + PRINTLABEL, printLabelDto, String.class);
            return serial;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("=========调用打印机API错误==========");
            return null;
        }
    }

    @RequestMapping("/getStandOutputTimeRangeTemplate")
    public void getStandOutputTimeRangeTemplate(){
        template.convertAndSend("/topic/tvScreen01/startCurrentTarget","eveningShift start");
    }
    @RequestMapping("/getOneProductFinished")
    public String getOneProductFinished(){
        template.convertAndSend("/topic/tvScreen01/getProductedNumber","finished");
        return "OK";
    }
    @RequestMapping("/endCurrentShift")
    public String endCurrentShift(){
        template.convertAndSend("/topic/tvScreen01/endCurrentTarget","endFinishCurrent");
        return "OK";
    }
    //获取当前的系统时间
    public String timeFormat() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    @RequestMapping("/getCurrentShiftByTest")
    public Map<String,String> getCurrentShift(){
        OperatorShift shift =shiftService.getCurrentShift();
       return  shiftTargetService.getCurrentShift(shift);
    }
    @RequestMapping("/testSavePro")
    public ProductCode testSaveProductCode(){
        ProductCode productCode=new ProductCode();
        productCode.setEpsCode("epsCode");
        productCode.setOrderNo("10001");
        productCode.setSerialNo("10002");
        productCode.setStatus("1");
        return productCodeService.save(productCode);
    }
    @RequestMapping("/testStationNumber")
    public void testStationNumber(String palletNo){
        //通过托盘号查找当前的绑定的产品信息
        Pallet pallet=palletService.findPalletByPalletNo(palletNo);
        List list =new ArrayList();
        Map<String,String> map=new HashMap<String,String>();
        map.put("orderNo","1001");
        map.put("number",String.valueOf(100));
        list.add(map);
        Map<String,String> map1=new HashMap<String,String>();
        map1.put("orderNo","1002");
        map1.put("number",String.valueOf(100));
        list.add(map1);
        ValueOperations value=redisTemplate.opsForValue();
        value.set("station",list);
    }
    @RequestMapping("/testGetRedisValue")
    public void testGetRedisValue(){
        List list=(List)redisTemplate.opsForValue().get("station002");
        for(int i=0;i<list.size();i++){
            Map map=(Map)list.get(0);
            String orderNo=(String)map.get("orderNo");
            String number=(String)map.get("number");
            log.info("orderNo::::"+orderNo);
            log.info("number::::"+number);
        }
    }
    @RequestMapping("/pushPalletNoToOperatorStation")
    public void pushPalletNoToStation(String palletNo){
        template.convertAndSend("/topic/VisionControl/palletNo",palletNo);
    }
    @RequestMapping("/pushPalletNoToRepair02")
    public void pushPalletNoToRepair02(String palletNo){
        template.convertAndSend("/topic/Repair02/palletNo",palletNo);
    }
    @RequestMapping("/pushPalletNoToKnobsStation")
    public void pushPalletNoToKnobsStation(String palletNo){
        template.convertAndSend("/topic/Knobs/palletNo",palletNo);
    }
    @RequestMapping("/pushPalletNoToAnyStation")
    public void pushPalletNoToKnobsStation(String station,String palletNo){
        template.convertAndSend("/topic/"+station+"/palletNo",palletNo);
    }
    @RequestMapping("/pushPrinterToScreen")
    public void pushPrinterToScreen(){
        template.convertAndSend("/topic/lineLeaderScreen/printApiError","123,456,789");
    }


    @RequestMapping("/testPrintApi")
    public void testPrintApi(int printNo) {
        switch (printNo) {
            case 1:
                fireAndVisionAndRemoveGasLeftListener.printer1("", "", "123456789");
                break;
            case 2:
                printer2Listener.doThing();
                break;
            case 3:
                printer3Listener.doThing();
                break;
            case 4:
                printer4Listener.doThing();
                break;
            default:
                break;
        }
    }
    @RequestMapping("/getProductPlan")
    public void getProductPlan(){
        scheduleGetProductPlanAndOperation.getProductPlan();
    }
    @RequestMapping("/readEmptyPoint")
    public boolean getEmptyPoint(){
        //0 有产品,1表示空托盘 （0 false 1 true 空托盘）
        boolean result=false;
        UaClient uaClient = opcUaClientTemplate.getUaClientList().get(2);
        NodeId emptyPoint = new NodeId(3, EMPTY_POINT);
        try {
            Variant readNodeVariant = opcUaClientTemplate.readNodeVariant(uaClient, emptyPoint);
           result= readNodeVariant.booleanValue();
             log.info("get Result",result);
        } catch (OpcUaClientException e) {
            e.printStackTrace();
        }
        return result;
    }
    @RequestMapping("/setRedisValue")
    public String redisTestsetValue(){
        ValueOperations operations=redisTemplate.opsForValue();
        String resultValue=operations.get("matchBottomPlateCode").toString();
        log.info("resultValue,is ",resultValue);
        return resultValue;
    }
    @RequestMapping("/getRedisCacheList")
    public List getRedisList(){
        ListOperations listOperations =redisTemplate.opsForList();
        List<String> cacheBottomValue=listOperations.range("cacheBottomValue",0,listOperations.size("cacheBottomValue"));
        return cacheBottomValue;
    }
    @RequestMapping("/getSickReaderInfo")
    public void getSickReaderInfo(){
        MinaTcpSickReader minaTcpSickReader =new MinaTcpSickReader();
        minaTcpSickReader.connect();
        try {
            minaTcpSickReader.startsick();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @RequestMapping("/testMatchError")
    public void testError(){
        Map<String,String>map =new HashMap<String,String>();
        map.put("result","matchError");
        map.put("type","6");
        map.put("reason","match error error");
        template.convertAndSend("/topic/lineLeaderScreen/matchError",map);
    }
    @RequestMapping("/testPrinterError")
    public void testErrorPrinter(){
        Map<String,String>map =new HashMap<String,String>();
        map.put("result","matchError");
        map.put("type","6");
        map.put("reason","printer error error");
        template.convertAndSend("/topic/lineLeaderScreen/printerError",map);
    }
    @RequestMapping("/productCodeAndSerialInfo")
    public void productCodeAndSerialInfo(){
        Map<String,String>map =new HashMap<String,String>();
        map.put("currentSerialNumber","currentSerialNumber");
        map.put("bottomPlateCode","bottomPlateCode");
        template.convertAndSend("/topic/lineLeaderScreen/productCodeAndSerialInfo",map);
    }
    @RequestMapping("/readSerialNumber")
    public void readSerialNumber(){
        Map<String,String>map =new HashMap<String,String>();
        map.put("readSerialNumber","readSerialNumber");
        template.convertAndSend("/topic/lineLeaderScreen/readSerialNumber",map);
    }
    @RequestMapping("/matchBottomPlateCode")
    public void matchBottomPlateCode(){
        Map<String,String>map =new HashMap<String,String>();
        map.put("matchBottomPlateCode","matchBottomPlateCode");
        template.convertAndSend("/topic/lineLeaderScreen/matchBottomPlateCode",map);
    }
    @RequestMapping("/printer04ScannerWork")
    public void printer04ScannerWork(){
        minaTcpSickReaderForPacking02.connect();
        try {
            minaTcpSickReaderForPacking02.startsick();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @RequestMapping("/getResisCachePrinterLabel")
    public Map<String,String> getResisCachePrinterLabel(){
        Map<String,String> map =new HashMap<String,String>();
        ValueOperations valueOperations=redisTemplate.opsForValue();
        String cachePackage02Label=valueOperations.get("cachePackage02Label").toString();
        String package02MatchResult=valueOperations.get("package02MatchResult").toString();
        map.put("cachePackage02Label",cachePackage02Label);
        map.put("package02MatchResult",package02MatchResult);
       return map;
    }
    //最后一个扫eps条码工作
    @RequestMapping("/setScannerEps003Working")
    public void setScannerEps003Working(){
        taskExecutor.execute(() -> packagingLabelMatchListener.readPackagePrinterLabel());
    }
}
