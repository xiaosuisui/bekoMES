package com.mj.beko.opcua;

/**
 * @author wanghb
 */
public interface OpcUaClientCallback<T> {

    T performAction() throws Exception;
}
