package com.mj.beko.service;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by MOUNTAIN on 2017/7/18.
 */
public interface IPAndMACService {
    String getIpAddr(HttpServletRequest httpServletRequest) throws Exception;

    String getMACAddress(String IP) throws Exception;

    String getStationName(String ip);
}
