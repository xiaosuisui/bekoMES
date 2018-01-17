package com.mj.beko;

import java.io.*;
import java.net.Socket;

/**
 * Created by Administrator on 2017/11/27.
 */
public class Test11 {
    public static void main(String[] args) {
        String str =
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                        +"<tcsOrderSet>"
                        +"<sceneName>abcdef</sceneName>"
                        +"<order xsi:type=\"transportWithdraw\" name=\"TO-2017-12-29-07-47-48-303\" disableVehicle=\"false\" forceStop=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>"
                        +"</tcsOrderSet>";

        try {
            //1、创建客户端Socket，指定服务器地址和端口
            //Socket socket = new Socket("192.168.6.112",55555);
            Socket socket = new Socket("10.114.0.118",55555);
            //2、获取输出流，向服务器 端发送信息
            OutputStream os = socket.getOutputStream();//字节输出流
            PrintWriter pw = new PrintWriter(os);//将输出流包装成打印流
            pw.write(str);
            pw.flush();
            socket.shutdownOutput();
            //3、获取输入流，并读取服务器端的响应信息
            InputStream is;
            is = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String info = null;
            while((info=br.readLine()) != null){
                System.out.println("我是客户端，服务器说："+info);
            }

            //4、关闭资源
            br.close();
            is.close();
            pw.close();
            os.close();
            socket.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();



        }

    }
}
