package com.mj.beko.tcs;

import com.mj.beko.domain.TcsOrder;
import com.mj.beko.opcua.OpcUaClientException;
import com.mj.beko.opcua.OpcUaClientTemplate;
import com.mj.beko.service.TcsOrderService;
import com.prosysopc.ua.client.UaClient;
import lombok.extern.slf4j.Slf4j;
import org.opcfoundation.ua.builtintypes.NodeId;
import org.opcfoundation.ua.builtintypes.Variant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jc on 2017/8/3.
 * 用来处理xml解析出来的各种行为对应的操作
 */
@Slf4j
@Service
@Transactional
public class MessageHandler {

    @Autowired
    private SimpMessagingTemplate template;

    @Inject
    private TcsOrderService tcsOrderService;

    @Inject
    private InitTcsOrderService initTcsOrderService;

    @Inject
    private OpcUaClientTemplate opcUaClientTemplate;
    private static final String EPS_DOWN_SENSOR = "\"information\".\"EPS_Down\"";//下滚筒传感器(false 没东西,true 表示有东西)
    private static final String EPS_UP_SENSOR="\"information\".\"EPS_Up\"";//上滚筒传感器(false 没东西,true 表示有东西)

    //处理滚筒线小车第一次停车的情况
    public void handlerCallMaterialFirstCome(String orderName, String vehicle, int count,String locationName) {
        //更改tcsOrder状态
        TcsOrder tcsOrder = updateTcsOrderState(orderName, vehicle,count);
        String node02 = tcsOrder.getNode02(); //plc滚动信号点
  /*      //orderName和状态推送到前端(前端对应的显示当前执行的小车)
        pushtcsOrderChangeMessage(orderName, vehicle, count);*/
        //**********此处待编写 "让PLC的滚筒滚动起来" 的代码********(先给小车滚筒发,然后给PLC发,空托盘入小车)
        firstAgvLastPlc(orderName, node02, locationName, 1);
        //预留出接口供tablet调用
        template.convertAndSend("/topic/screenSupply/tcsOrder",tcsOrder);
    }
    //处理滚筒线小车第二次停车的情况
    public void handlerCallMaterialSecCome(String orderName, String vehicle, int count,String locationName) {
       // updateTcsOrderState(orderName, vehicle);
        TcsOrder tcsOrder = updateTcsOrderState(orderName, vehicle,count);
        /*推送一体机状态更改*/
        firstPlcLastAgv(orderName, "\"information\".\"GO_3\"",locationName, 0);
        template.convertAndSend("/topic/screenSupply/tcsOrder",tcsOrder);
    }
    //处理滚筒线第三次停车的情况
    public void handlerCallMaterialthirdCome(String orderName, String vehicle, int count,String locationName) {
        //更新调拨单状态,把状态2----3,表示小车到位的标识
        TcsOrder tcsOrder = updateTcsOrderState(orderName, vehicle,count);
        template.convertAndSend("/topic/screenSupply/tcsOrder",tcsOrder);
        /*推送一体机状态更改*/
        /*pushtcsOrderChangeMessage(orderName, vehicle, count);*/
        //##############################PLC接口#############################(先让小车发,然后给PLC让滚筒滚动)(物料上料区)
        firstAgvLastPlc(orderName, "\"information\".\"G0_2\"",locationName, 0);
    }

    //处理滚筒线小车第四次停车的情况
    public void handlerCallMaterialFourTime(String orderName, String vehicle, int count,String locationName) {
        //更新调拨单状态,把状态2----3,表示小车到位的标识
       // updateTcsOrderState(orderName, vehicle);
        TcsOrder tcsOrder = updateTcsOrderState(orderName, vehicle,count);
        String node02 = tcsOrder.getNode02(); //plc滚动信号点
        template.convertAndSend("/topic/screenSupply/tcsOrder",tcsOrder);
        /*推送一体机状态更改*/
       /* pushtcsOrderChangeMessage(orderName, vehicle, count);*/
        //##############################PLC接口#############################(先给滚筒发,然后给小车)(物料下料区)
    }

