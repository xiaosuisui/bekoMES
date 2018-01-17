package com.mj.beko.web.rest;

import com.mj.beko.constants.HttpClientProperties;
import com.mj.beko.domain.EmployInfo;
import com.mj.beko.httpclient.HttpTemplate;
import com.mj.beko.service.IPAndMACService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取Ip地址
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class IPAndMACResource {
    @Autowired
    private HttpTemplate httpTemplate;
    @Autowired
    private IPAndMACService ipAndMACService;
    @Autowired
    private HttpClientProperties httpClientProperties;

    @RequestMapping("/IPAndMAC")
    public Map getRemoteIPAndMac(String operatorCardId, HttpServletRequest httpServletRequest) {
        String url=null;
        Map<String, String> result = new HashMap<>();
        String testNo="4010a01ed425";
       if(operatorCardId.contains("CARDNO")){
             url="http://"+httpClientProperties.getBeko().getHost()+httpClientProperties.getApi().getEmployInfoByRfidCard()+operatorCardId;
        }else{
            url="http://"+httpClientProperties.getBeko().getHost()+httpClientProperties.getApi().getEmployInfoByRfidCard()+testNo;
        }
        EmployInfo employInfo=httpTemplate.getForObject(url, EmployInfo.class);
        if(employInfo==null||employInfo.getEmployeeNumber()==null||"".equals(employInfo.getEmployeeNumber())){
            result.put("resultStatus","nok");
            return result;
        }
        log.info("em.....{}{}{}",employInfo.getName(),employInfo.getEmployeeNumber(),employInfo.getSurname());
        //判断接收参数operatorCardId是否为空
        //验证成功后，根据返回验证结果信息，拿到operatorName
        result.put("resultStatus", "ok");
        result.put("operatorName", employInfo.getName()+employInfo.getSurname());
        try {
            String IP = ipAndMACService.getIpAddr(httpServletRequest);
           /* String MAC = ipAndMACService.getMACAddress(IP);*/
            String type = ipAndMACService.getStationName(IP);
            if ("127.0.0.1".equals(IP)) {
                IP = InetAddress.getLocalHost().getHostAddress();
            }
            result.put("IP", IP);
         /*   result.put("MAC", MAC);*/
            result.put("type", type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    @RequestMapping("/getMacByIp")
    public Map getRemoteMacByIp(String ip) {
        Map<String,String> map = new HashMap<String,String>();
        try {
          String MAC = ipAndMACService.getMACAddress(ip);
          map.put("MAC",MAC);
        }catch (Exception e){
            e.printStackTrace();
        }
        return map;
    }
}
