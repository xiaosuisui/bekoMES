package com.mj.beko.service.ApiService;

import com.mj.beko.domain.CycleTimeTarget;
import com.mj.beko.domain.OperatorShift;
import com.mj.beko.domain.OperatorShiftDetail;
import com.mj.beko.domain.Order;
import com.mj.beko.repository.ShiftDetailRepository;
import com.mj.beko.service.CycleTimeTargetService;
import com.mj.beko.service.OrderService;
import com.mj.beko.service.ShiftService;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ricardo on 2017/12/9.
 * 计算每一shift的target 数量
 */
@Service
@Transactional
@Slf4j
public class ShiftTargetServiceImpl implements ShiftTargetService {
    @Autowired
    private ShiftService shiftService;
    @Autowired
    private ShiftDetailRepository shiftDetailRepository;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CycleTimeTargetService cycleTimeTargetService;
    @Override
    public Map<String,String> getTargetQuantityAndShiftName() {
        Map<String,String> newMap =new HashMap<String,String>();
        OperatorShift shift =shiftService.getCurrentShift();
        if(shift==null) return newMap;
        //获取当前是早班中班晚班
        Map<String,String> shiftMap=getCurrentShift(shift);
        if(shiftMap==null || shiftMap.size()==0) return newMap;
        //获取该班的工作时间和休息时间
        int breakTimeCount=getWorkTimeAndBreakTime(String.valueOf(shift.getId()),"breakTime",shiftMap.get("shiftName"));
        //获取工作时间
        int workTimeCount=Integer.valueOf(shiftMap.get("workTimeCount"));
        int workTime=(workTimeCount-breakTimeCount)*60;//获取该班次的工作时间
         Map<String,String> map=leftProductIdAndNumber();
        List<Order> orderList=orderService.getOrderListForShiftTarget();
        int shiftNumber=getFinalTargetShiftNumber(workTime,map,orderList);
        shiftMap.put("shiftNumber",String.valueOf(shiftNumber));
        //查询当前正在进行的订单
        return shiftMap;
    }
    //获取当前的shift的班次
    public Map<String,String> getCurrentShift(OperatorShift operatorShift){
        List<OperatorShiftDetail> operatorShiftDetailList=shiftDetailRepository.getOperatorShiftDetailByShiftId(String.valueOf(operatorShift.getId()));
        //获取其中的早班，中班,晚班时间点
        for(OperatorShiftDetail operatorShiftDetail:operatorShiftDetailList){
            Map<String,String> map =new HashMap<String,String>();
            //判断是否属于早班
            if("morningShift".equals(operatorShiftDetail.getName()) &&"workTime".equals(operatorShiftDetail.getContentType())){
                String firstShiftStartTime =operatorShiftDetail.getStartTime();
                String firstShiftEndTime=operatorShiftDetail.getEndTime();
                //判断系统配置开始时间跟系统配置结束时间的关系(当前时间大于开始时间,当前时间小于结束时间)
                if(compareTime(firstShiftStartTime,timeFormat()) && !compareTime(firstShiftEndTime,timeFormat())){
                    map.put("shiftName","morningShift");
                    map.put("workTimeCount",String.valueOf(operatorShiftDetail.getCountTime()));
                    return map;
                }
            }
            //判断是否属于中班
            if("middleShift".equals(operatorShiftDetail.getName()) &&"workTime".equals(operatorShiftDetail.getContentType())){
                String firstShiftStartTime =operatorShiftDetail.getStartTime();
                String firstShiftEndTime=operatorShiftDetail.getEndTime();
                if(compareTime(firstShiftStartTime,timeFormat()) && !compareTime(firstShiftEndTime,timeFormat())){
                    map.put("shiftName","middleShift");
                    map.put("workTimeCount",String.valueOf(operatorShiftDetail.getCountTime()));
                    return map;
                }
            }
            //是否属于晚班
            if("eveningShift".equals(operatorShiftDetail.getName()) &&"workTime".equals(operatorShiftDetail.getContentType())){
                String firstShiftStartTime =operatorShiftDetail.getStartTime();
                String firstShiftEndTime=operatorShiftDetail.getEndTime();
                if(firstShiftEndTime.equals("00:00")) {firstShiftEndTime="24:00";}
                //判断当前的时间点是否跨越了24点,如果没有跨越12点
                if(Integer.valueOf(firstShiftStartTime.split(":")[0])<Integer.valueOf(firstShiftEndTime.split(":")[0])) {
                    if (compareTime(firstShiftStartTime, timeFormat()) && !compareTime(firstShiftEndTime, timeFormat())) {
                        map.put("shiftName", "eveningShift");
                        map.put("workTimeCount", String.valueOf(operatorShiftDetail.getCountTime()));
                        return map;
                    }
                    //如果跨越了12点
                }else if(Integer.valueOf(firstShiftStartTime.split(":")[0])>Integer.valueOf(firstShiftEndTime.split(":")[0])){
                    //结束时间加上24点
                    firstShiftEndTime=String.valueOf(Integer.valueOf(firstShiftEndTime.split(":")[0])+24)+":"+String.valueOf(firstShiftEndTime.split(":")[1]);
                    if (compareTime(firstShiftStartTime, timeFormat()) && !compareTime(firstShiftEndTime, timeFormat())) {
                        map.put("shiftName", "eveningShift");
                        map.put("workTimeCount", String.valueOf(operatorShiftDetail.getCountTime()));
                        return map;
                    }
                }

            }
        }
        return new HashMap<>();
    }
    //查询某个班次的工作时间和休息时间
    public int getWorkTimeAndBreakTime(String id,String contentType,String name){
        List<OperatorShiftDetail> shiftDetailList=shiftDetailRepository.getOperatorShiftDetailByShiftIdAnAndContentType(id,contentType,name);
        int resultTime=0;
       for(OperatorShiftDetail detail:shiftDetailList){
           resultTime+=detail.getCountTime();
       }
       return  resultTime;
    }
    //获取当前的系统时间
    public String timeFormat() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }
    //判断是否属于某个班次(提前40分钟的缓存) 40 60 3600
    public static boolean compareTime(String s1,String currentTime){
        int cacheTime=35 * 60;//缓存时间为35分钟，此处转化为秒
        if("00:00".equals(s1)){
            s1="24:00";
        }
        try {
            if (s1.indexOf(":")<0||currentTime.indexOf(":")<0) {
                log.info("wrong time format");
            }else{
                String[]array1 = s1.split(":");
                int configTotalTime = Integer.valueOf(array1[0])*3600+Integer.valueOf(array1[1])*60-cacheTime;
                String[]array2 = currentTime.split(":");
                int currentTotal = Integer.valueOf(array2[0])*3600+Integer.valueOf(array2[1])*60+Integer.valueOf(array2[2]);
                //判断当前时间是否大于系统中配置的开始时间和结束时间
                return currentTotal-configTotalTime>=0?true:false;
            }
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            return true;
        }
        return false;
    }
    //shift 转换后获取当前的工单剩下的产品的数量
    public Map<String,String> leftProductIdAndNumber(){
        Map<String,String> map =new HashMap<String,String>();
        log.info("get currentOrder and leftNumber");
        Map<String,Order> mapOrder=orderService.getCurrentOrderAndNextOrder();
        Order currentOrder=mapOrder.get("currentOrder");
        int leftNumber=currentOrder.getQuantity()-currentOrder.getCompletionNumber()-currentOrder.getBrokenNumber();
        map.put("productId",currentOrder.getProductNo());
        if(leftNumber<0) map.put("leftNumber",String.valueOf(leftNumber));
        if(leftNumber>=0) map.put("leftNumber",String.valueOf(leftNumber));
        return map;
    }

    /**
     *
     * @param totalTime(一共需要工作的时间  s)
     * @param map (当前的产品Id和当前的订单剩下的数量)
     * @param orders(未生产的订单列表)
     * @return
     */
    //获取每一个shift的产量
    public int getFinalTargetShiftNumber(int totalTime,Map<String,String> map,List<Order> orders){
        int workTime=0;//工作剩余的时间
        int shiftTargetNumber=0;//shiftTargetNumber
        //判断剩下的leftNumber订单够不够一个shift生产的
        //正在生产的productId的每小时目标产量
        String targetNumberForCurrentOrderId=cycleTimeTargetService.getCycleTimeTargetByProductId(map.get("productId")).getTarget();
        //计算剩下的数量需要多少时间生产完(剩下数量/每小时产量 * 3600)
        int needLeftTime  =(Integer.valueOf(map.get("leftNumber"))) * 3600 /Integer.valueOf(targetNumberForCurrentOrderId);
        //判断如果需要时间小于shift时间,则工作时间增加,目标产量增加
        if(needLeftTime<totalTime){
            workTime+=needLeftTime;
            shiftTargetNumber+= Integer.valueOf(map.get("leftNumber"));
            //如果需要的时间大于该班次的时间,则计算该shift时间内能生产多少个产品(时间 * 小时产量)
        }else if(needLeftTime>totalTime){
            shiftTargetNumber=(totalTime * Integer.valueOf(targetNumberForCurrentOrderId))/3600;
            return shiftTargetNumber;
        }
        //增加极端情况下的判断,表明当前没有新的订单
        if(orders==null ||orders.size()==0){
            return shiftTargetNumber;
        }
        //如果正在生产的订单满足不了生产需求,则遍历订单列表中的订单
        for(int i=0;i<orders.size();i++){
            Order order  =orders.get(i);
            //获取订单的小时产量
            String targetNumber=cycleTimeTargetService.getCycleTimeTargetByProductId(order.getProductNo()).getTarget();
            int time=order.getQuantity() * 3600 /Integer.valueOf(targetNumber);//生产完该订单需要的时间
            //如果当前时间小于shifttime
            workTime+=time;
            shiftTargetNumber+=order.getQuantity();
            //判断如果时间超时.
            if(workTime>totalTime){
                //减去刚才加的数量
                shiftTargetNumber=shiftTargetNumber-order.getQuantity();
                //减去刚才加的时间,并计算剩下的时间能生产多少个产品(leftTime/3600 *小时产量)
                int leftTime=totalTime-(workTime-time);
                shiftTargetNumber+= (leftTime * Integer.valueOf(targetNumber))/3600;
                return shiftTargetNumber;
            }
            //增加极端情况下的判断,如果当前的订单产品不足够该shifit生产
            if(i==orders.size()-1){
                return shiftTargetNumber;
            }
        }
        return 0;
    }
}

