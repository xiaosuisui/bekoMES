package com.mj.beko.tcs;

import com.mj.beko.domain.TscOrderCreateTemplate;
import com.mj.beko.service.TscOrderCreateTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.inject.Inject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by jc on 2017/8/2.
 * 初始化调拨单
 */
@Service
@Transactional
@Slf4j
public class InitTcsOrderService {

    public static final String MIDEA_SCENE_NAME = "abcdef";

    @Inject
    private TscOrderCreateTemplateService tscOrderCreateTemplateService;

    @Inject
    private TcsClientSendService tcsClientSendService;

    /**
     * 生成调拨单并发送
     * @param name (调拨单的参数)
     * @param functionName (调拨单的类型, (叫料(callMaterial)---->>>> 空托盘(callEmptyPallet)))
     * 调拨单程序的入口
     */
    public void createTcsOrderSet(String name, String functionName) throws IOException {
        /*查询该调拨单对应的参数,动态的添加拼凑调拨单*/
        List<TscOrderCreateTemplate> tscOrderCreateTemplateList = tscOrderCreateTemplateService.findAllByNameAndFunctionName(name, functionName);
        if(tscOrderCreateTemplateList==null ||tscOrderCreateTemplateList.size()<1) return;
        TscOrderCreateTemplate tscOrderCreateTemplate=tscOrderCreateTemplateList.get(0);
        if(tscOrderCreateTemplate==null) return;
        //根据不同的functionName调度不同的订单模板
        String xml="";
        if(functionName.equals("GUNTONG")){
            xml = createTcsOrderTemplate(timeFormat(), tscOrderCreateTemplate);
        }
        if(functionName.equals("LIULIJIA")){
           xml = createLiuLiJiaOrderTemplate(timeFormat(), tscOrderCreateTemplate);
        }
        if(functionName.equals("EPSUP")){
           xml = creatEpsUpOrderTemplate(timeFormat(), tscOrderCreateTemplate);
        }
        if(functionName.equals("EPSDOWN")){
            xml = creatEpsDownOrderTemplate(timeFormat(), tscOrderCreateTemplate);
        }
        //该xml文件待完善(定点)
        if(functionName.equals("BOTTOMANDTOPPLATE")){
            xml=createPlateTypeOrderTemplate(timeFormat(),tscOrderCreateTemplate);
        }
        //此处开始用来区分单独的情景(单独的送料取料)(滚筒取空托盘)
        if(functionName.equals("SCROLLEMPTY")){
            xml=createScrollerForEmptyPallet(timeFormat(),tscOrderCreateTemplate);
        }
        //滚筒单独送物料
        if(functionName.equals("SCROLLMATERIAL")){
            xml=createScrollerForMaterial(timeFormat(),tscOrderCreateTemplate);
        }
        //第一第二工位叫空车
        if(functionName.equals("BOTTOMPLATEFOREMPTYCAR")){
            xml=createPlateEmptyCarOrderTemplate(timeFormat(),tscOrderCreateTemplate);
        }
        //第一第二工位送物料
        if(functionName.equals("BOTTOMPLATEFORMATERIAL")){
           xml=createPlateForMaterialTemplate(timeFormat(),tscOrderCreateTemplate);
        }
        log.debug("生成的调拨单为::::::" + xml);
        tcsClientSendService.sendTcsOrderSet(xml, tscOrderCreateTemplate);
    }

    /**
     * 生成调拨单的操作完成指令并发送
     * @param orderName
     */
    public void createNextStepTcsOrder(String orderName, String locationName) throws IOException {
        //生成调拨单的操作完成指令
        String operationTcsOrder = createNextStepTcsOrderTemplate(orderName,locationName);
        log.info("WAIT:0 Progress=100:::::::::::::" + operationTcsOrder);
        //发送调拨单的操作完成指令
        tcsClientSendService.sendTcsOrderSet(operationTcsOrder, null);
    }

    //################################定义调拨单和operationOrder####################################

