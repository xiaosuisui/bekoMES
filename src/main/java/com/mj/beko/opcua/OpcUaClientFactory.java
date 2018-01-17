package com.mj.beko.opcua;

import com.prosysopc.ua.client.UaClient;

/**
 * @author wanghb
 */
public interface OpcUaClientFactory {

    UaClient createUaClient() throws OpcUaClientException;

    String getUaAddress();
}
