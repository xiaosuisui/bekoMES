package com.mj.beko.service.impl;

import com.mj.beko.service.IPAndMACService;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by MOUNTAIN on 2017/7/18.
 */
@Service
@ConfigurationProperties()
public class IPAndMACServiceImpl implements IPAndMACService {
    public static final String MAC_ADDRESS_PREFIX01 = "MAC Address = ";
    public static final String MAC_ADDRESS_PREFIX02 = "MAC 地址 = ";
    public static final String LOOPBACK_ADDRESS = "127.0.0.1";
    public static final String IPv6Address = "0:0:0:0:0:0:0:1";

    private Map<String, String> stationScreenAndIP = new HashMap<>();

    public Map<String, String> getStationScreenAndIP() {
        return stationScreenAndIP;
    }

    public void setStationScreenAndIP(Map<String, String> stationScreenAndIP) {
        this.stationScreenAndIP = stationScreenAndIP;
    }

    /**
     * 通过HttpServletRequest返回IP地址
     *
     * @param request HttpServletRequest
     * @return ip String
     * @throws Exception
     */
    public String getIpAddr(HttpServletRequest request) throws Exception {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        //如果使用localhost访问，对于windows IPv6会返回0:0:0:0:0:0:0:1，将其转为127.0.0.1
        if (IPv6Address.equals(ip)) {
            ip = LOOPBACK_ADDRESS;
        }
        return ip;
    }


    /**
     * 通过IP地址获取MAC地址
     *
     * @param ip String,127.0.0.1格式
     * @return mac String
     * @throws Exception
     */
    public String getMACAddress(String ip) throws Exception {
        String line = "";
        String macAddress = "";
        //如果为127.0.0.1,则获取本地MAC地址。
        if (LOOPBACK_ADDRESS.equals(ip)) {
            InetAddress inetAddress = InetAddress.getLocalHost();
            byte[] mac = NetworkInterface.getByInetAddress(inetAddress).getHardwareAddress();
            //下面代码是把mac地址拼装成String
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                if (i != 0) {
                    sb.append("-");
                }
                //mac[i] & 0xFF 是为了把byte转化为正整数
                String s = Integer.toHexString(mac[i] & 0xFF);
                sb.append(s.length() == 1 ? 0 + s : s);
            }
            //把字符串所有小写字母改为大写成为正规的mac地址并返回
            macAddress = sb.toString().trim().toUpperCase();
            return macAddress;
        }
        //获取非本地IP的MAC地址
        Process p = Runtime.getRuntime().exec("nbtstat -A " + ip);
        InputStreamReader isr = new InputStreamReader(p.getInputStream(), "GBK");
        BufferedReader br = new BufferedReader(isr);
        while ((line = br.readLine()) != null) {
            if (line != null) {
                if (line.contains(MAC_ADDRESS_PREFIX01)) {
                    macAddress = fromLineToGetMacAddress(line, MAC_ADDRESS_PREFIX01);
                }
                if (line.contains(MAC_ADDRESS_PREFIX02)) {
                    macAddress = fromLineToGetMacAddress(line, MAC_ADDRESS_PREFIX02);
                }
            }
        }
        br.close();
        return macAddress;
    }

    public String fromLineToGetMacAddress(String line, String MAC_ADDRESS_PREFIX) {
        String macAddress = "";
        int index = line.indexOf(MAC_ADDRESS_PREFIX);
        if (index != -1) {
            macAddress = line.substring(index + MAC_ADDRESS_PREFIX.length()).trim().toUpperCase();
        }
        return macAddress;
    }

    /**
     * 通过IP匹配相应的显示机器，并返回显示机器对应的类型名称。
     */
    public String getStationName(String ip) {
        return stationScreenAndIP.get(ip);
    }
}
