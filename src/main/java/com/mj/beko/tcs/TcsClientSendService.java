package com.mj.beko.tcs;

import com.mj.beko.domain.*;
import com.mj.beko.repository.ConsumedPartsRepository;
import com.mj.beko.repository.OrderRepository;
import com.mj.beko.service.AgvCarNumberForStationService;
import com.mj.beko.service.OrderService;
import com.mj.beko.service.PalletTypeAndCapilityService;
import com.mj.beko.service.TcsOrderService;
import io.swagger.models.auth.In;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.tcp.TcpOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by jc on 2017/8/1.
 */
@Service
@Transactional
public class TcsClientSendService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static int connectCount = 1;

    @Inject
    private TcpOperations<String> tcsClient;

    @Inject
    private TcsOrderService tcsOrderService;

    @Inject
    private MessageHandler messageHandler;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ConsumedPartsRepository consumedPartsRepository;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private AgvCarNumberForStationService agvCarNumberForStationService;
    @Autowired
    private PalletTypeAndCapilityService palletTypeAndCapilityService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private SimpMessagingTemplate template;
    private String productOrder="";
    /**
     * @param xml 生成的调拨单 xml
     * @param tscOrderCreateTemplate 用来获取调拨单的参数,并保存到数据库中
     * @throws IOException
     */
    public void sendTcsOrderSet(String xml, TscOrderCreateTemplate tscOrderCreateTemplate) throws IOException {
        tcsClient.connect(new TcpConnectionHandlerBuilder<String>()
            .onConnect(c -> {
                log.debug("TCS Connected Successed, send message: " + xml);
                connectCount = 1;
                c.send(MessageBuilder.withPayload(xml + "\r\n\r\n").build());
            })
            .onMessage(m -> {
                log.debug("Receive TCS message: " + m.getPayload());
                String xmlStr = m.getPayload();
                /*解析出type订单编号*/
                String tcsOrderType = StrPatternUtil.matchStr(xmlStr, Pattern.compile("xsi:type=\"(.*?)\""));
                //判断接收的是调拨单的响应
                if ("transportResponse".equals(tcsOrderType)) {
                    /*解析出订单编号*/
                    String orderName = StrPatternUtil.matchStr(xmlStr, Pattern.compile("orderName=\"(.*?)\""));
                    if(orderName==null||"".equals(orderName)) return;
                    String materialName ="";
                    String stationName=tscOrderCreateTemplate.getStation();
                    String name=tscOrderCreateTemplate.getName();
/*                   if("BurnerSupport1".equals(stationName)&& ("BS1Left".equals(name)||"BS1Right".equals(name))){
                        *//*materialName=dealWithMaterialgIfChange(stationName,name);*//*
                       getNewModelChangeMaterial(stationName,name);
                        if(materialName==null) return;
                    }
                    if("BurnerSupport2".equals(stationName)&& ("BS2Left".equals(name)||"BS2Right".equals(name))){
                        getNewModelChangeMaterial(stationName,name);
                        if(materialName==null) return;;
                    }
                    if("BurnerSupport3".equals(stationName)&& ("BS3Left".equals(name)||"BS3Right".equals(name))){
                        getNewModelChangeMaterial(stationName,name);
                        if(materialName==null) return;
                    }
//                    //如果当前是滚筒线的第四工位叫料
                    if("BurnerSupport4".equals(stationName)&& ("BS4Left".equals(name)||"BS4Right".equals(name))){
                        getNewModelChangeMaterial(stationName,name);
                        if(materialName==null) return;
                   }
//                    //如果当前是滚筒线的第五工位叫料
                    if("BurnerCap1".equals(stationName)&& ("BC1Left".equals(name)||"BC1Right".equals(name))){
                        getNewModelChangeMaterial(stationName,name);
                        if(materialName==null) return;
                    }
//                    //如果当前是滚筒线的第六工位叫料
                    if("BurnerCap2".equals(stationName)&& ("BC2Left".equals(name)||"BC2Right".equals(name))){
                        getNewModelChangeMaterial(stationName,name);
                        if(materialName==null) return;
                    }*/
                    /*把生成的调度单保存到tcsOrder中，tcsOrder中的字段待完善,推送*/
                    TcsOrder tcsOrder = new TcsOrder();
                    tcsOrder.setTcsOrderName(orderName);
                    tcsOrder.setStationNo(tscOrderCreateTemplate.getStation());
                    tcsOrder.setStartTime(Timestamp.from(Instant.now()));//接收tcsOrder的开始时间
                    log.info(Timestamp.from(Instant.now()).toString());
                    tcsOrder.setFunctionType(tscOrderCreateTemplate.getFunctionName()); //EPS--->滚筒---->流利架
                    //截取掉物料名称的物料名称
                    if (!"".equals(materialName)){
                        tcsOrder.setConsumePartName(materialName.toString().substring(0,materialName.toString().length()-1));
                    }
                    tcsOrder.setStartPoint(tscOrderCreateTemplate.getMaterialStartPoint());
                    tcsOrder.setEndPoint(tscOrderCreateTemplate.getMaterialOutPoint());
                    tcsOrder.setNode01(tscOrderCreateTemplate.getNode01()); //该调度单对应的电气的节点
                    tcsOrder.setNode02(tscOrderCreateTemplate.getNode02());//该调度但对应的电气的节点
                    tcsOrder.setNode02(tscOrderCreateTemplate.getNode02());//该调度但对应的电气的节点
                    tcsOrder.setConsumePartQuantity("6");
                    tcsOrder.setState(OrderState.CREATESTATUS);
                    tcsOrderService.save(tcsOrder);
                    /*把调度单的信息推送到一体机中*/
                    messageHandler.pushCreatedTcsOrder(tcsOrder);
                }
            })
            .onConnectFailure(c -> {
                log.debug("ConnectFailure TCS第" + connectCount + "次连接失败::::::" + c.getMessage());
                //连接失败重新发一遍，最多重发5次
                try {
                    if (connectCount <= 5) sendTcsOrderSet(xml, tscOrderCreateTemplate);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            })
            .onFailure(c -> log.debug("Failure TCS发送消息失败:::::::"))
            .onClosed(() -> log.debug("Closed TCS关闭连接:::::::::"))
            .build()
        );
    }
    //判断物料是否需要切换的逻辑(假象他们的订单都是小订单,L零散类型的,恶心类型的)
    public String getNewModelChangeMaterial(String stationName,String name){
        StringBuffer material =new StringBuffer();
        //读取缓存中的当前工位的当前工单的完成数量{station:[{orderNo:number:0},{orderNo,number}]}(缓存每个工位的完成数量)
        List<Map<String,String>> mapList=readRedisDateForCurrentStation(stationName);
        log.info("start createTcsOrder ,judge station material if need to change");
        //查询当前正在生产的订单,如果查不到则不进行叫料处理
        Map<String,Order> currentOrderAndNextOrder=orderService.getCurrentOrderAndNextOrder();
        String palletCapilityNumber=palletTypeAndCapilityService.getPalletTypeAndCapilitiesByWorkStation(stationName).getCapility();
        if(currentOrderAndNextOrder.get("currentOrder")==null){
            template.convertAndSend("/topic/lineLeaderScreen/noWorkOrderNow",stationName);
            //空表示系统异常,没有订单信息
            return null;
        }
        //判断缓存中的结果,此刻还未初始化,表明当前的订单还未生产(恶心的逻辑就是要判断当前的订单数量能够满足几车)
        //如果当前系统中有订单。但是还没有开始生产,突然叫料了。则就查询当前需要生产的订单,这应该是程序的入口
        if(mapList==null||mapList.size()==0){
            //判断当前的订单计划的数量
            HashOperations value=redisTemplate.opsForHash();
            Order currentOrder=currentOrderAndNextOrder.get("currentOrder");
            //获取当前工位运送的该订单的小车的数量
            String carNumber=(String)value.get(currentOrder.getOrderNo(),stationName);
            //表明当前工位是当前工单的第一车,也算是程序的入口
            if(carNumber==null){
                //查询当前订单当前工位的物料名称
                material=getMaterialNameForProductNoAndStation(currentOrder.getProductNo(),stationName);
                productOrder=currentOrder.getOrderNo();
                //把当前的工单小车的数量置为1
                value.put(currentOrder.getOrderNo(),stationName,"1");
                log.info("agvagvagv,carryMaterial for {},{},number is 1",currentOrder.getOrderNo(),stationName);
                //生成一条数据库记录
            }
            //如果能读到当前工单的工位的物料的配送记录(,正常逻辑当前工位的第二车)
            if(carNumber!=null){
                //查询当前工单的要生产的数量，看看是不是恶心的小订单类型
                int quantity=currentOrder.getQuantity();
                //初始化小车数
                int carNum=Integer.valueOf(carNumber);
                //读取当前工位的标准的生产数量
                int palletCap =Integer.valueOf(palletCapilityNumber);
                //表明要生产的数量大于小车数量,继续送当前订单的物料(要生产的物料大于小车的标准托盘数量,第二车继续送该物料)
                if(quantity-carNum*palletCap>0){
                    material=getMaterialNameForProductNoAndStation(currentOrder.getProductNo(),stationName);
                    productOrder=currentOrder.getOrderNo();
                    value.put(currentOrder.getOrderNo(),stationName,String.valueOf(carNum+1));
                    log.info("agvagvagav,carry material for{},{},{}",currentOrder.getOrderNo(),stationName,String.valueOf(carNum+1));
                 //如果要生产的数量只能满足一车子,就要切换单子(恶心的小单子)判断当前的一车子能够满足几个单子生产(如果要生产的数量<托盘的标准数量)
                }else if(quantity<=carNum*palletCap){
                    //表明要送下一个订单的物料了。如果下一个订单不存在,则提示，此刻可以查下一个订单
                    Order nextOrder=currentOrderAndNextOrder.get("nextOrder");
                    //如果下一个订单不存在.则返回，不在生成调度单,如果不存在下一个订单,则直接return掉
                    if(nextOrder==null){
                        template.convertAndSend("/topic/lineLeaderScreen/noNextOrderInfo",currentOrder.getOrderNo());
                        return null;
                        //开始的第二车如果存在下一个订单,
                    }else if(nextOrder!=null){
                        //恶心的小订单类型,判断是否要生成下一个订单的调度,查询下一个订单的类型是否跟当前订单的物料类型匹配，连续多少个订单可以匹配
                        Map<String,String> nextOrderMap=orderService.getSameMaterialQuantityAndNextOrderNo(currentOrder,currentOrder.getQuantity(),Integer.valueOf(palletCapilityNumber),stationName);
                        //判断跟当前订单相同的物料的以下的订单(以下相同的物料的单子,生产同类型的物料的单子的数量有多少个)
                        String number=nextOrderMap.get("totalNumber");
                        //如果要生产的数量小于托盘的容量-当前订单剩下的生产量.那就送下一个单子吧(下一个单子可能是下一个订单也可能是下下下个订单)
                        if(Integer.valueOf(number)<=(Integer.valueOf(palletCapilityNumber)-currentOrder.getQuantity())){
                            String  nextOrderNo =nextOrderMap.get("orderNo");
                            material=getMaterialNameForProductNoAndStation(nextOrderNo,stationName);
                            productOrder=nextOrderNo;
                            //设置到缓存中去
                            setRedisCarCarryReord(nextOrderNo,stationName);
                            //如果剩下的数量,大于托盘的数量
                        }else if(Integer.valueOf(number)>(Integer.valueOf(palletCapilityNumber)-currentOrder.getQuantity())){
                            //表示被切割的类型(同一个类型的单子的物料)
                            String nowOrder =nextOrderMap.get("nowOrder");
                            material=getMaterialNameForProductNoAndStation(nowOrder,stationName);
                            //下一个订单
                            productOrder=nowOrder;
                            setRedisCarCarryReord(nowOrder,stationName);
                        }
                        template.convertAndSend("/topic/lineLeaderScreen/nextOrderStartCarry",nextOrder.getOrderNo()+nextOrder.getProductNo());
                    }
                }
            }
        }
        //如果当前有已经完成的数量,表示已经开始babababab的生产了.
        else if(mapList.size()>0){
            //查看当前的订单编号和已经完成数量
            int size=mapList.size()-1;
            String redisOrderNo=mapList.get(size).get("orderNo");
            log.info("mapList===1,redisOrderNo is {}",redisOrderNo);
            //完成数量
            String number=mapList.get(0).get("number");//获取当前工单当前工位已经生产的数量(20)
            //通过订单号查询需要生产的数量
            int quantity=orderService.getOneOrderByOrderNo(redisOrderNo).getQuantity();
            //如果当前完成的数量大于等于要生产的数量(很巧合的当前托盘生产完成后,满足了订单数量,刚好叫料),恰好下一个单子没出来station:{"orderNo":1,"number":1}
            //待订单已完全上线后,状态会更改为5,所以如果当前订单完成,只有一个记录时表示下一个要生产的就是当前工单
            //叫料时订单恰好完成.只有一个订单的信息缓存
            if(Integer.valueOf(number)>=quantity){
                //如果系统中只有一个单子的缓存,叫料时恰好完成了,查下一个产品类型是否跟现在一致.(订单剩余数量为0)
                Map<String,String> nextOrderMap=orderService.getSameMaterialQuantityAndNextOrderNo(orderService.getOneOrderByOrderNo(redisOrderNo),0,Integer.valueOf(palletCapilityNumber),stationName);
                String totalNumber=nextOrderMap.get("totalNumber");//获取是否是一致的类型
                //表明此处下一个订单类型切换了。跟当前订单类型的单子没有了，看下一个订单类型是啥样子滴。
                if(Integer.valueOf(totalNumber)==0){
                    //就要判断下一个订单的类型是否能够满足当前订单的生产(如果不存在下一个订单,则停止叫料)
                    if(!nextOrderMap.containsKey("orderNo")){
                        return null;
                    }else{
                        String  nextStepOrderNo =nextOrderMap.get("orderNo");
                        Order nextOrder =orderService.getOneOrderByOrderNo(nextStepOrderNo);
                        Map<String,String> secNextOrderMap=orderService.getSameMaterialQuantityAndNextOrderNo(nextOrder,nextOrder.getQuantity(),Integer.valueOf(palletCapilityNumber),stationName);
                        String secNextOrderTotalNumber=secNextOrderMap.get("totalNumber");//相同的数量
                        //查询跟订单nextOrder相同类型的单子
                        //表明下一个订单类型一个车子足够生产了,向下查询
                        if(Integer.valueOf(secNextOrderTotalNumber)<=Integer.valueOf(palletCapilityNumber)-nextOrder.getQuantity()){
                            //执行查找该类型结束后的第一个订单,查不到下个订单，表明订单结束。
                            if(!secNextOrderMap.containsKey("orderNo")){
                                return null;
                            }else{
                                String order=secNextOrderMap.get("orderNo");
                                material=getMaterialNameForProductNoAndStation(orderService.getOneOrderByOrderNo(order).getProductNo(),stationName);
                                setRedisCarCarryReord(order,stationName);
                            }
                            //如果剩下的同类型的产品大于一个托盘的标准的生产量,则记录运输该类型的产品
                        }else if(Integer.valueOf(secNextOrderTotalNumber)>Integer.valueOf(palletCapilityNumber)-nextOrder.getQuantity()){
                            String nowOrder =secNextOrderMap.get("nowOrder");
                            material=getMaterialNameForProductNoAndStation(orderService.getOneOrderByOrderNo(nowOrder).getProductNo(),stationName);
                            setRedisCarCarryReord(nowOrder,stationName);
                        }

                    }
                    //表明是同种类型的产品,判断同种类型的产品够不够生产
                }else if(Integer.valueOf(totalNumber)>0){
                    //如果剩下的产量不能够生产一个标准的托盘。上一个托盘完成，所以此处为0
                    if(Integer.valueOf(totalNumber)<Integer.valueOf(palletCapilityNumber)){
                        //查下一个订单,第一个不是同种类型的订单
                        if(!nextOrderMap.containsKey("orderNo")){
                            return null;
                        }else{
                            String newOrderNo=nextOrderMap.get("orderNo");
                            Order newOrder=orderService.getOneOrderByOrderNo(newOrderNo);
                            material=getMaterialNameForProductNoAndStation(newOrder.getProductNo(),stationName);
                            setRedisCarCarryReord(newOrderNo,stationName);
                        }
                    }
                    if(Integer.valueOf(totalNumber)>Integer.valueOf(palletCapilityNumber)){
                       String nowOrderNo=nextOrderMap.get("nowOrder");
                        Order nowOrder=orderService.getOneOrderByOrderNo(nowOrderNo);
                        material=getMaterialNameForProductNoAndStation(nowOrder.getProductNo(),stationName);
                        setRedisCarCarryReord(nowOrderNo,stationName);
                    }
                }
                //如果完成的数量小于要生产的数量(),表明此刻只有一个单子在生产
            }else if(Integer.valueOf(number)<quantity){
                    //首先判断该订单剩下的数量能不能够一个托盘生产
                   Order redisOrder=orderService.getOneOrderByOrderNo(redisOrderNo);
                   int leftNumber=redisOrder.getQuantity()-Integer.valueOf(number);
                   Map<String,String> newOrderMap=orderService.getSameMaterialQuantityAndNextOrderNo(redisOrder,leftNumber,Integer.valueOf(palletCapilityNumber),stationName);
                    String totalNumber=newOrderMap.get("totalNumber");
                    //如果要生产的数量小于一个标准的托盘容量
                    if(Integer.valueOf(totalNumber)<Integer.valueOf(palletCapilityNumber)-leftNumber){
                        //进行 下一轮订单的查询
                        if(!newOrderMap.containsKey("orderNo")){
                            return null;
                        }else{
                            String nextOrderNo =newOrderMap.get("orderNo");
                            String productNo=orderService.getOneOrderByOrderNo(nextOrderNo).getProductNo();
                            material=getMaterialNameForProductNoAndStation(productNo,stationName);
                            setRedisCarCarryReord(nextOrderNo,stationName);
                        }
                    }
            }
            //此刻表明是多个微型恶心的订单的情况 order01:50, order02: 60,order:03:70, order04:20   200
            //暂时考虑2个单子吧(可能会有那些恶心的情况出现)清除缓存中前面的单子
            //
        }
        return material.toString();
    }
    //判断物料是否需要切换
    public String getModelChangeMaterial(String stationName,String name){
        StringBuffer materialName=new StringBuffer();
        //读取缓存中的当前工位的当前工单的完成数量
        List<Map<String,String>> mapList =readRedisDateForCurrentStation(stationName);
        log.info("start createTcsOrder station material if need to change");
        //查到当前生产的工单的物料名称
        Map<String,Order> currentOrderAndNextOrder=orderService.getCurrentOrderAndNextOrder();
        //表明现在是空，表明未生产初始化
        if(mapList==null){

        }
        //表明当前只有一个工单在执行
        if(mapList.size()==1){
            //查询当前订单编号,和已经完成的产品数量
            String redisOrderNo=mapList.get(0).get("orderNo");
            log.info("mapList===1,redisOrderNo is {}",redisOrderNo);
            //完成数量
            String number=mapList.get(0).get("number");//获取当前工位已经生产的数量
            //通过订单号查询需要生产的数量
            int quantity=orderService.getOneOrderByOrderNo(redisOrderNo).getQuantity();
            //每个托盘的标准的生产量
            String palletCapilityNumber=palletTypeAndCapilityService.getPalletTypeAndCapilitiesByWorkStation(stationName).getCapility();
            //判断如果当前的完成数量大于等于要生产的数量
            if(Integer.valueOf(number)>=quantity){
                //判断当前已经执行完毕,执行清除工作,逻辑需要完善
                //如果当前的完成数量小于要生产的数量
            }else if(Integer.valueOf(number)<quantity){
                log.info("now mapList==1 producted number <<<<<< plan Number,number is{} quantity is",number,quantity);
                //如果剩料小于等于当前的标准容量,则查下一个订单的物料信息
                if(quantity-Integer.valueOf(number)<=Integer.valueOf(palletCapilityNumber)){
                    log.info("now we juade left componment left plan quantity,next Order {}" );
                    //判断一下当前生产的订单是否等于该订单号,如果等于则可以用下一个工单
                    if(redisOrderNo.equals(currentOrderAndNextOrder.get("currentOrder"))){
                        Order nextOrder=currentOrderAndNextOrder.get("nextOrder");
                        //如果不存在下一个order,则返回空值,空值则不在生成调度单,表明订单结束
                        if(nextOrder==null) {
                            //推送信息到前台。表明系统中的订单结束
                            return null;}
                        //如果存在下一个订单产品
                        else if(nextOrder!=null){
                            String productNo=nextOrder.getProductNo();
                            materialName=getMaterialNameForProductNoAndStation(productNo,stationName);
                        }
                        //此刻订单已经切换了,只需要查当前工单即可
                    } else if(!redisOrderNo.equals(currentOrderAndNextOrder.get("currentOrder").getOrderNo())){
                        Order currentOrder1 =currentOrderAndNextOrder.get("currentOrder");
                        if(currentOrder1==null){
                            //此处返回值为null,maybe 逻辑会有错误,先放这
                            //表明订单结束
                            return null;
                        }
                        else if(currentOrder1!=null){
                            String productNo=currentOrder1.getProductNo();
                            materialName=getMaterialNameForProductNoAndStation(productNo,stationName);
                        }
                    }
                }
                //剩余数量大于托盘的标准数量,则进行叫当前工单的料
                if(quantity-Integer.valueOf(number)>=Integer.valueOf(palletCapilityNumber)){
                    Order redisOrder=orderService.getOneOrderByOrderNo(redisOrderNo);
                    materialName=getMaterialNameForProductNoAndStation(redisOrder.getProductNo(),stationName);
                }
            }
            //如果当前的mapList有2个值
        }else if(mapList.size()==2){
            //计算该order的计划数量
            //表明当前叫料的是第二个元素,先判断,后删除
            Map<String,String> mapSec=mapList.get(1);
            String redisOrderName=mapSec.get("orderNo");
            String redisNumber=mapSec.get("number");
            Order order=orderService.getOneOrderByOrderNo(redisOrderName);
            materialName=getMaterialNameForProductNoAndStation(order.getProductNo(),stationName);
            //删除第一个元素
            deleteEleFromRedis(stationName);
        }
        return null;
    }
    /*判断是否物料需要切换*/
    public String dealWithMaterialgIfChange(String stationName,String name){
            StringBuffer materialName=new StringBuffer();
            //读取缓存中的当前工位的当前工单的完成数量
            List<Map<String,String>> mapList=readRedisDateForCurrentStation(stationName);
            //查到当前生产的工单的物料名称
            Map<String,Order> currentOrderAndNextOrder=orderService.getCurrentOrderAndNextOrder();
            //查询系统中正在进行的订单
            log.info("start create tcsOrder attention please");
           //表明该工位的生产数量为0(还未初始化)，没有查到缓存中的数据
           if(mapList==null){
                 materialName=getMaterialNameForProductNoAndStation(currentOrderAndNextOrder.get("currentOrder").getProductNo(),stationName);
                 log.info("mapList null::::{}",materialName.toString());
                }else  if(mapList.size()==1){
                   //查询当前工单的对应的生产数量
                    String redisOrderNo=mapList.get(0).get("orderNo");
                    log.info("mapList===1,redisOrderNo{}",redisOrderNo);
                    String number=mapList.get(0).get("number");//获取当前工位已经生产的数量
                    //通过订单号查询需要生产的数量
                    int quantity=orderService.getOneOrderByOrderNo(redisOrderNo).getQuantity();
                    //每个托盘的标准的生产量
                    String palletCapilityNumber=palletTypeAndCapilityService.getPalletTypeAndCapilitiesByWorkStation(stationName).getCapility();
                    //判断当前订单已经完成,执行清除
                    if(Integer.valueOf(number)>=quantity){
                       //判断当前已经执行完毕,执行清除
                        deleteEleFromRedis(stationName);
                      //如果当前的生产数量小于总数量
                    }else if(Integer.valueOf(number)<quantity){
                        log.info("now mapList==1 producted number <<<<<< plan Number");
                        //如果剩料小于等于当前的标准容量,则查下一个订单的物料信息
                        if(quantity-Integer.valueOf(number)<Integer.valueOf(palletCapilityNumber)){
                            log.info("now we juade left componment left plan quantity,next Order {}" );
                            //判断一下当前生产的订单是否等于该订单号,如果等于则可以用下一个工单
                            if(redisOrderNo.equals(currentOrderAndNextOrder.get("currentOrder"))){
                                Order nextOrder=currentOrderAndNextOrder.get("nextOrder");
                                //如果不存在下一个order,则返回空值,空值则不在生成调度单
                                if(nextOrder==null){return null;}
                                //如果存在下一个订单产品
                                else if(nextOrder!=null){
                                    String productNo=nextOrder.getProductNo();
                                    materialName=getMaterialNameForProductNoAndStation(productNo,stationName);
                                }
                                //此刻订单已经切换了,只需要查当前工单即可
                            }else if(!redisOrderNo.equals(currentOrderAndNextOrder.get("currentOrder").getOrderNo())){
                                   Order currentOrder1 =currentOrderAndNextOrder.get("currentOrder");
                                   if(currentOrder1==null){
                                       //此处返回值为null,maybe 逻辑会有错误,先放这
                                       return null;
                                   }
                                   else if(currentOrder1!=null){
                                       String productNo=currentOrder1.getProductNo();
                                       materialName=getMaterialNameForProductNoAndStation(productNo,stationName);
                                }
                            }
                        }
                        //剩余数量大于托盘的标准数量,则进行叫当前工单的料
                        if(quantity-Integer.valueOf(number)>=Integer.valueOf(palletCapilityNumber)){
                            Order redisOrder=orderService.getOneOrderByOrderNo(redisOrderNo);
                            materialName=getMaterialNameForProductNoAndStation(redisOrder.getProductNo(),stationName);
                        }
                    }
                }else if(mapList.size()==2){
                    //判断mapList,取其中的第一个元素,判断当前的元素是当前的订单
                    Map<String,String> map=mapList.get(0);
                    //获取第一个元素的当前工单和完成数量
                    String redisOrderNo=map.get("orderNo");
                    String redisOrderNumber=map.get("number");
                    //计算该order的计划数量
                    Order currentOrderDb=orderService.getOneOrderByOrderNo(redisOrderNo);
                    //表明当前的order没生产完
                    if(currentOrderDb.getQuantity()>Integer.valueOf(redisOrderNumber)){
                        //判断需要叫新料还是旧物料
                        //获取当前工位的标准产量
                        String palletTypeAndCapilityCapilityNumber=palletTypeAndCapilityService.getPalletTypeAndCapilitiesByWorkStation(stationName).getCapility();

                       //送下一个订单的物料，剩余的物料小于当前的标准生产量
                       if(Integer.valueOf(currentOrderDb.getQuantity())-Integer.valueOf(redisOrderNumber)<Integer.valueOf(palletTypeAndCapilityCapilityNumber)){
                           //获取下一个订单的信息
                           Map<String,String> nextOrderMap=mapList.get(1);
                           String nextOrderName=nextOrderMap.get("orderNo");
                           log.info("mapList=2,当前的操作是送下一个订单的物料啊,{}",nextOrderName);
                           Order nextOrder=orderService.getOneOrderByOrderNo(nextOrderName);
                           materialName=getMaterialNameForProductNoAndStation(nextOrder.getProductNo(),stationName);
                           //送当前order的物料,剩余的物料大于当前的标准生产量
                       }else if(Integer.valueOf(currentOrderDb.getQuantity())-Integer.valueOf(redisOrderNumber)>=Integer.valueOf(palletTypeAndCapilityCapilityNumber)){
                            materialName=getMaterialNameForProductNoAndStation(currentOrderDb.getProductNo(),stationName);
                           log.info("mapList=2,当前的操作是送当前订单的物料啊,{}",currentOrderDb.getOrderNo());
                        }
                        //完成数量大于等于标准计划数量,则执行删除操作
                    }if(Integer.valueOf(redisOrderNumber)>=Integer.valueOf(currentOrderDb.getQuantity())){
                        //表明当前叫料的是第二个元素,先判断,后删除
                        Map<String,String> mapSec=mapList.get(1);
                        String redisOrderName=mapSec.get("orderNo");
                        String redisNumber=mapSec.get("number");
                        Order order=orderService.getOneOrderByOrderNo(redisOrderName);
                        materialName=getMaterialNameForProductNoAndStation(order.getProductNo(),stationName);
                        //删除第一个元素
                        deleteEleFromRedis(stationName);
                    }
                }
            return materialName.toString().substring(0,materialName.toString().length()-1);
    }
    //读取当前工位的redis的数据,获取当前工位的完成数量的数据
    public List<Map<String,String>> readRedisDateForCurrentStation(String stationName){
        List currentStationFinished=(List)redisTemplate.opsForValue().get(stationName);
        return  currentStationFinished;
    }

    /**
     * 通过工单查询某个工位上的物料的名称
     * @param productNo
     * @return
     */
    public StringBuffer getMaterialNameForProductNoAndStation(String productNo,String workStation){
        StringBuffer materialName=new StringBuffer();
        List<ConsumedParts> consumedPartsList=consumedPartsRepository.getConsumPartsByProducntNoAndStation(productNo,workStation);
        if(consumedPartsList==null||consumedPartsList.size()<1){
            materialName.append("noMaterialName,");
        }else{
            for(ConsumedParts consumedParts:consumedPartsList){
                materialName.append(consumedParts.getPartName()+",");}
        }
        return materialName;
    }
    public void deleteEleFromRedis(String station){
        List list=(List)readRedisDateForCurrentStation(station);
        log.info("execute a delete operation{}",station);
        int listSize=list.size();//5 移除前面的所有的数据
        for(int i=0;i<listSize-1;i++){
            list.remove(0);
        }
/*        //移除第一个数据
        list.remove(0);*/
        redisTemplate.opsForValue().set(station,list);
    }
    //设置缓存中的小车运输每个单子的记录
    public void setRedisCarCarryReord(String orderNo,String station){
        HashOperations hashOperations =redisTemplate.opsForHash();
        String carNum=(String)hashOperations.get(orderNo,station);
        if(carNum==null){
            hashOperations.put(orderNo,station,"1");
        }else if(carNum!=null){
            int carValue=Integer.valueOf(carNum);
            hashOperations.put(orderNo,station,carValue);
        }
    }
}
