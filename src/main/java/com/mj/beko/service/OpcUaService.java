package com.mj.beko.service;

import com.mj.beko.opcua.OpcUaClientTemplate;
import com.mj.beko.opcualistener.OpcUaConnectionListener;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * @author wanghb
 */
@Service
public class OpcUaService {
    @Inject
    private OpcUaClientTemplate opcUaClientTemplate;

    @Inject
    private OpcUaConnectionListener uaConnectionListener;

    @PostConstruct
    public void opcuaClientConnect() {
        opcUaClientTemplate.addConnectionListener(uaConnectionListener);
        opcUaClientTemplate.connectAlwaysInBackend();
    }
}
