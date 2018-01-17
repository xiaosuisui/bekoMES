package com.mj.beko.codeScanner;

import com.mj.beko.opcua.OpcUaClientException;
import com.mj.beko.opcua.OpcUaClientTemplate;
import com.prosysopc.ua.client.UaClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.opcfoundation.ua.builtintypes.NodeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Created by Administrator on 2017/7/15/015.
 */
@Component
@ConfigurationProperties(prefix = "codeScanner")
@Data
@Slf4j
public class GetBarcode {
    private String host;
    private int port;

    @Inject
    private OpcUaClientTemplate opcUaClientTemplate;

    public String getBarcode(){
        System.out.println("=============扫条码的线程：" + Thread.currentThread().getId());
        String info = null;
        Socket client = null;
        OutputStream os = null;
        PrintWriter pw = null;
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            client = new Socket(host, port);
            //向扫描仪发送扫描指令
            os = client.getOutputStream();
            pw = new PrintWriter(os);
            is = client.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            int i = 0;
            while (info == null) {
                pw.write("start");
                pw.flush();
                //接受扫描器回复的指令
                try {
                    client.setSoTimeout(1000);
                    info = br.readLine();
                }catch(SocketTimeoutException e){
                    log.error("scaner can get barCode！");
                }
                log.info("条形码数据：" + info);
                i++;
                if(i >= 50){
                    log.info("50次未读，报警！！！！");
                    pw.write("stop");
                    pw.flush();
                    break;
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
                isr.close();
                is.close();
                pw.close();
                os.close();
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (info == null) {
                    //控制PLC检测为空托盘时的放行
                    UaClient uaClient = opcUaClientTemplate.getUaClientList().get(0);
                    NodeId fxNode = new NodeId(3, "\"information\".\"Scan_Finish\"");
                    try {
                        opcUaClientTemplate.writeNodeValue(uaClient, fxNode, 1);
                    } catch (OpcUaClientException e) {
                        e.printStackTrace();
                    }
                }
                return info;
            }
        }
    }

    public String getBarcode1(){
        log.info("=============扫条码1的线程：" + Thread.currentThread().getId());
        String info = null;
        Socket client = null;
        OutputStream os = null;
        PrintWriter pw = null;
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            client = new Socket("10.114.21.184", port);
            //向扫描仪发送扫描指令
            os = client.getOutputStream();
            pw = new PrintWriter(os);
            is = client.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            int i = 0;
            while (info == null) {
                pw.write("start");
                pw.flush();
                //接受扫描器回复的指令
                try {
                    client.setSoTimeout(1000);
                    info = br.readLine();
//                    if (info != null) {
//                        info += br.readLine();
//                    }
                }catch(SocketTimeoutException e){
                    log.error("扫描枪未扫到条形码数据！");
                }
                log.info("条形码数据：" + info);
                i++;
                if(i >= 15){
                    log.info("15次未读到，报警！！！！");
                    pw.write("stop");
                    pw.flush();
                    break;
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
                isr.close();
                is.close();
                pw.close();
                os.close();
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                return info;
            }
        }
    }

    public String getBarcode2(){
        System.out.println("=============扫条码2的线程：" + Thread.currentThread().getId());
        String info = null;
        Socket client = null;
        OutputStream os = null;
        PrintWriter pw = null;
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            client = new Socket("10.114.21.178", port);
            //向扫描仪发送扫描指令
            os = client.getOutputStream();
            pw = new PrintWriter(os);
            is = client.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            int i = 0;
            while (info == null) {
                pw.write("start");
                pw.flush();
                //接受扫描器回复的指令
                try {
                    client.setSoTimeout(1000);
                    info = br.readLine();
                }catch(SocketTimeoutException e){
                    log.error("扫描枪未扫到条形码数据！");
                }
                log.info("条形码数据：" + info);
                i++;
                if(i >= 15){
                    log.info("15次未读，报警！！！！");
                    pw.write("stop");
                    pw.flush();
                    break;
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
                isr.close();
                is.close();
                pw.close();
                os.close();
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                return info;
            }
        }
    }

    public String getBarcode3(){
        log.info("=============扫条码3的线程：" + Thread.currentThread().getId());
        String info = null;
        Socket client = null;
        OutputStream os = null;
        PrintWriter pw = null;
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            client = new Socket("10.114.21.179", port);
            //向扫描仪发送扫描指令
            os = client.getOutputStream();
            pw = new PrintWriter(os);
            is = client.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            int i = 0;
            while (info == null) {
                pw.write("start");
                pw.flush();
                //接受扫描器回复的指令
                try {
                    client.setSoTimeout(1000);
                    info = br.readLine();
//                    if (info != null) {
//                        info += br.readLine();
//                    }
                }catch(SocketTimeoutException e){
                    log.error("package扫描枪未扫到条形码数据！");
                }
                log.info("条形码数据：" + info);
                i++;
                if(i >= 15){
                    log.info("15次未读，报警！！！！");
                    pw.write("stop");
                    pw.flush();
                    break;
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
                isr.close();
                is.close();
                pw.close();
                os.close();
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                return info;
            }
        }
    }
    public String getBottomPlateBarCode(){
       log.info("=============扫条码bottomPlate的线程：" + Thread.currentThread().getId());
        String info = null;
        Socket client = null;
        OutputStream os = null;
        PrintWriter pw = null;
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            client = new Socket("10.114.21.181", port);
            //向扫描仪发送扫描指令
            os = client.getOutputStream();
            pw = new PrintWriter(os);
            is = client.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            int i = 0;
            while (info == null) {
                pw.write("start");
                pw.flush();
                //接受扫描器回复的指令
                try {
                    client.setSoTimeout(1000);
                    info = br.readLine();
//                    if (info != null) {
//                        info += br.readLine();
//                    }
                }catch(SocketTimeoutException e){
                    log.error("bottomplate扫描枪未扫到条形码数据！");
                }
                log.info("条形码数据：" + info);
                i++;
                if(i >= 20){
                    log.info("15次未读，报警！！！！");
                    pw.write("stop");
                    pw.flush();
                    break;
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
                isr.close();
                is.close();
                pw.close();
                os.close();
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                return info;
            }
        }
    }
}
