package com.mj.beko.config;

import com.mj.beko.constants.OpcUaProperties;
import com.mj.beko.opcua.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wanghb
 */
@Configuration
public class OpcUaConfiguration {

    //根据opcua属性创建opcUaClient工厂实例
    @Bean
    List<OpcUaClientFactory> opcUaClientFactory(OpcUaProperties opcUaProperties) {
        List<OpcUaClientFactory> opcUaClientFactoryList = new ArrayList<OpcUaClientFactory>();
        List<Map<String, String>> plcList = opcUaProperties.getPlcList();
        for (Map<String, String> plc : plcList) {
            AutoReconnectUaClientFactory opcUaClientFactory = new AutoReconnectUaClientFactory();
            opcUaClientFactory.setUaAddress(plc.get("address"));
            opcUaClientFactoryList.add(opcUaClientFactory);
        }
        return opcUaClientFactoryList;
    }

    //根据opcua属性和opcUaClient工厂创建opcUaClient模板
    @Bean(destroyMethod = "close")
    OpcUaClientTemplate opcUaClientTemplate(List<OpcUaClientFactory> opcUaClientFactoryList, OpcUaProperties opcUaProperties)
        throws OpcUaClientException {
        return new OpcUaClientTemplate(opcUaClientFactoryList, opcUaProperties);
    }


    @Bean
    OpcUaSubscribeNodes opcUaSubscribeNodes(OpcUaProperties opcUaProperties) throws OpcUaClientException {
        return new OpcUaSubscribeNodes(opcUaProperties);
    }
}
