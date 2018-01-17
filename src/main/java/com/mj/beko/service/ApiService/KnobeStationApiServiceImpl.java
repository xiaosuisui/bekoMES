package com.mj.beko.service.ApiService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ricardo on 2017/11/22.
 */
@Service
@Slf4j
@Transactional
public class KnobeStationApiServiceImpl implements KnobeStationApiService {
    /*推送*/
    @Inject
    private SimpMessagingTemplate template;
    @Override
    public void pushPalletNoToKnobeStation(String palletNo) {
        Map<String,String> map =new HashMap<String,String>();
        map.put("palletNo",palletNo);
        template.convertAndSend("/topic/KnobeStation/palletNo",map);
    }
}