    //处理EPSUP小车第一次到位的情况（表示到上料点,需要通知滚筒线滚动 maybe)
    public void handlerEpsupFirstCome(String orderName, String vehicle, int count){
        TcsOrder tcsOrder =updateTcsOrderState(orderName,vehicle,count);//更改tcsOrder状态
        String node01=tcsOrder.getNode01(); //叫料
        String node02=tcsOrder.getNode02(); //正转
        String node03=tcsOrder.getNode03(); //反转
        TcsOrder tcsOrder1=tcsOrderService.getLastEPpsTypeTcsOrder(tcsOrder.getTcsOrderName());
        //让单滚筒滚动来还是双滚筒滚动通过光电来判断(读取上泡沫和下泡沫的光电)(读取下滚筒)
        boolean epsDownResult=false; //false表示没东西,可以滚动
        boolean epsUpResult=false; //false表示没东西可以滚动
        UaClient uaClient = opcUaClientTemplate.getUaClientList().get(0);
        NodeId epsDownNode = new NodeId(3, EPS_DOWN_SENSOR);
        NodeId epsuPNode = new NodeId(3, EPS_UP_SENSOR);
        try {
            Variant readDownNodeVariant = opcUaClientTemplate.readNodeVariant(uaClient, epsDownNode);
            epsDownResult =readDownNodeVariant.booleanValue();
            Variant readUpNodeVariant = opcUaClientTemplate.readNodeVariant(uaClient, epsuPNode);
            epsUpResult =readUpNodeVariant.booleanValue();
        } catch (OpcUaClientException e) {
            e.printStackTrace();
        }
        //如果两个光电表示没东西,双滚筒滚动
        if(!epsDownResult && !epsUpResult){
            log.info("double scroller......{}",orderName);
            epsDownAndUpRoll();
            //下滚筒没东西,上滚筒有东西
        }else if(!epsDownResult && epsUpResult){
            log.info("epsDownRoller scroller.......{}",orderName);
                epsDownRoll();
            //如果下滚筒有东西,上滚筒没东西，
        }else if(epsDownResult && !epsUpResult){
            log.info("epsUpRoller scroller.......,{}",orderName);
            epsUpRoll();
        }
        //如果查不到除此之外的eps类型的单子，表明是第一个单子(如果是第一个单子,此处应该为空车,则让双滚筒滚动,两边上料)
/*        if(tcsOrder==null){
            //plc双滚筒滚动
            log.info("tcs Order name,{},chabudao last order,shuang guntong scroller",tcsOrder1.getTcsOrderName());
            epsDownAndUpRoll();
        }else{
            //判断上一次的叫料是什么类型
            String type=tcsOrder1.getFunctionType();
            if("EPSDOWN".equals(type)){
                log.info("tcs Order name,{},plc shuang guntong scroller",tcsOrder1.getTcsOrderName());
                //PLC双滚筒滚动
                epsDownAndUpRoll();
            }
            if("EPSUP".equals(type)){
                //PLC滚筒单节点滚动
                log.info("tcs Order name,{},plc dan guntong scroller",tcsOrder1.getTcsOrderName());
                epsUpRoll();
            }
        }*/
        //推送一体机状态
        template.convertAndSend("/topic/epsSupply/tcsOrder",tcsOrder);
        //##########################PLC接口###################################(通知滚筒)
    }

    //处理EPSUP小车第二次到位的情况(只需要推送一体机public------》NOP)
    public void handlerEpsUpSecondCome(String orderName ,String vehicle, int count){
        TcsOrder tcsOrder =updateTcsOrderState(orderName,vehicle,count);//更改tcsOrder状态
        //推送一体机状态
        template.convertAndSend("/topic/epsSupply/tcsOrder",tcsOrder);
    }

