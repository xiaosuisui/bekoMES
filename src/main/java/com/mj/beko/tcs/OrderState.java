package com.mj.beko.tcs;

/**
 * Created by jc on 2017/8/2.
 * 定义tcsOrder的状态常量
 */
public  interface OrderState {

    //################定义tcsOrderj解析中的常量##########################
    String BEING_PROCESSED="BEING_PROCESSED";     // 正在处理
    String FINISHED="FINISHED";                     // 处理完成
    String RAW="RAW";                               // 未处理
    String ACTIVE ="ACTIVE";                       // 就绪
    String DISPATCHABLE="DISPATCHABLE";           // 未派遣
    String UNROUTABLE ="UNROUTABLE";              // 路线不可规划
    String  WITHDRAW="WITHDRAW";                   // 撤销订单
    String FAILED ="FAILED";                        // 订单失败
    //###########################定义数据库中的状态常量###################
    String CREATESTATUS="0";//tcsOrder create
    String PALLETEMPTY="1";//agv到达空托盘点
    String PALLETOUT="2";//agv到达空托盘下料点
    String FEEDINGSTATUS="3";//上料状态
    String UNLOADINGSTATUS="4";//下料状态
    String FIINISHEDSTATUS="5";//完成状态
    //#########定义调度单的类型###########################################
    String CALLMATERIALTYPE="CallMaterial";//表示线边的工位叫料
    String CALLEMPTYTYPE="CallEmptyPallet";//表示叫空托盘类型
    String GUNTONG="GUNTONG";//表示线边的工位叫料
    String LIULIJIA="LIULIJIA";//流利架
    String EPSUP="EPSUP"; // 上泡沫
    String EPSDOWN="EPSDOWN";//下泡沫
    String SCROLLEMPTY="SCROLLEMPTY";//滚筒空托盘
    String SCROLLMATERIAL="SCROLLMATERIAL";//滚筒上料
    String BOTTOMANDTOPPLATE="BOTTOMANDTOPPLATE";//第一第二工位叫料
    String BOTTOMPLATEFOREMPTYCAR="BOTTOMPLATEFOREMPTYCAR";//第一第二工位叫空车
    String BOTTOMPLATEFORMATERIAL="BOTTOMPLATEFORMATERIAL";//第一第二工位叫料
}