    /**
     *
     * @param deadlineTime
     * @param tscOrderCreateTemplate
     * @return
     */
    //滚筒类型的tcsOrder
    public String createTcsOrderTemplate(String deadlineTime,TscOrderCreateTemplate tscOrderCreateTemplate) {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                   + "<tcsOrderSet>"
                   + "<sceneName>" + MIDEA_SCENE_NAME + "</sceneName>"
                   + "<order xsi:type=\"transport\" deadline=\"" + deadlineTime + "\" intendedVehicle=\"" + tscOrderCreateTemplate.getVerticalType() + "\" vehicleTypeAvailable=\"true\" id=\"\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                   + "<destination locationName=\"" + tscOrderCreateTemplate.getPalletEmptyPoint() + "\" operation=\"WAIT:0\" />" //到达空托盘点，等待
                   + "<destination locationName=\"" + tscOrderCreateTemplate.getPalletEmptyPoint() + "\" operation=\"OP_ROLLER_LOAD\" />" //空车执行上空托盘命令
                   + "<destination locationName=\"" + tscOrderCreateTemplate.getPalletOutPoint() + "\" operation=\"WAIT:0\" />" //到达空托盘下料点,等待
                   + "<destination locationName=\"" + tscOrderCreateTemplate.getPalletOutPoint() + "\" operation=\"OP_ROLLER_UNLOAD\" />" //agv执行下空托盘命令
                   + "<destination locationName=\"" + tscOrderCreateTemplate.getMaterialStartPoint() + "\" operation=\"WAIT:0\" />" //到达上料点 等待
                   + "<destination locationName=\"" + tscOrderCreateTemplate.getMaterialStartPoint() + "\" operation=\"OP_ROLLER_LOAD\" />" //agv执醒上料
                   + "<destination locationName=\"" + tscOrderCreateTemplate.getMaterialOutPoint() + "\" operation=\"NOP\" />" //到达工位点，等待
                   + "<destination locationName=\"" + tscOrderCreateTemplate.getMaterialOutPoint() + "\" operation=\"OP_ROLLER_UNLOAD\" />" //agv执醒上料
                   + "<destination locationName=\"" + tscOrderCreateTemplate.getStopCarPoint() + "\" operation=\"NOP\" />" //到达停车点
                   + "</order>"
                   + "</tcsOrderSet>";
        return xml;
    }

    //EPSDOWN类型的订单(先到达下泡沫区，在到达上泡沫区)(送下泡沫，单滚筒滚动或者双滚筒滚动，plc控制)
    public String creatEpsDownOrderTemplate(String deadlineTime,TscOrderCreateTemplate tscOrderCreateTemplate){
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<tcsOrderSet>"
                + "<sceneName>" + MIDEA_SCENE_NAME + "</sceneName>"
                + "<order xsi:type=\"transport\" deadline=\"" + deadlineTime + "\" intendedVehicle=\"" + tscOrderCreateTemplate.getVerticalType() + "\" vehicleTypeAvailable=\"false\" id=\"\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                + "<destination locationName=\"" + tscOrderCreateTemplate.getMaterialStartPoint() + "\" operation=\"NOP\" />" //AGV小车到达EPS上料点
                + "<destination locationName=\"" + tscOrderCreateTemplate.getMaterialStartPoint() + "\" operation=\"OP_EPS_ROLLER_LOAD\" />" //AGV小车到达EPS上料点
                + "<destination locationName=\"" + tscOrderCreateTemplate.getMaterialOutPoint() + "\" operation=\"NOP\" />" //AGV小车到达EPS下泡沫区
                + "<destination locationName=\"" + tscOrderCreateTemplate.getMaterialOutPoint() + "\" operation=\"OP_EPS_ROLLER_LOW_UNLOAD\" />" //AGV小车到达EPS下泡沫区
                + "<destination locationName=\"" + tscOrderCreateTemplate.getMaterialOutPoint() + "\" operation=\"WAIT:0\" />" //agv停车等待(等待处理)
                + "<destination locationName=\"" + tscOrderCreateTemplate.getSecMaterialOutPoint() + "\" operation=\"NOP\" />" //agv到达上泡沫区
                + "<destination locationName=\"" + tscOrderCreateTemplate.getSecMaterialOutPoint() + "\" operation=\"OP_EPS_ROLLER_HIGH_UNLOAD\" />" //agv到达上泡沫区
                + "<destination locationName=\"" + tscOrderCreateTemplate.getStopCarPoint() + "\" operation=\"NOP\" />" //agv到达停车点
                + "</order>"
                + "</tcsOrderSet>";
        return xml;
    }
    //EPSUP类型的订单(EPSUP单工位)
    public String creatEpsUpOrderTemplate(String deadlineTime,TscOrderCreateTemplate tscOrderCreateTemplate){
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<tcsOrderSet>"
                + "<sceneName>" + MIDEA_SCENE_NAME + "</sceneName>"
                + "<order xsi:type=\"transport\" deadline=\"" + deadlineTime + "\" intendedVehicle=\"" + tscOrderCreateTemplate.getVerticalType() + "\" vehicleTypeAvailable=\"false\" id=\"\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                + "<destination locationName=\"" + tscOrderCreateTemplate.getMaterialStartPoint() + "\" operation=\"NOP\" />" //AGV小车到达EPS上料点
                + "<destination locationName=\"" + tscOrderCreateTemplate.getMaterialStartPoint() + "\" operation=\"OP_EPS_ROLLER_LOAD\" />" //AGV小车到达EPS上料点
                + "<destination locationName=\"" + tscOrderCreateTemplate.getMaterialOutPoint() + "\" operation=\"NOP\" />" //AGV小车到达EPS下泡沫区,继续前进
                + "<destination locationName=\"" + tscOrderCreateTemplate.getMaterialOutPoint() + "\" operation=\"NOP\" />" //AGV小车到达EPS下泡沫区,继续前进
                + "<destination locationName=\"" + tscOrderCreateTemplate.getSecMaterialOutPoint() + "\" operation=\"NOP\" />" //agv到达上泡沫区
                + "<destination locationName=\"" + tscOrderCreateTemplate.getSecMaterialOutPoint() + "\" operation=\"OP_EPS_ROLLER_HIGH_UNLOAD\" />" //agv到达上泡沫区，卸载物料(上)
                + "<destination locationName=\"" + tscOrderCreateTemplate.getStopCarPoint() + "\" operation=\"NOP\" />" //agv到达停车点
                + "</order>"
                + "</tcsOrderSet>";
        return xml;
    }

