package com.mj.beko.web.rest.screenApi;

import com.mj.beko.domain.*;
import com.mj.beko.opcua.OpcUaClientException;
import com.mj.beko.opcua.OpcUaClientTemplate;
import com.mj.beko.repository.OrderRepository;
import com.mj.beko.repository.ProductBrokenDataRepository;
import com.mj.beko.service.*;
import com.mj.beko.service.ApiService.CurrentShiftService;
import com.mj.beko.service.ApiService.RepairStationApiService;
import com.mj.beko.service.ApiService.ShiftTargetService;
import com.mj.beko.tcs.InitTcsOrderService;
import com.mj.beko.util.DateTimeFormatUtil;
import com.prosysopc.ua.client.UaClient;
import lombok.extern.slf4j.Slf4j;
import org.opcfoundation.ua.builtintypes.NodeId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ricardo on 2017/11/16.
 * tablet push data to server api
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class TabletScreenToServerApi {

    @Autowired
    private FailureReasonDataService failureReasonDataService;
    @Autowired
    private PalletService palletService;
    @Autowired
    private TestStationDataService testStationDataService;
    @Autowired
    private RepairStationApiService repairStationApiService;
    @Inject
    private OpcUaClientTemplate opcUaClientTemplate;
    @Autowired
    private ShiftTargetService shiftTargetService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CurrentShiftService currentShiftService;
    @Autowired
    private InitTcsOrderService initTcsOrderService;
    @Autowired
    private TcsOrderService tcsOrderService;
    @Autowired
    private ProductBrokenDataRepository productBrokenDataRepository;
    @Autowired
    private ProductBrokenDataService productBrokenDataService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/burnStationPushDataToServer")
    public Map<String,String> burnStationPushDataToServer(String reasons,String workstation,String operator,String palletNo,String type) throws OpcUaClientException {
        Map map = new HashMap<String,String>();
        String[] reasonList =reasons.split(",");
        //通过托盘号查找当前正在生产的产品
        Pallet pallet=palletService.findPalletByPalletNo(palletNo);
        if(pallet!=null && pallet.getBottomPlaceCode()!=null && pallet.getProductNo()!=null){
            //通过托盘号查找当前正在生产的产品,把信息跟当前的产品信息绑定
            for(String reason:reasonList){
                FailureReasonData failureReasonData =new FailureReasonData();
                failureReasonData.setOperator(operator);
                failureReasonData.setWorkstation(workstation);
                failureReasonData.setProductNo(pallet.getProductNo());
                failureReasonData.setPoint(type);
                failureReasonData.setBarCode(pallet.getBottomPlaceCode());
                failureReasonData.setCreateTime(DateTimeFormatUtil.getCurrentDateTime());
                failureReasonData.setReason(reason);
                failureReasonData.setStatus("0");
                failureReasonDataService.save(failureReasonData);
            }
            map.put("result","ok");
            pushLeave(workstation);
        }else{
            //如果当前的托盘号并没有绑定产品
            map.put("result","nok");
        }
        return map;
    }

    /**
     * 点击OK按钮，让托盘线体运行
     * @param workstation
     */
    @GetMapping("/okButtonContinue")
    public Map<String,String> okButtonContinue(String workstation){
        Map<String,String> map =new HashMap<String,String>();
        log.info("点击ok按钮,continue gogogog",workstation);
        pushLeave(workstation);
        map.put("result","ok");
        return map;
    }


    public void pushLeave(String workstation){
        //火焰测试控制plc放行
        switch (workstation) {
            case "Knobs":
                writePlcLeave("\"OPCOA\".\"Knob_fangxing\"", 1);
                break;
            case "FireTest1":
                writePlcLeave("\"ITread\".\"ranshao1fangxing\"", 2);
                break;
            case "FireTest2":
                writePlcLeave("\"ITread\".\"ranshao2fangxing\"", 2);
                break;
            default:
                break;
        }
    }

    public void writePlcLeave(String fxNode, int plcNo){
        UaClient uaClient = opcUaClientTemplate.getUaClientList().get(plcNo);
        NodeId nodeId1 = new NodeId(3, fxNode);
        try {
            boolean flag = opcUaClientTemplate.writeNodeValue(uaClient, nodeId1, 1);
        } catch (OpcUaClientException e) {
            e.printStackTrace();
        }
    }
    // first repairStation api
    //通过托盘号,查找该产品失败的原因(如果是返修工位1,则查测试工位,如果是返修工位02,则查knobe和burn 工位)
    @GetMapping("/getFailureReasonByPalletForRepairOne")
    public List<TestStationData> getFilureReasonByPallet(String palletNo){
        //通过托盘查询当前生产的产品和下底盘条码
        log.info("get RepairStation failure Reason,palletNo {}",palletNo);
        Pallet pallet=palletService.findPalletByPalletNo(palletNo);
        if(pallet!=null && pallet.getBottomPlaceCode()!=null && pallet.getProductNo()!=null){
           //查询测试结果表中结果中下底盘条码为barCode且结果为NOK的记录
            List<TestStationData> stationDataList=testStationDataService.getTestNOKDataByBarcode(pallet.getBottomPlaceCode());
            return stationDataList;
        }
        return new ArrayList<TestStationData>();
    }

    //通过托盘号，查找人工点击的（旋钮和火焰测试工位中的失败原因显示在对应的平板上）
    @GetMapping("/getKnobeAndBurnFailureByBarCode")
    public List<FailureReasonData> getKnobeAndBurnFailureByBarCode(String palletNo,String station){
        //通过托盘查询当前生产的产品和下底盘条码
        Pallet pallet=palletService.findPalletByPalletNo(palletNo);
        if(pallet!=null && pallet.getBottomPlaceCode()!=null && pallet.getProductNo()!=null){
            //查询测试结果表中结果中下底盘条码为barCode且结果为NOK的记录(查询状态为0)
            List<FailureReasonData> failureReasonDatas=failureReasonDataService.getFailureReasonByCode(pallet.getBottomPlaceCode());
            //把失败原因状态更新为1;是返修工位的时候更改状态
            if(station.equals("repair02")){
                failureReasonDataService.updateFailureReasonData(failureReasonDatas);
            }
            return failureReasonDatas;
        }
        return new ArrayList<FailureReasonData>();
    }


    /**
     * 通过原因和 palletNo 生成返修记录
     * @param palletNo
     * @param reason
     * @return
     */
    @GetMapping("/createProductRepairByTablet")
    public Map<String,String> createProductRepair(String palletNo,String reason,String status){
        Map<String,String> map=new HashMap<String,String>();
        log.info("create a productRepair record");
        repairStationApiService.createOneProductRepair(palletNo,reason,status);
        map.put("result","ok");
        return map;
    }

    /**
     * 获取每个shift的目标产量
     * @return
     */
    @GetMapping("/getShiftTargetNumber")
    public Map<String,String> getShiftTargetNumber(){
        Map<String,String> map=shiftTargetService.getTargetQuantityAndShiftName();
        return map;
    }
    /**
     *获取电视界面的前6个订单
     * @return
     */
    @GetMapping("/getOrderSixListForTv")
    public List<Order> getOrderSixListForTv(){
        return orderService.getSixOrderListForTvScreen();
    }
    /**
     * 获取电视界面的当前的currentTarget产量
     */
    @GetMapping("/getCurrentShiftTarget")
    public int getCurrentShiftTarget(){
      log.info("getCurrentShiftTarget");
     return currentShiftService.getCurrentShiftNumber();
    }
    /**
     * 第一第二工位叫料,按钮叫料
     * @param name
     * @param functionName
     */
    @GetMapping("/callBottmPlateMaterial")
    public Map<String, String> callBottmPlateMaterialAll(String name,String functionName){
        Map<String,String> map =new HashMap<String,String>();
        try {
            initTcsOrderService.createTcsOrderSet(name,functionName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        map.put("result","ok");
        return map;
    }
    @GetMapping("/createNextStepForAgv")
    public Map<String,String> createNextStepForAgv(String orderName,String locationName){
        Map<String,String> map =new HashMap<String,String>();
        try {
            initTcsOrderService.createNextStepTcsOrder(orderName,locationName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        map.put("result","ok");
        return map;
    }
    /**
     * 通过托盘号判断当前的产品结果是否为OK
     * @param palletNo
     * @return
     */
    @GetMapping("/getFinalProductResult")
    public Map<String,String> getProductResult(String palletNo){
        Map<String,String> map =new HashMap<String,String>();
        //通过托盘号获取当前正在执行的托盘
        Pallet pallet=palletService.findPalletByPalletNo(palletNo);
        //查询testStationData 中电测试的结果的数据为Ok
       List<TestStationData> eletricResultData=testStationDataService.getElectricResultMarkByPalletNo(palletNo);
       //如果能查到电测试的数据,则表示一段OK
       if(eletricResultData!=null && eletricResultData.size()>0){
            List<FailureReasonData> failureReasonData=failureReasonDataService.getKnobsAndBurnResultMarkByPalletNo(palletNo,"burning");
            if(failureReasonData!=null && failureReasonData.size()>0){
                map.put("result","OK");
                map.put("styleRemark","0");
            }else{
                map.put("result","NOK");
                map.put("styleRemark","1");
            }
       }else{
           map.put("result","NOK");
           map.put("styleRemark","1");
       }
        //查询 failureReasonData中火焰检测结果数据为OK
        return map;
    }
    /**
     * 生成一条检测结果为OK的记录在数据库中,传一个下底盘条码
     * step:electric ,leakage,flow
     * @return
     */
    @GetMapping("/saveProductResultMark")
    public Map<String,String> saveProductResultMark(String bottomPalteBarCode,String step){
        Map<String,String> map =new HashMap<String,String>();
        //合格后生成一条电测试结果合格OK的记录
        TestStationData testStationData =new TestStationData();
        testStationData.setBarCode(bottomPalteBarCode);
        testStationData.setStep(step);
        testStationData.setContentType("result");
        testStationData.setValue("OK");
        TestStationData stationData=testStationDataService.save(testStationData);
        map.put("result",stationData.getBarCode());
        return map;
    }
    /**
     *保存旋钮工位的检测合格
     * @param palletNo
     * @return
     */
    @GetMapping("/getKnobsProductResultMark")
    public Map<String,String> saveKnobsResultMark(String palletNo){
        Map<String,String> map =new HashMap<String,String>();
        Pallet pallet =palletService.findPalletByPalletNo(palletNo);
        FailureReasonData failureReasonData =new FailureReasonData();
        //保存下底盘条码
        failureReasonData.setBarCode(pallet.getBottomPlaceCode());
        failureReasonData.setStatus("OK");
        failureReasonData.setPoint("knobs");
        failureReasonData.setProductNo(pallet.getProductNo());
        FailureReasonData reasonData=failureReasonDataService.save(failureReasonData);
        map.put("result",reasonData.getBarCode());
        return map;
    }
    /**
     * 生成一条火焰检测OK的记录
     * @param palletNo
     * @return
     */
    @GetMapping("/getBurningProductResultMark")
    public Map<String,String> saveBurningResultMark(String palletNo){
        Map<String,String> map=new HashMap<String,String>();
        Pallet pallet =palletService.findPalletByPalletNo(palletNo);
        FailureReasonData failureReasonData =new FailureReasonData();
        //保存下底盘条码
        failureReasonData.setBarCode(pallet.getBottomPlaceCode());
        failureReasonData.setStatus("OK");
        failureReasonData.setPoint("burning");
        failureReasonData.setProductNo(pallet.getProductNo());
        FailureReasonData reasonData=failureReasonDataService.save(failureReasonData);
        map.put("result",reasonData.getBarCode());
        return map;
    }
    //最后一个工位检测test工位的数据是否为OK or NOK
    @RequestMapping("/getFinalProductOkOrNok")
    public Map<String,String> getFinalProductOkOrNok(String palletNo){
        //定义设置结果为true和false的标识符
        boolean flag=false;
        Map<String,String> map =new HashMap<String,String>();
        //查测试工位的结果是否为Ok(leakage,flow,electric)
        Pallet pallet=palletService.findPalletByPalletNo(palletNo);
        //获取下底盘条码
        String bottomPlateBarCode=pallet.getBottomPlaceCode();
        //如果查不到下底盘条码，表明是空托盘,一路放行
        if(bottomPlateBarCode==null||"".equals(bottomPlateBarCode)||bottomPlateBarCode.isEmpty()){
            log.info("empty pallet can go。。。。。。");
            flag=true;
            map.put("result","OK");
            return map;
        }
        List<TestStationData> testStationDataForLeakageList=testStationDataService.getDiffTestStationResultMark(bottomPlateBarCode,"leakage");
        //leakage test OK 的标识如果查不到
        if(testStationDataForLeakageList==null||testStationDataForLeakageList.size()==0){
            map.put("leakage","NOK");
            flag=true;
        }
        //flow test
        List<TestStationData> testStationDataForFlowList=testStationDataService.getDiffTestStationResultMark(bottomPlateBarCode,"flow");
        if(testStationDataForFlowList==null||testStationDataForFlowList.size()==0){
            map.put("flow","NOK");
            flag=true;
        }
        //electric
        List<TestStationData> testStationDataForElectricList=testStationDataService.getDiffTestStationResultMark(bottomPlateBarCode,"electric");
        if(testStationDataForElectricList==null||testStationDataForElectricList.size()==0){
            map.put("electric","NOK");
            flag=true;
        }
        //knobs station
        List<FailureReasonData> failureReasonDataForKnobs=failureReasonDataService.getKnobsAndBurnResultMarkByPalletNo(palletNo,"knobs");
        //如果查不到,则查状态为0的是否还有失败的记录
        if(failureReasonDataForKnobs==null||failureReasonDataForKnobs.size()==0){
            List<FailureReasonData> failureReasonDataForKnobsList=failureReasonDataService.getKnobsAndBurnTestResult(palletNo,"Knobs");
            //如果查到,表示有失败的原因,则记录
            if(failureReasonDataForKnobsList!=null && failureReasonDataForKnobsList.size()>0){
                map.put("knobs","NOK");
                flag=true;
            }
        }
        //burnTest Station
        List<FailureReasonData> failureReasonDataForBurning=failureReasonDataService.getKnobsAndBurnResultMarkByPalletNo(palletNo,"burning");
        if(failureReasonDataForBurning==null||failureReasonDataForBurning.size()==0){
            List<FailureReasonData> failureReasonDataForBurnList=failureReasonDataService.getKnobsAndBurnTestResult(palletNo,"FireTest1");
            //如果查到表示有失败原因
            if(failureReasonDataForBurnList!=null && failureReasonDataForBurnList.size()>0){
                map.put("fireTest","NOK");
                flag=true;
            }
            List<FailureReasonData> failureReasonDataForBurn1List=failureReasonDataService.getKnobsAndBurnTestResult(palletNo,"FireTest2");
            //如果查到表示有失败原因
            if(failureReasonDataForBurn1List!=null && failureReasonDataForBurn1List.size()>0){
                map.put("fireTest","NOK");
                flag=true;
            }
        }
        //screw test
        // no screw
        if(flag){
            map.put("result","NOK");
        }else if(!flag){
            map.put("result","OK");
        }
        return map;
    }
    //first and second station  make sure material to car
    @RequestMapping("/setMaterialHavePutToCar")
    public Map<String,String> setMaterialHavePutToCar(){
        Map<String,String> map =new HashMap<>();
        List<TcsOrder> bottomAndTopPlateList=tcsOrderService.getLatestTcsOrderForBottomAndTopPlate();
        //如果查不到状态为3的记录
        if(bottomAndTopPlateList==null || bottomAndTopPlateList.size()==0){
           map.put("result","NOK");
           //如果能查到且状态为3
        }else if(bottomAndTopPlateList!=null && bottomAndTopPlateList.size()==1){
            TcsOrder tcsOrder =bottomAndTopPlateList.get(0);
            try {
                initTcsOrderService.createNextStepTcsOrder(tcsOrder.getTcsOrderName(),"Location-0035");
                map.put("result","OK");
            } catch (IOException e) {
                e.printStackTrace();
            }
            //存在多个记录的情况下
        }else if(bottomAndTopPlateList!=null && bottomAndTopPlateList.size()>1){
            TcsOrder tcsOrder =bottomAndTopPlateList.get(0);
            try {
                initTcsOrderService.createNextStepTcsOrder(tcsOrder.getTcsOrderName(),"Location-0035");
            } catch (IOException e) {
                e.printStackTrace();
            }
            //消除掉其他的多余的状态为3的记录(该记录可能在异常时发生,此处清掉)
            for(int i=0;i<bottomAndTopPlateList.size();i++){
                TcsOrder tcsOrder1 =bottomAndTopPlateList.get(i);
                tcsOrder1.setState("6");
                tcsOrderService.saveAndFlush(tcsOrder1);
            }
            map.put("result","OK");
        }
        return map;
    }
    @RequestMapping("/pullOutProduct")
    public Map<String,String> pullOutProduct(String palletNo,String stationName){
        Map<String,String> map =new HashMap<String,String>();
       //通过托盘号查找当前产品对应的产品信息
        Pallet pallet=palletService.findPalletByPalletNo(palletNo);
        if(pallet==null){
            map.put("result","NOK");
            map.put("reason","invalid palletNo");
        }else if(pallet!=null){
            //如果托盘信息存在,但是托盘没有绑定产品
            if(pallet.getProductNo()==null){
                map.put("result","NOK");
                map.put("reason","no product info");
            //如果能拿到产品信息,则在数据库中生成broken记录
            }else if(pallet.getProductNo()!=null && pallet.getBottomPlaceCode()!=null){
                    ProductBrokenData productBrokenData=productBrokenDataService.getProductBrokenDataByBottomPlateCode(pallet.getBottomPlaceCode());
                    //如果查不到该产品信息.则可以创建
                    if(productBrokenData==null){
                            ProductBrokenData productBrokenData1 =new ProductBrokenData();
                            productBrokenData1.setOrderNo(pallet.getCurrentOrderNo());
                            productBrokenData1.setProductId(pallet.getProductNo());
                            productBrokenData1.setBottomPlateBarCode(pallet.getBottomPlaceCode());
                            productBrokenData1.setCreateTime(Timestamp.from(Instant.now()));
                            productBrokenData1.setStationName(stationName);
                            productBrokenDataRepository.save(productBrokenData1);
                            map.put("result","OK");
                            //记录坏件数量,如果是返修工位1的,记录到1 的字段
                            if("Repair01_tablet".equals(stationName)){
                                //获取当前的订单号,坏件数量+1
                                Order order=orderService.getOneOrderByOrderNo(pallet.getCurrentOrderNo());
                                order.setBrokenNumber(order.getBrokenNumber()+1);
                                orderRepository.save(order);
                            }
                            //如果是返修工位02的，记录到2的字段
                            if("Repair02_tablet".equals(stationName)){
                                Order order=orderService.getOneOrderByOrderNo(pallet.getCurrentOrderNo());
                                order.setRepair02Broken(order.getRepair02Broken()+1);
                                orderRepository.save(order);
                            }
                     //如果查到该信息
                    }else if(productBrokenData!=null){
                        map.put("result","NOK");
                        map.put("reason","system already have this product info");
                    }
            }
        }
        redisTemplate.delete(palletNo);
        palletService.clearPalletData(palletNo);
        return map;
    }
}
