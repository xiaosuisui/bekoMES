package com.mj.beko.codeScanner;

import com.mj.beko.opcua.OpcUaClientTemplate;
import com.prosysopc.ua.client.UaClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.opcfoundation.ua.builtintypes.NodeId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ricardo on 2018/1/15.
 */
@Service
@Slf4j
public class MinaTcpSickReaderForPacking01 extends IoHandlerAdapter {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private OpcUaClientTemplate opcUaClientTemplate;
    @Autowired
    private SimpMessagingTemplate template;
    private IoConnector connector = new NioSocketConnector();
    private static IoSession session;
    public void connect(){
        if(connector.isDisposed()||connector.isDisposing()){
            connector=new NioSocketConnector();
        }
        try{
            connector.setConnectTimeoutMillis(3000); //设置连接超时
            connector.getSessionConfig().setMaxReadBufferSize(1000);//设置缓冲区大小
            if(!connector.isActive()){
                connector.setHandler(this);
            }
            ConnectFuture connFuture = connector.connect(new InetSocketAddress("10.114.21.222", 2112));
           /* connFuture.awaitUninterruptibly();*/
            session = connFuture.getSession();
            System.out.println("package01 scanner TCP start");
        }catch (Exception e){
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            connect();
        }
    }
    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        super.messageSent(session, message);
    }
    @Override
    public void sessionCreated(IoSession session) throws Exception {
        log.info("session01 is create");
        super.sessionCreated(session);
    }
    @Override
    public void sessionOpened(IoSession session) throws Exception {
        log.info("session01 is open");
        super.sessionOpened(session);
    }
    @Override
    public void sessionClosed(IoSession session) throws Exception {
        log.info("session01 closed");
        Thread.sleep(2000);
        // connect();
    }
    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        super.sessionIdle(session, status);
    }
    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        session.closeNow();
        Thread.sleep(2000);
    }
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        IoBuffer bbuf = (IoBuffer) message;
        byte[] byten = new byte[bbuf.limit()];
        bbuf.get(byten, bbuf.position(), bbuf.limit());
        String msgStr =new String(byten);
        //终止扫描
        if(msgStr!=null && msgStr.length()>10){
            log.info("get printer03 label,value is{}",msgStr);
            //缓存中取出serialNumber
            ValueOperations valueOperations=redisTemplate.opsForValue();
            String matchDbEpsCodeSerialNumber=valueOperations.get("matchDbEpsCodeSerialNumber").toString();
            //如果不匹配
            if(!msgStr.contains(matchDbEpsCodeSerialNumber)){
                Map<String,String> map =new HashMap<String,String>();
                map.put("result","matchError");
                map.put("type","14");
                map.put("reason","package01 serial not match,value is"+matchDbEpsCodeSerialNumber+","+msgStr);
                template.convertAndSend("/topic/lineLeaderScreen/lastPrinterError",map);
                log.info("package01 serial not match,value is {}{}",msgStr,matchDbEpsCodeSerialNumber);
                return;
            }
            //如果匹配
            String matchResult=matchDbEpsCodeSerialNumber+"OK";
            valueOperations.set("package01MatchResult",matchResult);
            //从缓存中获取package02 的 匹配结果serialNumber
           String package02MatchResult= valueOperations.get("package02MatchResult").toString();
            //如果package02的结果一致,则写plc，哥们你可以go
            if(matchResult.equals(package02MatchResult)){
                UaClient uaClient = opcUaClientTemplate.getUaClientList().get(2);
                log.info("start to info package01 work ok,{}",msgStr);
                //
                NodeId printer01Node = new NodeId(3, "\"ITread\".\"pacakge01_go\"");
                try {
                    //如果发成功了,则给plc信号,hi 你可以抓取了。往节点写1值
                    //写2次
                    log.info("match package01,start write value to plc to go,{}",msgStr);
                    boolean flag1 = opcUaClientTemplate.writeNodeValue(uaClient, printer01Node, 1);
                    if(!flag1){
                        log.info("write second match package01,start write value to plc to go,,{}",msgStr);
                        boolean flag2 = opcUaClientTemplate.writeNodeValue(uaClient, printer01Node, 1);
                    }
                    log.info("info package01work ok,{}",msgStr);
                }catch (Exception e){
                    log.error("write no to package01 failure,{}",msgStr);
                }
            }
            stopSick();
            //close session
            connector.dispose();
            session.closeNow();
            session.closeOnFlush();
        }
    }
    public void startsick() throws IOException, InterruptedException {
        Socket socket =new Socket("10.114.21.221",2112);
        OutputStream out =socket.getOutputStream();
        PrintWriter writer =new PrintWriter(out);
        //write once
        log.info("write package02 scanner start,again");
        writer.write("start");
        writer.flush();
        out.close();
        writer.close();
        socket.close();
    }
    public void stopSick() throws IOException {
        Socket socket =new Socket("10.114.21.221",2112);
        PrintWriter writer =new PrintWriter(socket.getOutputStream());
        writer.write("stop");
        writer.flush();
        socket.close();
    }
}
