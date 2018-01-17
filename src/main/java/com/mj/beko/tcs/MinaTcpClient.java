package com.mj.beko.tcs;

import com.mj.beko.domain.TcsOrder;
import com.mj.beko.service.TcsOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Ricardo on 2017/12/8.
 * 监听44444端口，接收日志消息
 */
@Slf4j
@Service
public class MinaTcpClient extends IoHandlerAdapter {

    @Inject
    private TcsOrderService tcsOrderService;

    @Inject
    private MessageHandler messageHandler;

    @Inject
    private TaskExecutor taskExecutor;

    private static StringBuilder comboMsg = new StringBuilder(); //处理接收消息不全,用来拼凑消息

    private IoConnector connector = new NioSocketConnector();
    private static IoSession session;
    @PostConstruct
    public void start() {
        taskExecutor.execute(() -> connect());
    }

    @Async("taskExecutor")
    public void connect(){
        log.info("execute connection");
        if(connector.isDisposed() || connector.isDisposing()){
            log.info("connection connctor is Disposed");
            connector = new NioSocketConnector();
        }
        try{
            connector.setConnectTimeoutMillis(30000); //设置连接超时
            connector.getSessionConfig().setMaxReadBufferSize(10240);//设置缓冲区大小
            if(!connector.isActive()){
                connector.setHandler(this);
            }
            DefaultIoFilterChainBuilder chain = connector.getFilterChain();
            /*chain.addLast("exceutor", new ExecutorFilter());*/
            ConnectFuture connFuture = connector.connect(new InetSocketAddress("10.114.0.118", 44444));
            connFuture.awaitUninterruptibly();
            session = connFuture.getSession();
            log.info("TCP have start success");
        }catch (Exception e){
            e.printStackTrace();
            log.info("minnaTcp Error,{}",e.getMessage());
            log.info("tcp need to restart");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            connect();
        }
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        log.info("session is create");
        super.sessionCreated(session);
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        log.info("session is open");
        super.sessionOpened(session);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        log.info("session closed");
        Thread.sleep(2000);
        session.closeNow();
        session.closeOnFlush();
        connect();
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        log.info("session idle");
        super.sessionIdle(session, status);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        log.info("exception");
        Thread.sleep(2000);
        connect();
        super.exceptionCaught(session, cause);
    }

    @Override
    public synchronized void messageReceived(IoSession session, Object message) throws Exception {
        log.info("message receive");
        IoBuffer bbuf = (IoBuffer) message;
        byte[] byten = new byte[bbuf.limit()];
        bbuf.get(byten, bbuf.position(), bbuf.limit());
        String msgStr =new String(byten);
        log.debug("接收到的未处理的数据为：" + msgStr);
        String[] msgs = msgStr.split("\\|");
        for (String msg : msgs) {
            if (checkStatusMessageSet(msg)) {
                log.info("解析到完整的msg::::" + msg);
                doHandleMessage(msg);
            } else {
                comboMsg.append(msg);
                comboMsg.delete(0,comboMsg.length());
/*                if (comboMsg.toString().contains("statusMessageSet>")) {
                    if (comboMsg.toString().startsWith("<?")){
                        log.info("拼凑出来的xml,{}",comboMsg.toString());
                        doHandleMessage(comboMsg.toString());
                    }
                    comboMsg.delete(0, comboMsg.length());
                }*/
            }
        }
    }

    /**
     * 判断读到的xml日志中,是否包含完整的xml节点
     * @param msg
     * @return
     */
    private boolean checkStatusMessageSet(String msg) {
        return msg != null && msg.startsWith("<?") && msg.contains("statusMessageSet>");
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        log.info("message Sent");
        super.messageSent(session, message);
    }

    @Override
    public void inputClosed(IoSession session) throws Exception {
        log.info("input Closed");
        super.inputClosed(session);
    }

