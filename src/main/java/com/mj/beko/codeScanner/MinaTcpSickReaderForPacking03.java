package com.mj.beko.codeScanner;

import com.mj.beko.domain.ProductCode;
import com.mj.beko.opcua.OpcUaClientTemplate;
import com.mj.beko.service.ProductCodeService;
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
public class MinaTcpSickReaderForPacking03 extends IoHandlerAdapter {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private OpcUaClientTemplate opcUaClientTemplate;
    @Autowired
    private SimpMessagingTemplate template;
    @Autowired
    private ProductCodeService productCodeService;
    @Autowired
    private MinaTcpSickReaderForPacking01 minaTcpSickReaderForPacking01;
    @Autowired
    private MinaTcpSickReaderForPacking02 minaTcpSickReaderForPacking02;
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
            ConnectFuture connFuture = connector.connect(new InetSocketAddress("10.114.21.220", 2112));
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
    public void messageReceived(IoSession session, Object message) {
        IoBuffer bbuf = (IoBuffer) message;
        byte[] byten = new byte[bbuf.limit()];
        bbuf.get(byten, bbuf.position(), bbuf.limit());
        String msgStr =new String(byten);
        //终止扫描
        if(msgStr!=null && msgStr.length()>10){
            log.info("read printer03 Label value:::::::"+msgStr);
            //拿到了EPS条码,数据库中查询对应的serialNumber
            ProductCode productCode=productCodeService.getProductCodeByEpsCode(msgStr);
            //把eps的序列号放在缓存里
            redisTemplate.opsForValue().set("matchDbEpsCodeSerialNumber",productCode.getSerialNo());
            log.info("have got eps code,call another two scanner");
            //调用另外两个扫描枪,
            log.info("call scanner 02");
            minaTcpSickReaderForPacking01.connect();
            try {
                minaTcpSickReaderForPacking01.startsick();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("call scanner 03");
            minaTcpSickReaderForPacking02.connect();
            try {
                minaTcpSickReaderForPacking02.startsick();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //
            try {
                stopSick();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //close session
            connector.dispose();
            session.closeNow();
            session.closeOnFlush();
        }
    }
    public void startsick() throws IOException, InterruptedException {
        Socket socket =new Socket("10.114.21.222",2112);
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
        Socket socket =new Socket("10.114.21.222",2112);
        PrintWriter writer =new PrintWriter(socket.getOutputStream());
        writer.write("stop");
        writer.flush();
        socket.close();
    }
}