    //处理EPSUP小车第三次到位的情况()
    public void handlerEpsUpThirdCome(String orderName,String vehicle, int count){
        TcsOrder tcsOrder =updateTcsOrderState(orderName,vehicle,count);//更改tcsOrder状态
        String node01=tcsOrder.getNode01(); //叫料
        String node02=tcsOrder.getNode02(); //正转
        String node03=tcsOrder.getNode03(); //反转
        //推送一体机
        template.convertAndSend("/topic/epsSupply/tcsOrder",tcsOrder);
    }

    //处理EPSUP小车第四次到位的情况（NOP修改状态不需要执行任何操作）
    public void handlerEpsUpFourthCome(String orderName,String vehicle, int count){
        TcsOrder tcsOrder =updateTcsOrderState(orderName,vehicle,count);//更改tcsOrder状态
        template.convertAndSend("/topic/epsSupply/tcsOrder",tcsOrder);
    }

    //处理EPSDOWN第一次到位的情况
     //（表示到上料点,需要通知滚筒线滚动 maybe）
    public void handlerEpsdownFirstCome(String orderName, String vehicle, int count){
        TcsOrder tcsOrder =updateTcsOrderState(orderName,vehicle,count);//更改tcsOrder状态
        String node01=tcsOrder.getNode01(); //叫料
        String node02=tcsOrder.getNode02(); //正转
        String node03=tcsOrder.getNode03(); //反转
        //判断当前是一次还是二次滚动
        TcsOrder tcsOrder1=tcsOrderService.getLastEPpsTypeTcsOrder(orderName);
        //让单滚筒滚动来还是双滚筒滚动通过光电来判断(读取上泡沫和下泡沫的光电)(读取下滚筒)
        boolean epsDownResult=false; //false表示没东西,可以滚动
        boolean epsUpResult=false; //false表示没东西可以滚动
        UaClient uaClient = opcUaClientTemplate.getUaClientList().get(0);
        NodeId epsDownNode = new NodeId(3, EPS_DOWN_SENSOR);
        NodeId epsuPNode = new NodeId(3, EPS_UP_SENSOR);
        try {
            Variant readDownNodeVariant = opcUaClientTemplate.readNodeVariant(uaClient, epsDownNode);
            epsDownResult =readDownNodeVariant.booleanValue();
            Variant readUpNodeVariant = opcUaClientTemplate.readNodeVariant(uaClient, epsuPNode);
            epsUpResult =readUpNodeVariant.booleanValue();
        } catch (OpcUaClientException e) {
            e.printStackTrace();
        }
        //如果两个光电表示没东西,双滚筒滚动
        if(!epsDownResult && !epsUpResult){
            log.info("double eps down scroller......{}",orderName);
            epsDownAndUpRoll();
            //下滚筒没东西,上滚筒有东西
        }else if(!epsDownResult && epsUpResult){
            log.info("epsDownRoller eps down scroller.......{}",orderName);
            epsDownRoll();
            //如果下滚筒有东西,上滚筒没东西，
        }else if(epsDownResult && !epsUpResult){
            log.info("epsUpRoller epsdown scroller.......,{}",orderName);
            epsUpRoll();
        }

/*        String type=tcsOrder1.getFunctionType();
        //双工位
        if("EPSDOWN".equals(type)){
            //PLC双滚筒滚动
            epsDownAndUpRoll();
        }
        if("EPSUP".equals(type)){
            //PLC单节点滚动
            epsUpRoll();
        }*/
        //推送一体机状态
        template.convertAndSend("/topic/epsSupply/tcsOrder",tcsOrder);
    }

    //处理EPSdown第二次到位的情况
    public void handlerEpsdownSecondCome(String orderName, String vehicle, int count){
        TcsOrder tcsOrder =updateTcsOrderState(orderName,vehicle,count);//更改tcsOrder状态
        //推送一体机状态
        template.convertAndSend("/topic/epsSupply/tcsOrder",tcsOrder);
    }

