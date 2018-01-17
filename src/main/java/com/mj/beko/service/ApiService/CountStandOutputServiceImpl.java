package com.mj.beko.service.ApiService;

import com.mj.beko.domain.OperatorShift;
import com.mj.beko.domain.OperatorShiftDetail;
import com.mj.beko.service.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Ricardo on 2017/12/14.
 */
@Service
@Transactional
public class CountStandOutputServiceImpl implements CountStandOutputService {
    @Autowired
    private ShiftService shiftService;
    @Autowired
    private ShiftTargetService shiftTargetService;
    /**
     * 获取标准的输出产量,shiftName由前端传入
     * shiftName (早班,中班,晚班)
     * @return
     */
    @Override
    public Object[] getCountStandOutputTimeRange(String shiftName) {
        //获取当前正在执行的shifit
        OperatorShift shift =shiftService.getCurrentShift();
        //获取当前的shiftDetail
        List<OperatorShiftDetail> shiftTargetDetailList=shiftService.getOperatorShiftByShiftName(shift.getId(),shiftName);
        //遍历出其中的开始时间和结束时间
        for(OperatorShiftDetail operatorShiftDetail:shiftTargetDetailList){
            //获取工作时间的开始时间和结束时间
            if("workTime".equals(operatorShiftDetail.getContentType())){
                 String startTime=operatorShiftDetail.getStartTime();//8:00
                 String endTime=operatorShiftDetail.getEndTime();//16:00
                 return getTimeRangeForTvScreen(startTime,endTime);
            }
        }
        //获取当前的shift
        return new Object[]{};
    }

    public Object[] getTimeRangeForTvScreen(String startTime,String endTime){
        //截取开始时间
        int startValue=Integer.valueOf(startTime.split(":")[0]);
        int endValue=Integer.valueOf(endTime.split(":")[0]);
        List<String> list = new ArrayList<String>();
        //判断当前时间是否跨越24点
        if(startValue<endValue){
            for(int i=0;i<endValue-startValue;i++){
                StringBuffer sb =new StringBuffer();
                int time=startValue+i;
                list.add(sb.append(String.valueOf(time)).append(":00").toString());
            }
        }
        //如果跨越了24点
        if(startValue>endValue){
            int range=24-startValue+endValue;
            for(int i=0;i<range;i++){
                StringBuffer sb =new StringBuffer();
                int time=(startValue+i)%24;
                list.add(sb.append(String.valueOf(time)).append(":00").toString());
            }
        }
        return list.toArray();
    }
}