    //流利架类型的订单(先到达物料区,送料,然后运空料)
    public String createLiuLiJiaOrderTemplate(String deadlineTime,TscOrderCreateTemplate tscOrderCreateTemplate){
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<tcsOrderSet>"
                + "<sceneName>" + MIDEA_SCENE_NAME + "</sceneName>"
                + "<order xsi:type=\"transport\" deadline=\"" + deadlineTime + "\" intendedVehicle=\"" + tscOrderCreateTemplate.getVerticalType() + "\" vehicleTypeAvailable=\"false\" id=\"\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                + "<destination locationName=\"Location-0026\" operation=\"OP_ROLLER_LOAD\" />" //AGV提前伸杆
                + "<destination locationName=\"" + tscOrderCreateTemplate.getMaterialOutPoint() + "\" operation=\"WAIT:0\" />" //AGV小车旋钮工位物料上料点
                + "<destination locationName=\"" + tscOrderCreateTemplate.getPalletOutPoint() + "\" operation=\"WAIT:0\" />" //AGV小车旋钮工位空托盘下料点
                + "<destination locationName=\"" + tscOrderCreateTemplate.getPalletOutPoint() + "\" operation=\"OP_ROLLER_UNLOAD\" />" //AGV缩杆
                + "<destination locationName=\"Location-0014\" operation=\"OP_ROLLER_LOAD\" />" //AGV伸杆
                + "<destination locationName=\"" + tscOrderCreateTemplate.getPalletEmptyPoint() + "\" operation=\"OP_ROLLER_LOAD\" />" //AGV小车物料区下空托盘
                + "<destination locationName=\"" + tscOrderCreateTemplate.getPalletEmptyPoint() + "\" operation=\"NOP\" />"
                + "<destination locationName=\"" + tscOrderCreateTemplate.getMaterialStartPoint() + "\" operation=\"OP_ROLLER_UNLOAD\" />" //AGV小车物料区上料
                + "<destination locationName=\"" + tscOrderCreateTemplate.getMaterialStartPoint() + "\" operation=\"NOP\" />"
                + "<destination locationName=\"" + tscOrderCreateTemplate.getStopCarPoint() + "\" operation=\"NOP\" />" //agv到达停车点
                + "</order>"
                + "</tcsOrderSet>";
        return xml;
    }
    //滚筒类型的调度单单独取空托盘
    public String createScrollerForEmptyPallet(String deadlineTime,TscOrderCreateTemplate tscOrderCreateTemplate){
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<tcsOrderSet>"
                + "<sceneName>" + MIDEA_SCENE_NAME + "</sceneName>"
                + "<order xsi:type=\"transport\" deadline=\"" + deadlineTime + "\" intendedVehicle=\"" + tscOrderCreateTemplate.getVerticalType() + "\" vehicleTypeAvailable=\"true\" id=\"\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                + "<destination locationName=\"" + tscOrderCreateTemplate.getPalletEmptyPoint() + "\" operation=\"WAIT:0\" />" //到达空托盘点，等待
                + "<destination locationName=\"" + tscOrderCreateTemplate.getPalletEmptyPoint() + "\" operation=\"OP_ROLLER_LOAD\" />" //空车执行上空托盘命令
                + "<destination locationName=\"" + tscOrderCreateTemplate.getPalletEmptyPoint() + "\" operation=\"NOP\" />" //4步停车第二步
                + "<destination locationName=\"" + tscOrderCreateTemplate.getPalletOutPoint() + "\" operation=\"WAIT:0\" />" //到达空托盘下料点,等待
                + "<destination locationName=\"" + tscOrderCreateTemplate.getPalletOutPoint() + "\" operation=\"OP_ROLLER_UNLOAD\" />" //agv执行下空托盘命令
                + "<destination locationName=\"" + tscOrderCreateTemplate.getPalletOutPoint() + "\" operation=\"NOP\" />" //到达空托盘下料点,等待
                + "<destination locationName=\"" + tscOrderCreateTemplate.getStopCarPoint() + "\" operation=\"NOP\" />" //到达停车点
                + "</order>"
                + "</tcsOrderSet>";
        return xml;
    }
    //滚筒类型的调度单单独送物料到工位
    public String createScrollerForMaterial(String deadlineTime,TscOrderCreateTemplate tscOrderCreateTemplate){
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<tcsOrderSet>"
                + "<sceneName>" + MIDEA_SCENE_NAME + "</sceneName>"
                + "<order xsi:type=\"transport\" deadline=\"" + deadlineTime + "\" intendedVehicle=\"" + tscOrderCreateTemplate.getVerticalType() + "\" vehicleTypeAvailable=\"true\" id=\"\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                + "<destination locationName=\"" + tscOrderCreateTemplate.getMaterialStartPoint() + "\" operation=\"WAIT:0\" />" //到达上料点 等待
                + "<destination locationName=\"" + tscOrderCreateTemplate.getMaterialStartPoint() + "\" operation=\"OP_ROLLER_LOAD\" />" //agv执醒上料
                + "<destination locationName=\"" + tscOrderCreateTemplate.getMaterialStartPoint() + "\" operation=\"NOP\" />" //伪造停车次数
                + "<destination locationName=\"" + tscOrderCreateTemplate.getMaterialOutPoint() + "\" operation=\"NOP\" />" //到达工位点，等待
                + "<destination locationName=\"" + tscOrderCreateTemplate.getMaterialOutPoint() + "\" operation=\"OP_ROLLER_UNLOAD\" />" //agv执醒上料
                + "<destination locationName=\"" + tscOrderCreateTemplate.getMaterialOutPoint() + "\" operation=\"NOP\" />" //伪造停车次数
                + "<destination locationName=\"" + tscOrderCreateTemplate.getStopCarPoint() + "\" operation=\"NOP\" />" //到达停车点
                + "</order>"
                + "</tcsOrderSet>";
        return xml;
    }
    //流利架的小车单独取送物料
    public String createLiulijiaForEmptyBox(String deadlineTime,TscOrderCreateTemplate tscOrderCreateTemplate){
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<tcsOrderSet>"
                + "<sceneName>" + MIDEA_SCENE_NAME + "</sceneName>"
                + "<order xsi:type=\"transport\" deadline=\"" + deadlineTime + "\" intendedVehicle=\"" + tscOrderCreateTemplate.getVerticalType() + "\" vehicleTypeAvailable=\"false\" id=\"\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                + "<destination locationName=\"Location-0026\" operation=\"OP_ROLLER_LOAD\" />" //AGV提前伸杆
                + "<destination locationName=\"" + tscOrderCreateTemplate.getMaterialOutPoint() + "\" operation=\"WAIT:0\" />" //AGV小车旋钮工位物料上料点
                + "<destination locationName=\"" + tscOrderCreateTemplate.getPalletOutPoint() + "\" operation=\"WAIT:0\" />" //AGV小车旋钮工位空托盘下料点
                + "<destination locationName=\"" + tscOrderCreateTemplate.getPalletOutPoint() + "\" operation=\"OP_ROLLER_UNLOAD\" />" //AGV缩杆
                + "<destination locationName=\"Location-0014\" operation=\"OP_ROLLER_LOAD\" />" //AGV伸杆
                + "<destination locationName=\"" + tscOrderCreateTemplate.getPalletEmptyPoint() + "\" operation=\"OP_ROLLER_LOAD\" />" //AGV小车物料区下空托盘
                + "<destination locationName=\"" + tscOrderCreateTemplate.getPalletEmptyPoint() + "\" operation=\"NOP\" />"
                + "<destination locationName=\"" + tscOrderCreateTemplate.getMaterialStartPoint() + "\" operation=\"OP_ROLLER_UNLOAD\" />" //AGV小车物料区上料
                + "<destination locationName=\"" + tscOrderCreateTemplate.getMaterialStartPoint() + "\" operation=\"NOP\" />"
                + "<destination locationName=\"" + tscOrderCreateTemplate.getStopCarPoint() + "\" operation=\"NOP\" />" //agv到达停车点
                + "</order>"
                + "</tcsOrderSet>";
        return xml;
    }
    //第一第二工位Agv类型的小车(先到工位边把空小车拉走,-> 然后送料到工位边)
    public String createPlateTypeOrderTemplate(String deadlineTime,TscOrderCreateTemplate tscOrderCreateTemplate){
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<tcsOrderSet>"
                + "<sceneName>" + MIDEA_SCENE_NAME + "</sceneName>"
                + "<order xsi:type=\"transport\" deadline=\"" + deadlineTime + "\" intendedVehicle=\"" + tscOrderCreateTemplate.getVerticalType() + "\" vehicleTypeAvailable=\"false\" id=\"\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                + "<destination locationName=\"" + tscOrderCreateTemplate.getPalletEmptyPoint() + "\" operation=\"OP_LIFTER_LOAD\" />" //AGV小车到达工位拉空车顶升
                + "<destination locationName=\"" + tscOrderCreateTemplate.getPalletEmptyPoint() + "\" operation=\"NOP\" />" //AGV小车到达工位拉空车
                + "<destination locationName=\"" + tscOrderCreateTemplate.getPalletOutPoint() + "\" operation=\"OP_LIFTER_UNLOAD\" />" //AGV把小车拉到停车点,下降
                + "<destination locationName=\"" + tscOrderCreateTemplate.getPalletOutPoint() + "\" operation=\"NOP\" />" //AGV小车到达工位拉空车
                + "<destination locationName=\"" + tscOrderCreateTemplate.getPalletOutPoint() + "\" operation=\"WAIT:0\" />" //AGV小车停车点等待上料
                + "<destination locationName=\"" + tscOrderCreateTemplate.getPalletOutPoint() + "\" operation=\"OP_LIFTER_LOAD\" />" //AGV小车顶升
                + "<destination locationName=\"" + tscOrderCreateTemplate.getMaterialOutPoint() + "\" operation=\"OP_LIFTER_UNLOAD\" />" //AGV小车下降
                + "<destination locationName=\"" + tscOrderCreateTemplate.getMaterialOutPoint() + "\" operation=\"NOP\" />" //AGV小车NOP
                + "<destination locationName=\"" + tscOrderCreateTemplate.getStopCarPoint() + "\" operation=\"NOP\" />" //agv到达停车点
                + "</order>"
                + "</tcsOrderSet>";
        return xml;
    }
    //第一第二工位agv单独叫料的情况(叫空车)
    public String createPlateEmptyCarOrderTemplate(String deadlineTime,TscOrderCreateTemplate tscOrderCreateTemplate){
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<tcsOrderSet>"
                + "<sceneName>" + MIDEA_SCENE_NAME + "</sceneName>"
                + "<order xsi:type=\"transport\" deadline=\"" + deadlineTime + "\" intendedVehicle=\"" + tscOrderCreateTemplate.getVerticalType() + "\" vehicleTypeAvailable=\"false\" id=\"\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                + "<destination locationName=\"" + tscOrderCreateTemplate.getPalletEmptyPoint() + "\" operation=\"OP_LIFTER_LOAD\" />" //AGV小车到达工位拉空车顶升
                + "<destination locationName=\"" + tscOrderCreateTemplate.getPalletEmptyPoint() + "\" operation=\"NOP\" />" //AGV小车到达工位拉空车
                + "<destination locationName=\"" + tscOrderCreateTemplate.getPalletEmptyPoint() + "\" operation=\"NOP\" />" //AGV小车到达工位拉空车
                + "<destination locationName=\"" + tscOrderCreateTemplate.getPalletOutPoint() + "\" operation=\"OP_LIFTER_UNLOAD\" />" //AGV把小车拉到停车点,下降
                + "<destination locationName=\"" + tscOrderCreateTemplate.getPalletOutPoint() + "\" operation=\"NOP\" />" //AGV小车到达物料点
                + "<destination locationName=\"" + tscOrderCreateTemplate.getPalletOutPoint() + "\" operation=\"NOP\" />" //AGV小车到达物料点
                + "<destination locationName=\"" + tscOrderCreateTemplate.getStopCarPoint() + "\" operation=\"NOP\" />" //agv到达停车点
                + "</order>"
                + "</tcsOrderSet>";
        return xml;
    }
    //第一第二工位agv单独叫料的情况(送物料)
    public String createPlateForMaterialTemplate(String deadlineTime,TscOrderCreateTemplate tscOrderCreateTemplate){
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<tcsOrderSet>"
                + "<sceneName>" + MIDEA_SCENE_NAME + "</sceneName>"
                + "<order xsi:type=\"transport\" deadline=\"" + deadlineTime + "\" intendedVehicle=\"" + tscOrderCreateTemplate.getVerticalType() + "\" vehicleTypeAvailable=\"false\" id=\"\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                + "<destination locationName=\"" + tscOrderCreateTemplate.getMaterialStartPoint() + "\" operation=\"WAIT:0\" />" //小车到达物料区(人工确认上料)
                + "<destination locationName=\"" + tscOrderCreateTemplate.getMaterialStartPoint() + "\" operation=\"OP_LIFTER_LOAD\" />" //AGV小车到达工位拉空车顶升
                + "<destination locationName=\"" + tscOrderCreateTemplate.getMaterialStartPoint() + "\" operation=\"NOP\" />" //AGV小车上料完成,
                + "<destination locationName=\"" + tscOrderCreateTemplate.getMaterialOutPoint() + "\" operation=\"OP_LIFTER_UNLOAD\" />" //AGV把小车拉到停车点,下降
                + "<destination locationName=\"" + tscOrderCreateTemplate.getMaterialOutPoint() + "\" operation=\"NOP\" />" //AGV小车到达物料点
                + "<destination locationName=\"" + tscOrderCreateTemplate.getMaterialOutPoint() + "\" operation=\"NOP\" />" //AGV小车到达物料点
                + "<destination locationName=\"" + tscOrderCreateTemplate.getStopCarPoint() + "\" operation=\"NOP\" />" //agv到达停车点
                + "</order>"
                + "</tcsOrderSet>";
        return xml;
    }

    /**
     * 定义处理完毕执行下一操作的指令
     * @param orderName
     * @return
     */
    public String createNextStepTcsOrderTemplate(String orderName,String locationName) {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                   + "<tcsOrderSet>"
                   + "<sceneName>" + MIDEA_SCENE_NAME + "</sceneName>"
                   + "<order xsi:type=\"transportOperationOrder\" name=\"" + orderName + "\" locationName=\""+locationName+"\" operationName=\"WAIT:0\" operationProgress=\"100\" id=\"\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" />"
                   + "</tcsOrderSet>";
        return xml;
    }

    /**
     * 获取当前的时间
     * @return
     */
    public String timeFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}