    //处理EPSdown第三次到位的情况
    public void handlerdEpsownThirdCome(String orderName,String vehicle, int count,String locationName){
        TcsOrder tcsOrder =updateTcsOrderState(orderName,vehicle,count);//更改tcsOrder状态
        String node01=tcsOrder.getNode01(); //叫料
        String node02=tcsOrder.getNode02(); //正转
        String node03=tcsOrder.getNode03(); //反转
        //读取EPSUp节点有没有发出叫料信号，如果有则结束掉wait0等待,如果没有则等待
        try {
            UaClient uaClient = opcUaClientTemplate.getUaClientList().get(2);
            NodeId nodeId = new NodeId(3, "\"ITread\".\"EPS_on_demand\"");
            Variant variant = opcUaClientTemplate.readNodeVariant(uaClient, nodeId);
            int val = variant.intValue();
            if (val == 1) {
                initTcsOrderService.createNextStepTcsOrder(orderName, locationName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //推送一体机
        //##########################PLC接口###################################(
        template.convertAndSend("/topic/epsSupply/tcsOrder",tcsOrder);
    }

    //处理EPSDown小车第四次到位的情况（NOP修改状态不需要执行任何操作）
    public void handlerEpsdownFourthCome(String orderName,String vehicle, int count){
        TcsOrder tcsOrder =updateTcsOrderState(orderName,vehicle,count);//更改tcsOrder状态
        template.convertAndSend("/topic/epsSupply/tcsOrder",tcsOrder);
    }

    //##################################流利架类型的单子#############################################
    //流利架第一次到位（小车到达物料取料点）
    @Async("taskExecutor")
    public void handlerFLiujijiaFirstCome(String orderName, String vehicle, int count, String locationName){
        TcsOrder tcsOrder =updateTcsOrderState(orderName, vehicle, count);//更改tcsOrder状态
        template.convertAndSend("/topic/screenSupply/tcsOrder",tcsOrder);
        UaClient uaClient = opcUaClientTemplate.getUaClientList().get(1);
        NodeId nodeId = new NodeId(3, "\"OPCOA\".\"KnobBox_Full\"");
        int i = 0;
        while (i < 50) {
            try {
                Variant variant = opcUaClientTemplate.readNodeVariant(uaClient, nodeId);
                int val = variant.intValue();
                if (val == 1) {
                    initTcsOrderService.createNextStepTcsOrder(orderName, locationName);
                    break;
                };
                i++;
                Thread.sleep(1000);
            } catch (OpcUaClientException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //流利架第二次到位()--->小车到达物料下料点
    @Async("taskExecutor")
    public void handlerLiulijiaSecondCome(String orderName,String vehicle, int count, String locationName){
        TcsOrder tcsOrder =updateTcsOrderState(orderName,vehicle,count);//更改tcsOrder状态
        template.convertAndSend("/topic/screenSupply/tcsOrder",tcsOrder);
        UaClient uaClient = opcUaClientTemplate.getUaClientList().get(1);
        NodeId nodeId = new NodeId(3, "\"OPCOA\".\"KnobBox_Empty\"");
        int i = 0;
        while (i < 50) {
            try {
                Variant variant = opcUaClientTemplate.readNodeVariant(uaClient, nodeId);
                int val = variant.intValue();
                if (val == 0) {
                    initTcsOrderService.createNextStepTcsOrder(orderName, locationName);
                    break;
                };
                i++;
                Thread.sleep(1000);
            } catch (OpcUaClientException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //流利架第三次到位()--->小车到达空箱子取料点
    public void handlerLiulijiaThirdCome(String orderName,String vehicle, int count){
        TcsOrder tcsOrder =updateTcsOrderState(orderName,vehicle,count);//更改tcsOrder状态
        //推送一体机
        template.convertAndSend("/topic/screenSupply/tcsOrder",tcsOrder);
    }
    //流利架第四次到位(小车把空箱子运到空箱子点)
    public void handlerLiulijiaFourCome(String orderName,String vehicle, int count){
        TcsOrder tcsOrder =updateTcsOrderState(orderName,vehicle,count);//更改tcsOrder状态
        //推送一体机
        template.convertAndSend("/topic/screenSupply/tcsOrder",tcsOrder);
    }
    //处理第一第二工位叫料,第一次到位的情况
    public void  handlerBottomPlateCarFirstCome(String orderName,String vehicle,int count){
        TcsOrder tcsOrder =updateTcsOrderState(orderName,vehicle,count);//更改tcsOrder状态
        template.convertAndSend("/topic/plateSupply/tcsOrder",tcsOrder);
    }
    //处理第一第二工位叫料,第二次到位的情况
    public void handlerBottomPlateSecCome(String orderName,String vehicle,int count){
        TcsOrder tcsOrder =updateTcsOrderState(orderName,vehicle,count);//更改tcsOrder状态
        template.convertAndSend("/topic/plateSupply/tcsOrder",tcsOrder);
    }
    //处理第一第二工位叫料,第三次到位的情况(此处为wait:0,需要员工确认上料完成)
    public void handlerBottomPlateThirdCome(String orderName,String vehicle,int count){
        TcsOrder tcsOrder =updateTcsOrderState(orderName,vehicle,count);//更改tcsOrder状态
        template.convertAndSend("/topic/plateSupply/tcsOrder",tcsOrder);
    }
    //处理第一第二工位叫料,第四次到位的情况
    public void handlerBottomPlateFourthCome(String orderName,String vehicle,int count){
        TcsOrder tcsOrder =updateTcsOrderState(orderName,vehicle,count);//更改tcsOrder状态
        template.convertAndSend("/topic/plateSupply/tcsOrder",tcsOrder);
    }
    //处理第一工位单独叫空车的情况(第一次)
    public void hanlderBottomPlateForEmptyCarFirstCome(String orderName,String vehicle,int count){
        TcsOrder tcsOrder =updateTcsOrderState(orderName,vehicle,count);//更改tcsOrder状态
        template.convertAndSend("/topic/plateSupply/tcsOrder",tcsOrder);
    }
    //处理第一工位单独叫空车的情况(第二次)
    public void hanlderBottomPlateForEmptyCarSecondCome(String orderName,String vehicle,int count){
        TcsOrder tcsOrder =updateTcsOrderState(orderName,vehicle,count);//更改tcsOrder状态
        template.convertAndSend("/topic/plateSupply/tcsOrder",tcsOrder);
    }
    //处理第一工位单独叫空车的情况(第三次)
    public void hanlderBottomPlateForEmptyCarThirdCome(String orderName,String vehicle,int count){
        TcsOrder tcsOrder =updateTcsOrderState(orderName,vehicle,count);//更改tcsOrder状态
        template.convertAndSend("/topic/plateSupply/tcsOrder",tcsOrder);
    }
    //处理第一工位单独叫空车的情况(第四次)
    public void hanlderBottomPlateForEmptyCarFourthCome(String orderName,String vehicle,int count){
        TcsOrder tcsOrder =updateTcsOrderState(orderName,vehicle,count);//更改tcsOrder状态
        template.convertAndSend("/topic/plateSupply/tcsOrder",tcsOrder);
    }
    //处理第一工位单独送料的第一次到位情况
    public void hanlderBottomPlateForMaterialCarFirstCome(String orderName,String vehicle,int count){
        TcsOrder tcsOrder =updateTcsOrderState(orderName,vehicle,count);//更改tcsOrder状态
        template.convertAndSend("/topic/plateSupply/tcsOrder",tcsOrder);
    }
    //处理第一工位单独送料的第二次到位情况
    public void hanlderBottomPlateForMaterialCarSecondCome(String orderName,String vehicle,int count){
        TcsOrder tcsOrder =updateTcsOrderState(orderName,vehicle,count);//更改tcsOrder状态
        template.convertAndSend("/topic/plateSupply/tcsOrder",tcsOrder);
    }
    //处理第一工位单独送料的第三次情况
    public void hanlderBottomPlateForMaterialCarThirdCome(String orderName,String vehicle,int count){
        TcsOrder tcsOrder =updateTcsOrderState(orderName,vehicle,count);//更改tcsOrder状态
        template.convertAndSend("/topic/plateSupply/tcsOrder",tcsOrder);
    }
    //处理第一二工位单独送料的第四次到位情况
    public void hanlderBottomPlateForMaterialCarFourthCome(String orderName,String vehicle,int count){
        TcsOrder tcsOrder =updateTcsOrderState(orderName,vehicle,count);//更改tcsOrder状态
        template.convertAndSend("/topic/plateSupply/tcsOrder",tcsOrder);
    }

    //处理滚筒叫空托盘的第一次到位(wait:0)
    public void handlerScrollerEmptyFirstCome(String orderName,String vehicle,int count,String locationName){
        //更改tcsOrder状态
        TcsOrder tcsOrder = updateTcsOrderState(orderName, vehicle,count);
        String node02 = tcsOrder.getNode02(); //plc滚动信号点（node02位plc滚筒滚动节点）
        //给小车滚筒发,然后给PLC发,空托盘入小车)
        firstAgvLastPlc(orderName, node02, locationName, 1);
        //预留出接口供tablet调用
        template.convertAndSend("/topic/screenSupply/tcsOrder",tcsOrder);
    }
    //处理滚筒线叫空托盘的第二次到位
    public void handlerScrollerEmptySecondCome(String orderName,String vehicle,int count){
        //NOP,更改小车状态,推送到一体机
        TcsOrder tcsOrder = updateTcsOrderState(orderName, vehicle,count);
        template.convertAndSend("/topic/screenSupply/tcsOrder",tcsOrder);
    }
    //处理滚筒线叫空托盘的第三次到位
    public void handlerScrollerThirdCome(String orderName,String vehicle,int count,String locationName){
        TcsOrder tcsOrder = updateTcsOrderState(orderName, vehicle,count);//更新状态
        //先给PLC发,再给小车发
        firstPlcLastAgv(orderName, "\"information\".\"GO_3\"",locationName, 0);
        template.convertAndSend("/topic/screenSupply/tcsOrder",tcsOrder);

    }
    //处理滚筒线叫空托盘的第四次到位
    public void handlerScrollerFourthCome(String orderName,String vehicle,int count){
        //NOP,更改小车状态,推送到一体机
        TcsOrder tcsOrder = updateTcsOrderState(orderName, vehicle,count);
        template.convertAndSend("/topic/screenSupply/tcsOrder",tcsOrder);
    }
    //处理滚筒线单独上物料的第一次到位
    public void handlerMaterialFirstCome(String orderName,String vehicle,int count,String locationName){
        TcsOrder tcsOrder = updateTcsOrderState(orderName, vehicle,count);
        //##############################PLC接口#############################(先让小车发,然后给PLC让滚筒滚动)(物料上料区)
        firstAgvLastPlc(orderName, "\"information\".\"G0_2\"",locationName, 0);
        template.convertAndSend("/topic/screenSupply/tcsOrder",tcsOrder);
    }
    //处理滚筒线单独上物料的第二次到位
    public void handlerMaterialSecondCome(String orderName,String vehicle,int count){
        TcsOrder tcsOrder = updateTcsOrderState(orderName, vehicle,count);
        template.convertAndSend("/topic/screenSupply/tcsOrder",tcsOrder);
    }
    //处理滚筒线单独上物料的第三次到位
    public void handlerMaterialThirdCome(String orderName,String vehicle,int count){
        TcsOrder tcsOrder = updateTcsOrderState(orderName, vehicle,count);
        template.convertAndSend("/topic/screenSupply/tcsOrder",tcsOrder);
    }
    //处理滚筒线单独上物料的第四次到位
    public void handlerMaterialFouthCome(String orderName,String vehicle,int count){
        TcsOrder tcsOrder = updateTcsOrderState(orderName, vehicle,count);
        template.convertAndSend("/topic/screenSupply/tcsOrder",tcsOrder);
    }

    //处理小车运输完成的情况
    public void handleFinishedCome(String orderName) {
        /*更新状态*/
      TcsOrder tcsOrder = updateTcsOrderState(orderName,null, 5);
        /*推送前端更改tcsOrder信息*/
        //查询当前订单的类型
        if("GUNTONG".equals(tcsOrder.getFunctionType())||"LIULIJIA".equals(tcsOrder.getFunctionType())){
            template.convertAndSend("/topic/screenSupply/tcsOrder",tcsOrder);
        }
        if("SCROLLEMPTY".equals(tcsOrder.getFunctionType())||"SCROLLMATERIAL".equals(tcsOrder.getFunctionType())){
            template.convertAndSend("/topic/screenSupply/tcsOrder",tcsOrder);
        }
        if("EPSDOWN".equals(tcsOrder.getFunctionType())||"EPSUP".equals(tcsOrder.getFunctionType())){
            template.convertAndSend("/topic/epsSupply/tcsOrder",tcsOrder);
        }
        if("BOTTOMANDTOPPLATE".equals(tcsOrder.getFunctionType())){//第一第二工位的小车叫料
            template.convertAndSend("/topic/plateSupply/tcsOrder",tcsOrder);
        }
    }
    /**
     * 更新调拨单的状态
     * @param orderName
     * @param vehicle
     */
    public TcsOrder updateTcsOrderState(String orderName, String vehicle, int count) {
        //更新调拨单的状态,把状态有 0---->1-----2, 表示小车到位的标识符号
        //new interface  0 create 1 palletEmpty  2 palletOut  3 materialstart 4 materialend 5 stop car
        List<TcsOrder> tcsOrders = tcsOrderService.findAllByTcsOrderName(orderName);
        /*更新当前订单的状态*/
        if(tcsOrders != null && tcsOrders.size() > 0){
            TcsOrder tcsOrder = tcsOrders.get(0);
            //如果当前状态 下料完成，则更新为完成
            if (OrderState.UNLOADINGSTATUS.equals(tcsOrder.getState())) {
                tcsOrder.setState(OrderState.FIINISHEDSTATUS);
                //如果当前状态为物料上料,则更新为物料下料
            } else if (OrderState.FEEDINGSTATUS.equals(tcsOrder.getState())  && count == 4) {
                tcsOrder.setState(OrderState.UNLOADINGSTATUS);
                //如果当前状态为空托盘下料,则更新为物料上料
            }else if (OrderState.PALLETOUT.equals(tcsOrder.getState())  && count == 3) {
                tcsOrder.setState(OrderState.FEEDINGSTATUS);
                //如果当前状态为到达空托盘区则更新为空托盘下料
            }else if (OrderState.PALLETEMPTY.equals(tcsOrder.getState()) && count == 2) {
                tcsOrder.setState(OrderState.PALLETOUT);
            }
            //如果当前状态为创建订单,则更新为agv到达工位运空托盘
            else if(OrderState.CREATESTATUS.equals(tcsOrder.getState()) && count == 1) {
                tcsOrder.setState(OrderState.PALLETEMPTY);
                tcsOrder.setExecutingVehicleRegex(vehicle);
            }
            tcsOrder = tcsOrderService.saveAndFlush(tcsOrder);
            log.info("修改{}调拨单的状态更新为{}", orderName, tcsOrder.getState());
            return tcsOrder;
        }
        return null;
    }
    //处理小车校验失败的情况
    public void handlerFailureCome() {
    }

    /**
     * 推送生成的tcsOrder信息到一体机
     * @param tcsOrder
     */
    public void pushCreatedTcsOrder(TcsOrder tcsOrder) {
        log.info("推送生成的调拨单{}信息到screen", tcsOrder.getTcsOrderName());
        Map<String,TcsOrder> createdTcsOrder = new HashMap<String,TcsOrder>();
        createdTcsOrder.put("createdTcsOrder", tcsOrder);
        //判断当前生生的调度单类型,推送到不同的一体机中去
        String functionType=tcsOrder.getFunctionType();
        //根据不同的类型推送到不同的一体机上
        if("GUNTONG".equals(functionType)||"LIULIJIA".equals(functionType)){
            template.convertAndSend("/topic/screenSupply/tcsOrder",tcsOrder);
        }
        //eps类型的
        if("EPSDOWN".equals(functionType)||"EPSUP".equals(functionType)){
            template.convertAndSend("/topic/epsSupply/tcsOrder",tcsOrder);
        }
        //第一第二工位的
        if("BOTTOMANDTOPPLATE".equals(functionType)){
            template.convertAndSend("/topic/plateSupply/tcsOrder",tcsOrder);
        }
    }

    /**
     * 先让AGV滚动起来，再让PLC滚动起来
     * @param orderName
     * @param node
     */
    public void firstAgvLastPlc(String orderName, String node, String locationName, int index){
        try {
            initTcsOrderService.createNextStepTcsOrder(orderName,locationName);
            UaClient uaClient = opcUaClientTemplate.getUaClientList().get(index);
            NodeId plcNode = new NodeId(3, node);
            boolean flag = opcUaClientTemplate.writeNodeValue(uaClient, plcNode, 1);
        } catch (IOException e) {
            log.error("MessageHandler ===> firstAgvLastPlc 发送调拨单的操作完成指令失败", e);
            e.printStackTrace();
        } catch (OpcUaClientException e) {
            log.error("MessageHandler ===> firstAgvLastPlc 连接OPCUA失败", e);
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 先让PLC滚动起来，再让AGV滚动起来
     * @param orderName
     * @param node
     */
    public void firstPlcLastAgv(String orderName, String node, String locationName, int index){
        try {
            //**********此处待编写 "让PLC的滚筒滚动起来" 的代码********(到达空托盘下料区,(先给滚筒发,再给小车发,空托盘下料))
            UaClient uaClient = opcUaClientTemplate.getUaClientList().get(index);
            NodeId plcNode = new NodeId(3, node);
            boolean flag = opcUaClientTemplate.writeNodeValue(uaClient, plcNode, 1);
            //发送指令让小车的滚桶滚起来
            initTcsOrderService.createNextStepTcsOrder(orderName,locationName);
        } catch (IOException e) {
            log.error("MessageHandler ===> firstPlcLastAgv 发送调拨单的操作完成指令失败", e);
            e.printStackTrace();
        } catch (OpcUaClientException e) {
            log.error("MessageHandler ===> firstPlcLastAgv 连接OPCUA失败", e);
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void epsDownAndUpRoll(){
        try {
            UaClient uaClient = opcUaClientTemplate.getUaClientList().get(0);
            NodeId upNode = new NodeId(3, "\"information\".\"GO_1\"");
            NodeId downNode = new NodeId(3, "\"information\".\"GO_4\"");
            boolean flag1 = opcUaClientTemplate.writeNodeValue(uaClient, upNode, 1);
            boolean flag2 = opcUaClientTemplate.writeNodeValue(uaClient, downNode, 1);
        } catch (OpcUaClientException e) {
            log.error("MessageHandler ===> epsDownAndUpRoll 连接OPCUA失败", e);
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //上滚筒滚动
    public void epsUpRoll(){
        try {
            UaClient uaClient = opcUaClientTemplate.getUaClientList().get(0);
            NodeId upNode = new NodeId(3, "\"information\".\"GO_1\"");
            boolean flag1 = opcUaClientTemplate.writeNodeValue(uaClient, upNode, 1);
        } catch (OpcUaClientException e) {
            log.error("MessageHandler ===> epsUpRoll 连接OPCUA失败", e);
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //下滚筒滚动
    public void  epsDownRoll(){
        try {
            UaClient uaClient = opcUaClientTemplate.getUaClientList().get(0);
            NodeId upNode = new NodeId(3, "\"information\".\"GO_4\"");
            boolean flag1 = opcUaClientTemplate.writeNodeValue(uaClient, upNode, 1);
        } catch (OpcUaClientException e) {
            log.error("MessageHandler ===> epsDownRoller 连接OPCUA失败", e);
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
