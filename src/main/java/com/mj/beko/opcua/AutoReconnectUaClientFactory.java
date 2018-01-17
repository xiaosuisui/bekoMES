package com.mj.beko.opcua;

import com.prosysopc.ua.client.UaClient;
import org.opcfoundation.ua.transport.security.SecurityMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;

/**
 * @author wanghb
 */
public class AutoReconnectUaClientFactory implements OpcUaClientFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoReconnectUaClientFactory.class);

    private String uaAddress;

    @Override
    public UaClient createUaClient() throws OpcUaClientException {
        UaClient uaClient;
        try {
            uaClient = new UaClient(getUaAddress());
            uaClient.setSecurityMode(SecurityMode.NONE);
            uaClient.setAutoReconnect(true);
        } catch (URISyntaxException e) {
            throw new OpcUaClientException("Error creating ua client: ", e);
        }
        return uaClient;
    }

    @Override
    public String getUaAddress() {
        return this.uaAddress;
    }

    public void setUaAddress(String uaAddress) {
        this.uaAddress = uaAddress;
    }
}
