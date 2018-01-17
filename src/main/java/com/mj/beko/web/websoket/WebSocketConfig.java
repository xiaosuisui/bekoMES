package com.mj.beko.web.websoket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/**
 *
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Override
    public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
        stompEndpointRegistry.addEndpoint("/gs-guide-websocket")
            .setAllowedOrigins("*")
//            .setAllowedOrigins("http://localhost:8080", "http://xxxxxx", "http://xxxxxx")
            .withSockJS();
    }

/**
 *  The configureMessageBroker() method overrides the default method in WebSocketMessageBrokerConfigurer to configure the message broker.
 *  It starts by calling enableSimpleBroker() to enable a simple memory-based message broker to carry the greeting messages back to the client on destinations prefixed with "/topic".
 *  It also designates the "/app" prefix for messages that are bound for @MessageMapping-annotated methods. This prefix will be used to define all the message mappings;
 *  for example, "/app/hello" is the endpoint that the GreetingController.greeting() method is mapped to handle.*/
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        log.info("websocket init:::::::::::::::::::::;");
        registry.enableSimpleBroker("/topic","/queue");
        registry.setApplicationDestinationPrefixes("/app");
    }

}
