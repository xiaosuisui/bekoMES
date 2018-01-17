package com.mj.beko.service.ApiService;

import com.mj.beko.domain.OperatorShift;
import com.mj.beko.domain.OperatorShiftDetail;
import com.mj.beko.domain.Order;
import com.mj.beko.repository.ShiftDetailRepository;
import com.mj.beko.service.CycleTimeTargetService;
import com.mj.beko.service.OrderService;
import com.mj.beko.service.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Ricardo on 2017/12/12.
 * 获取当前应该生产的数量。
 */
@Service
@Transactional
public class CurrentShiftServiceImpl implements CurrentShiftService {
    @Autowired
    private OrderService orderService;
    @Autowired
    private CycleTimeTargetService cycleTimeTargetService;
    @Autowired
    private ShiftService shiftService;
    @Autowired
    private ShiftDetailRepository shiftDetailRepository;
    @Override
    public int getCurrentShiftNumber() {
        //获取当前正在生产的订单
        Map<String,Order> mapOrder=orderService.getCurrentOrderAndNextOrder();
        //获取当前订单的每小时的产量
        String targetNumberForCurrentOrderId=cycleTimeTargetService.getCycleTimeTargetByProductId(mapOrder.get("currentOrder").getProductNo()).getTarget();
        //根据每小时的产量,计算3分钟的产量
        int currentQuantity=Integer.valueOf(targetNumberForCurrentOrderId) * 3/60;
        return currentQuantity;
    }

    /**
     * 判断当前shift是否在结束的时间范围内
     * @param shiftName
     * @return
     */
    @Override
    public String getResultIfShiftCanEnd(String shiftName,String date) {
        //获取当前shift的workTime的结束时间
        OperatorShift operatorShift=shiftService.getCurrentShift();
        List<OperatorShiftDetail> operatorShiftDetailList=shiftDetailRepository.getOperatorShiftByShiftName(operatorShift.getId(),shiftName);
        for(OperatorShiftDetail operatorShiftDetail:operatorShiftDetailList){
            if("workTime".equals(operatorShiftDetail.getContentType())){
                String startTime=operatorShiftDetail.getStartTime();
                String endTime=operatorShiftDetail.getEndTime();
                //如果开始时间小于结束时间
                if(Integer.valueOf(startTime.split(":")[0])<Integer.valueOf(endTime.split(":")[0])){
                    String newEndDate=date+" "+endTime+":00";
                    //判断当前时间是否大于结束时间的半小时
                    String formTime =timeFormat();
                    long currentTime=formatStringToDate(timeFormat()).getTime();
                    long currentTime1=formatStringToDate(newEndDate).getTime();
                     long result=currentTime-currentTime1;
                   if(new Date().getTime()>formatStringToDate(newEndDate).getTime()-30*60*1000){
                       return "ok";
                   }
                }
                //如果开始时间大于结束时间
                if(Integer.valueOf(startTime.split(":")[0]) > Integer.valueOf(endTime.split(":")[0])){
                    String newEndDate=date+" "+endTime+":00";
                    Calendar c = Calendar.getInstance();
                    c.setTime(formatStringToDate(newEndDate));
                    c.add(Calendar.DAY_OF_MONTH, 1);
                    if(new Date().getTime()>c.getTimeInMillis()-30*60*1000){
                        return "ok";
                    }
                }
                return "nok";
            }
            }
            return "nok";
        }
    //获取当前的系统时间
    public Date formatStringToDate(String str){
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
       return new Date();
    }
    public String timeFormat(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}