    //处理监听到的xml文件,44444端口接收的不只是订单，还会接收小车状态的判断,核心的调度方法
    private void doHandleMessage(String msg) {
        //判断该msg中是否为完整的msg,用于避免某些情况下的并发.
        int countStatusMessaageSet=StrPatternUtil.count(msg,Pattern.compile("statusMessageSet"));
        if(countStatusMessaageSet>2){
            log.info("check 到了一次拼凑出来的xml并发了,return,xml,{}",msg);
            return;
        }
        //增加一些判断条件
        int xmlnsCount =StrPatternUtil.count(msg,Pattern.compile("xmlns"));
        if(xmlnsCount>2){
            log.info("check 到了一次拼凑出来的xml并发了,return,xml xmlCount,{}",msg);
            return;
        }
//        log.debug("接收到的消息::::::::::::" + msg);
        //获取当前处理的订单号和订单状态
        String orderName = StrPatternUtil.matchStr(msg, Pattern.compile("orderName=\"(.*?)\""));
        String orderState = StrPatternUtil.matchStr(msg, Pattern.compile("orderState=\"(.*?)\""));
        //从数据库中根据订单名获取订单详情
        List<TcsOrder> tcsOrderList = tcsOrderService.findAllByTcsOrderName(orderName);
        if(tcsOrderList==null) return;
        TcsOrder tcsOrder = null;
        String currentState = OrderState.CREATESTATUS;
        if(tcsOrderList != null && tcsOrderList.size() > 0){
            tcsOrder = tcsOrderList.get(0);
            currentState = tcsOrder.getState();
        }else{
            return;
        }
        //订单处理失败的情况
        if (OrderState.FAILED.equals(orderState)) {
            messageHandler.handlerFailureCome(); //解析到订单状态为完成，并且数据库当前状态为下料完成
        } else if (OrderState.FINISHED.equals(orderState) && currentState.equals(OrderState.UNLOADINGSTATUS)) {
            //订单完成并且数据库中订单状态为下料的情况
            log.info("finished:::::::::::::::::::::::::::;");
            messageHandler.handleFinishedCome(orderName);
        } else {
            //订单过程中,接收小车运行过程中的几个停车点.
            //此处的逻辑是判断统计当前的xml日志中operation为"WAIT:0" and state="FINISHED"的次数
            int count1 = StrPatternUtil.count(msg, Pattern.compile("operation=\"WAIT:0\" state=\"FINISHED\""));
            int count2 = StrPatternUtil.count(msg, Pattern.compile("operation=\"NOP\" state=\"FINISHED\""));
            int count = count1 + count2;
            //解析出最后一个wait:0 finish 状态 对应的 locationName
            //表示小车到达工位点运输点,运输空托盘,并且状态未被处理（0 status）
            String locationNameStr="";
            locationNameStr=StrPatternUtil.matchStr(msg, Pattern.compile("locationName=\"(.*?)\" operation=\"WAIT:0\" state=\"FINISHED\"" ));
            if(locationNameStr==""){
                locationNameStr=StrPatternUtil.matchStr(msg, Pattern.compile("locationName=\"(.*?)\" operation=\"NOP\" state=\"FINISHED\"" ));
                if(locationNameStr=="")
                    return;
            }
            String[] locationNames=locationNameStr.split("locationName=");
            String locationName=locationNames[locationNames.length-1];
            if (count == 1 && OrderState.CREATESTATUS.equals(currentState)) {
                log.info("监听到了第一次停车点,订单号为{}", orderName);
                //获取当前正在执行的小车
                String executingVehicle = StrPatternUtil.matchStr(msg, Pattern.compile("executingVehicle=\"(.*?)\""));
                String functionType = tcsOrder.getFunctionType(); //可以根据此处来判断当前的订单类型（EPS- 滚筒线--流利架子--空车）
                //当操作为"叫料"的情况
                if (OrderState.GUNTONG.equals(functionType)) {
                    messageHandler.handlerCallMaterialFirstCome(orderName, executingVehicle, count,locationName);
                }
                if(OrderState.EPSUP.equals(functionType)){
                    messageHandler.handlerEpsupFirstCome(orderName,executingVehicle,count);
                }
                if(OrderState.EPSDOWN.equals(functionType)){
                    messageHandler.handlerEpsdownFirstCome(orderName,executingVehicle,count);

                }if(OrderState.LIULIJIA.equals(functionType)){
                    taskExecutor.execute(() -> messageHandler.handlerFLiujijiaFirstCome(orderName,executingVehicle,count, locationName));
                }
                //表明第一第二工位叫料
                if(OrderState.BOTTOMANDTOPPLATE.equals(functionType)){
                    messageHandler.handlerBottomPlateCarFirstCome(orderName,executingVehicle,count);
                }
                //如果第一第二工位叫空车
                if(OrderState.BOTTOMPLATEFOREMPTYCAR.equals(functionType)){
                    messageHandler.hanlderBottomPlateForEmptyCarFirstCome(orderName,executingVehicle,count);
                }
                //如果第一第二工位叫料单独
                if(OrderState.BOTTOMPLATEFORMATERIAL.equals(functionType)){
                    messageHandler.hanlderBottomPlateForMaterialCarFirstCome(orderName,executingVehicle,count);
                }
                //表明当前是滚筒单独运输空托盘
                if(OrderState.SCROLLEMPTY.equals(functionType)){
                    messageHandler.handlerScrollerEmptyFirstCome(orderName,executingVehicle,count,locationName);
                }
                //表明是当前滚筒单独上料
                if (OrderState.SCROLLMATERIAL.equals(functionType)){
                    messageHandler.handlerMaterialFirstCome(orderName,executingVehicle,count,locationName);
                }
            } else if (count == 2 && OrderState.PALLETEMPTY.equals(currentState)) { //如果为第二次停车,并且上个状态为空托盘上AGV(到达空托盘下料区)
                //表示小车到达endPoint点(下料区),并且状态未被处理(1 status)
                log.info("监听到了第二次停车点,订单号为{}", orderName);
                //获取当前正在执行的小车,此处分开写是因为不满足条件时不用执行解析
                String executingVehicle = StrPatternUtil.matchStr(msg, Pattern.compile("executingVehicle=\"(.*?)\""));
                //判断当前订单的类型是callMaterial还是callEmptyPallet,如果是叫料
                String functionType = tcsOrder.getFunctionType();
                //当操作为"叫料"的情况
                if(OrderState.GUNTONG.equals(functionType)){
                    messageHandler.handlerCallMaterialSecCome(orderName, executingVehicle, count,locationName);
                }
                if(OrderState.EPSUP.equals(functionType)){
                    messageHandler.handlerEpsUpSecondCome(orderName,executingVehicle,count);
                }
                if(OrderState.EPSDOWN.equals(functionType)){
                    messageHandler.handlerEpsdownSecondCome(orderName,executingVehicle,count);
                }
                if(OrderState.LIULIJIA.equals(functionType)){
                    taskExecutor.execute(() -> messageHandler.handlerLiulijiaSecondCome(orderName,executingVehicle,count, locationName));
                }
                //表明第一第二工位叫料(第二次到位)
                if(OrderState.BOTTOMANDTOPPLATE.equals(functionType)){
                    messageHandler.handlerBottomPlateSecCome(orderName,executingVehicle,count);
                }
                //如果第一第二工位叫空车
                if(OrderState.BOTTOMPLATEFOREMPTYCAR.equals(functionType)){
                    messageHandler.hanlderBottomPlateForEmptyCarSecondCome(orderName,executingVehicle,count);
                }
                //如果第一第二工位叫料单独
                if(OrderState.BOTTOMPLATEFORMATERIAL.equals(functionType)){
                    messageHandler.hanlderBottomPlateForMaterialCarSecondCome(orderName,executingVehicle,count);
                }
                //滚筒线运输空托盘，第二次停车
                if(OrderState.SCROLLEMPTY.equals(functionType)){
                    messageHandler.handlerEpsdownSecondCome(orderName,executingVehicle,count);
                }
                if(OrderState.SCROLLMATERIAL.equals(functionType)){
                    messageHandler.handlerMaterialSecondCome(orderName,executingVehicle,count);
                }
            } //判断第三次停车情况
            else if (count == 3 && OrderState.PALLETOUT.equals(currentState)) { //如果为第三次次停车,并且上个状态为到达空托盘下料区(到达物料上料区)
                //表示小车到达到达物料上料区,并且状态未被处理(2 status)
                log.info("监听到了第三次停车点,订单号为{}", orderName);
                String executingVehicle = StrPatternUtil.matchStr(msg, Pattern.compile("executingVehicle=\"(.*?)\""));
                //判断当前订单的类型是callMaterial还是callEmptyPallet,如果是叫料
                String functionType = tcsOrder.getFunctionType();
                //当操作为"叫料"的情况
                if(OrderState.GUNTONG.equals(functionType)){
                    messageHandler.handlerCallMaterialthirdCome(orderName, executingVehicle, count,locationName);
                }
                if(OrderState.EPSUP.equals(functionType)){
                    messageHandler.handlerEpsUpThirdCome(orderName,executingVehicle,count);
                }
                if(OrderState.EPSDOWN.equals(functionType)){
                    messageHandler.handlerdEpsownThirdCome(orderName,executingVehicle,count,locationName);
                }
                if(OrderState.LIULIJIA.equals(functionType)){
                    messageHandler.handlerLiulijiaThirdCome(orderName,executingVehicle,count);
                }
                //表明第一第二工位叫料(第三次到位)
                if(OrderState.BOTTOMANDTOPPLATE.equals(functionType)){
                    messageHandler.handlerBottomPlateThirdCome(orderName,executingVehicle,count);
                }
                //如果第一第二工位叫空车
                if(OrderState.BOTTOMPLATEFOREMPTYCAR.equals(functionType)){
                    messageHandler.hanlderBottomPlateForEmptyCarThirdCome(orderName,executingVehicle,count);
                }
                //如果第一第二工位叫料单独
                if(OrderState.BOTTOMPLATEFORMATERIAL.equals(functionType)){
                    messageHandler.hanlderBottomPlateForMaterialCarThirdCome(orderName,executingVehicle,count);
                }
                //滚筒线空托盘的第三次停车()
                if(OrderState.SCROLLEMPTY.equals(functionType)){
                    messageHandler.handlerScrollerThirdCome(orderName,executingVehicle,count,locationName);
                }
                if(OrderState.SCROLLMATERIAL.equals(functionType)){
                    messageHandler.handlerMaterialThirdCome(orderName,executingVehicle,count);
                }
            }
            else if (count == 4 && OrderState.FEEDINGSTATUS.equals(currentState)) { //如果为第四次停车,并且上个状态为物料上料区(到达物料下料区)
                //表示小车到达endPoint点(下料区),并且状态未被处理(3 status)
                log.info("监听到了第四次停车点,订单号为{}", orderName);
                //获取当前正在执行的小车,此处分开写是因为不满足条件时不用执行解析
                String executingVehicle = StrPatternUtil.matchStr(msg, Pattern.compile("executingVehicle=\"(.*?)\""));
                //判断当前订单的类型是callMaterial还是callEmptyPallet,如果是叫料
                String functionType = tcsOrder.getFunctionType();
                //当操作为"叫料"的情况
                if(OrderState.GUNTONG.equals(functionType)){
                    messageHandler.handlerCallMaterialFourTime(orderName, executingVehicle, count,locationName);
                }
                if(OrderState.EPSUP.equals(functionType)){
                    messageHandler.handlerEpsUpFourthCome(orderName,executingVehicle,count);
                }
                if(OrderState.EPSDOWN.equals(functionType)){
                    messageHandler.handlerEpsdownFourthCome(orderName,executingVehicle,count);
                }
                if(OrderState.LIULIJIA.equals(functionType)){
                    messageHandler.handlerLiulijiaFourCome(orderName,executingVehicle,count);
                }
                //表明第一第二工位叫料(第四次到位)
                if(OrderState.BOTTOMANDTOPPLATE.equals(functionType)){
                    messageHandler.handlerBottomPlateFourthCome(orderName,executingVehicle,count);
                }
                //如果第一第二工位叫空车
                if(OrderState.BOTTOMPLATEFOREMPTYCAR.equals(functionType)){
                    messageHandler.hanlderBottomPlateForEmptyCarFourthCome(orderName,executingVehicle,count);
                }
                //如果第一第二工位叫料单独
                if(OrderState.BOTTOMPLATEFORMATERIAL.equals(functionType)){
                    messageHandler.hanlderBottomPlateForMaterialCarFourthCome(orderName,executingVehicle,count);
                }
                //滚筒线叫空托盘的第四次停车
                if(OrderState.SCROLLEMPTY.equals(functionType)){
                    messageHandler.handlerScrollerFourthCome(orderName,executingVehicle,count);
                }
                if(OrderState.SCROLLMATERIAL.equals(functionType)){
                    messageHandler.handlerMaterialFouthCome(orderName,executingVehicle,count);
                }
            }
        }
    }
    /*查询当前调拨单的状态*/
    public String getCurrentSate(String name) {
        List<TcsOrder> tcsOrderList = tcsOrderService.findAllByTcsOrderName(name);
        if(tcsOrderList != null && tcsOrderList.size() > 0){
            return tcsOrderList.get(0).getState();
        }
        return "0";
    }
}
