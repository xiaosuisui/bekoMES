package com.mj.beko.util;

import org.springframework.http.HttpHeaders;

/**
 * Created by xiaosui on 2017/6/30.
 * 定义api中的responseHeader
 */
public class HttpResponseHeader {
    public static HttpHeaders getResponseHeader(){
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "application/json;charset=utf-8");
        // disable cache
        responseHeaders.add("Expires", "Tue, 03 Jul 2019 06:00:00 GMT");
        responseHeaders.add("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        responseHeaders.add("Cache-Control", "post-check=0, pre-check=0");
        responseHeaders.add("Pragma", "no-cache");
        return responseHeaders;
    }
}
