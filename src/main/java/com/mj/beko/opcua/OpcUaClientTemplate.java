package com.mj.beko.opcua;

import com.mj.beko.constants.OpcUaProperties;
import com.prosysopc.ua.ServiceException;
import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.client.*;
import com.prosysopc.ua.nodes.UaDataType;
import com.prosysopc.ua.nodes.UaNode;
import com.prosysopc.ua.nodes.UaVariable;
import org.opcfoundation.ua.builtintypes.DataValue;
import org.opcfoundation.ua.builtintypes.NodeId;
import org.opcfoundation.ua.builtintypes.UnsignedInteger;
import org.opcfoundation.ua.builtintypes.Variant;
import org.opcfoundation.ua.core.Attributes;
import org.opcfoundation.ua.core.MonitoringMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.AlwaysRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.Async;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author wanghb
 */
public class OpcUaClientTemplate {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpcUaClientTemplate.class);

    private List<UaClient> uaClientList = new ArrayList<UaClient>();

    private RetryTemplate retryTemplate;

    private long connBackOffPeriod;

    private List<OpcUaClientConnectionListener> connectionListeners = new ArrayList<>();

    private OpcUaProperties properties;

    public List<UaClient> getUaClientList() {
        return uaClientList;
    }

    public OpcUaClientTemplate(List<OpcUaClientFactory> opcUaClientFactoryList, OpcUaProperties properties)
        throws OpcUaClientException {
        LOGGER.debug("OpcUaClientTemplate Load.");
        for (OpcUaClientFactory opcUaClientFactory : opcUaClientFactoryList) {
            //使用opcUaClient工厂实例创建uaClient
            UaClient uaClient = opcUaClientFactory.createUaClient();
            uaClientList.add(uaClient);
        }
        connBackOffPeriod = properties.getRetry().getConnBackOffPeriod();
        retryTemplate = new RetryTemplate();
        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
        simpleRetryPolicy.setMaxAttempts(properties.getRetry().getMaxAttempts());
        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(properties.getRetry().getBackOffPeriod());
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);
        retryTemplate.setRetryPolicy(simpleRetryPolicy);
        this.properties = properties;
    }

    //订阅入口
    @Async("taskExecutor")
    public void connectAlwaysInBackend() {
        LOGGER.debug("OpcUaClientTemplate connectAlwaysInBackend taskExecutor run.");
        RetryTemplate alwaysRetryTemplate = new RetryTemplate();
        alwaysRetryTemplate.setRetryPolicy(new AlwaysRetryPolicy());
        FixedBackOffPolicy connFixedBackOffPolicy = new FixedBackOffPolicy();
        connFixedBackOffPolicy.setBackOffPeriod(connBackOffPeriod);
        alwaysRetryTemplate.setBackOffPolicy(connFixedBackOffPolicy);
        try {
            for (int i = 0; i < uaClientList.size(); i++) {
                int plcNo = i;
                alwaysRetryTemplate.execute(context -> {
                    try {
                        UaClient uaClient = uaClientList.get(plcNo);
                        connect(uaClient);
                        fireConnectionListeners(plcNo, uaClient);
                    } catch (OpcUaClientException e) {
                        e.printStackTrace();
                        throw e;
                    }
                    return true;
                });
            }
        } catch (OpcUaClientException e) {
            LOGGER.error("Error subscription: ", e);
        }
    }

    /**
     * 根据UaClient连接相应OPCUA服务并调用启动订阅的功能
     * @param uaClient
     * @return
     * @throws OpcUaClientException
     */
    public synchronized boolean connect(UaClient uaClient) throws OpcUaClientException {
        if (!uaClient.isConnected()) {
            try {
                LOGGER.info("Connecting ua server:{}", uaClient.getUri());
                uaClient.connect();
                return true;
            } catch (Exception e) {
                LOGGER.error("Error connecting ua server:{}", uaClient.getUri());
                throw new OpcUaClientException("Error connecting ua server and throw Exception: ", e);
            }
        } else {
            return true;
        }
    }

    public void addConnectionListener(OpcUaClientConnectionListener connectionListener) {
        LOGGER.info("add opcua connection listener {}", connectionListener);
        this.connectionListeners.add(connectionListener);
    }

    //启动监听列表中的所有监听器的订阅功能
    private void fireConnectionListeners(int plcNo, UaClient uaClient) throws OpcUaClientException {
        this.connectionListeners.stream().forEach(opcUaClientConnectionListener -> {
            opcUaClientConnectionListener.onConnected(plcNo, uaClient);
        });
    }

    //连接OPCUA并执行相应的功能
    public <T> T execute(UaClient uaClient, final OpcUaClientCallback<T> callback) throws OpcUaClientException {
        try {
            return this.retryTemplate.execute(context -> {
                connect(uaClient);
                return callback.performAction();
            });
        } catch (Exception e) {
            LOGGER.error("execute()", e);
            throw new OpcUaClientException("Error executing action: ", e);
        }
    }

    public RetryTemplate getRetryTemplate() {
        return retryTemplate;
    }

    //根据节点ID和监听为相应的节点订阅
    public boolean subscribeNodeValue(UaClient uaClient, NodeId id, MonitoredDataItemListener dataChangeListener)
        throws OpcUaClientException {
        return execute(uaClient, () -> doSubscribeNodeValue(uaClient, id, dataChangeListener));
    }

    //根据节点ID和监听为相应的节点订阅
    private synchronized boolean doSubscribeNodeValue(UaClient uaClient, NodeId id, MonitoredDataItemListener dataChangeListener)
        throws OpcUaClientException {
        try {
            Subscription subscription = new Subscription();
            subscription.setPublishingInterval(properties.getPublishingRate(), TimeUnit.MILLISECONDS);
            MonitoredDataItem item = new MonitoredDataItem(id, Attributes.Value,
                MonitoringMode.Reporting, subscription.getPublishingInterval());
            item.setDataChangeListener(dataChangeListener);
            subscription.addItem(item);
            uaClient.addSubscription(subscription);
            return true;
        } catch (ServiceException | StatusException e) {
            throw new OpcUaClientException("Error subscribing node " + id + ", value: ", e);
        }
    }

    /**
     * 根据UaClient将值写入到指定的节点，写入成功为返回true，否则为false
     * @param uaClient
     * @param id
     * @param object
     * @return
     * @throws OpcUaClientException
     */
    public boolean writeNodeValue(UaClient uaClient, NodeId id, Object object) throws OpcUaClientException {
        return execute(uaClient, () -> doWriteNodeValue(uaClient, id, object));
    }

    /**
     * 根据UaClient将值写入到指定的节点，写入成功为返回true，否则为false
     * @param uaClient
     * @param nodeId
     * @param value
     * @return
     * @throws OpcUaClientException
     */
    private boolean doWriteNodeValue(UaClient uaClient, NodeId nodeId, Object value) throws OpcUaClientException {
        try {
            UnsignedInteger attributeId = Attributes.Value;
            UaNode node = uaClient.getAddressSpace().getNode(nodeId);
            UaDataType dataType = null;
            if (attributeId.equals(Attributes.Value) && (node instanceof UaVariable)) {
                UaVariable v = (UaVariable) node;
                if (v.getDataType() == null) {
                    v.setDataType(uaClient.getAddressSpace().getType(v.getDataTypeId()));
                }
                dataType = (UaDataType) v.getDataType();
            }

            // 如果value是数组
            if (value.getClass().isArray()) {
                Object[] array = (Object[]) value;
                Object newArray = null;
                for (int i = 0; i < array.length; i++) {
                    Object el = dataType != null
                        ? uaClient.getAddressSpace()
                        .getDataTypeConverter()
                        .parseVariant(array[i].toString(), dataType)
                        : value;
                    Object v = ((Variant) el).getValue();
                    if (newArray == null) {
                        newArray = Array.newInstance(v.getClass(), array.length);
                    }
                    Array.set(newArray, i, v);
                }
                return uaClient.writeAttribute(nodeId, attributeId, newArray);
            }

            Object convertedValue = dataType != null
                ? uaClient.getAddressSpace().getDataTypeConverter().parseVariant(value.toString(), dataType) : value;
            return uaClient.writeAttribute(nodeId, attributeId, convertedValue);
        } catch (AddressSpaceException | ServiceException | StatusException e) {
            throw new OpcUaClientException("Error writing node value: ", e);
        }
    }

    /**
     * 根据UaClient和节点读取节点值
     * @param id
     * @param uaClient
     * @return
     * @throws OpcUaClientException
     */
    public Variant readNodeVariant(UaClient uaClient, NodeId id) throws OpcUaClientException {
        return execute(uaClient, () -> doReadNodeValue(uaClient ,id));
    }

    /**
     * 根据UaClient和节点读取节点值
     * @param uaClient
     * @param id
     * @return
     * @throws OpcUaClientException
     */
    private Variant doReadNodeValue(UaClient uaClient, NodeId id) throws OpcUaClientException {
        try {
            DataValue dataValue = uaClient.readValue(id);
            return dataValue.getValue();
        } catch (ServiceException | StatusException e) {
            throw new OpcUaClientException("Error reading " + id + " node value: ", e);
        }
    }

    /**
     * 关闭OPCUA的连接
     */
    public void close() {
        for (UaClient uaClient : uaClientList) {
            uaClient.disconnect();
        }
    }
}
