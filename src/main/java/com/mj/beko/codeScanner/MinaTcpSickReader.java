package com.mj.beko.codeScanner;

import com.mj.beko.listener.TypeCodePublisher;
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

import javax.inject.Inject;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ricardo on 2018/1/11.
 */
@Slf4j
@Service
public class MinaTcpSickReader extends IoHandlerAdapter {
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
            ConnectFuture connFuture = connector.connect(new InetSocketAddress("10.114.21.216", 2112));
           /* connFuture.awaitUninterruptibly();*/
            session = connFuture.getSession();
            System.out.println("printer01 label TCP start");
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
       // connect();
/*        super.exceptionCaught(session, cause);*/
    }
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        IoBuffer bbuf = (IoBuffer) message;
        byte[] byten = new byte[bbuf.limit()];
        bbuf.get(byten, bbuf.position(), bbuf.limit());
        String msgStr =new String(byten);
        //终止扫描
        if(msgStr!=null && msgStr.length()>10){
            log.info("read printer Label value:::::::"+msgStr);
            //把读到的结果放到匹配的缓存中,只做读取的工作,不做匹配逻辑
            ValueOperations valueOperations=redisTemplate.opsForValue();
            valueOperations.set("matchSerialNumber",msgStr);
            Map<String,String> map =new HashMap<String,String>();
            map.put("readSerialNumber",msgStr);
            template.convertAndSend("/topic/lineLeaderScreen/readSerialNumber",map);
            //读到typeCode后匹配是否一致
            String currentSerialNumber=valueOperations.get("currentSerialNumber").toString();
            log.info("currentSerial is {}",currentSerialNumber);
            //如果不匹配
            if(!msgStr.contains(currentSerialNumber)){
                Map<String,String> infoMap =new HashMap<String,String>();
                infoMap.put("result","MatchError");
                infoMap.put("type","6");
                infoMap.put("reason","serial cant match,db value is"+currentSerialNumber+"cache serial value is"+msgStr);
                template.convertAndSend("/topic/lineLeaderScreen/matchError",infoMap);
                return;
            }

            UaClient uaClient = opcUaClientTemplate.getUaClientList().get(2);
            log.info("start to info robot scanner03 work ok,{}",msgStr);
            NodeId printer01Node = new NodeId(3, "\"ITread\".\"Robot_release_Barcode_3\"");
            try {
                //如果发成功了,则给plc信号,hi 你可以抓取了。往节点写1值
                //写2次
                log.info("read serialNumber,start write value to plc to go,{}",msgStr);
                boolean flag1 = opcUaClientTemplate.writeNodeValue(uaClient, printer01Node, 1);
                if(!flag1){
                    log.info("write second to info robot scanner03 work ok,{}",msgStr);
                    boolean flag2 = opcUaClientTemplate.writeNodeValue(uaClient, printer01Node, 1);
                }
                log.info("info robot scanner3 work ok,{}",msgStr);

            }catch (Exception e){
                log.error("write no to robot scanner03 failure,{}",msgStr);
            }
          stopSick();
          //close session
            connector.dispose();
            session.closeNow();
            session.closeOnFlush();
        }
    }
    public void startsick() throws IOException, InterruptedException {
        Socket socket =new Socket("10.114.21.216",2112);
        OutputStream out =socket.getOutputStream();
        PrintWriter writer =new PrintWriter(out);
        //write once
        log.info("write start,again");
        writer.write("start");
        writer.flush();
        out.close();
        writer.close();
/*        for(int i=0;i<1;i++){
           if(!flag){
                stopSick();
                socket.close();
            }
            if(flag){
                    log.info("write start,again");
                    writer.write("start");
                    writer.flush();
                    out.close();
                    writer.close();
            }
        }*/
        socket.close();
    }
    public void stopSick() throws IOException {
        Socket socket =new Socket("10.114.21.216",2112);
        PrintWriter writer =new PrintWriter(socket.getOutputStream());
        writer.write("stop");
        writer.flush();
        socket.close();
    }

}
