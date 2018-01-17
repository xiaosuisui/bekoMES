package com.mj.beko.opcua;

import com.prosysopc.ua.client.UaClient;

/**
 * @author wanghb
 */
public interface OpcUaClientConnectionListener {

    void onConnected(int plcNo, UaClient uaClient);
}
