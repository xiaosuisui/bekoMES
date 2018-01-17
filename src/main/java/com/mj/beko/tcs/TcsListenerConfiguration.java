package com.mj.beko.tcs;

import com.mj.beko.constants.TcsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.tcp.TcpOperations;
import org.springframework.messaging.tcp.reactor.Reactor2TcpClient;
import reactor.fn.Consumer;
import reactor.fn.Function;
import reactor.io.buffer.Buffer;
import reactor.io.codec.Codec;

/**
 * Created by jc on 2017/8/2.
 */
@Configuration
public class TcsListenerConfiguration {
    @Bean(destroyMethod = "shutdown")
    TcpOperations<String> tcsListenerClient(TcsProperties properties) {
        return new Reactor2TcpClient<>(properties.getHost(), properties.getStatusPort(),
            new Codec<Buffer, Message<String>, Message<String>>() {
                @Override
                public Function<Buffer, Message<String>> decoder(Consumer<Message<String>> next) {
                    return bytes -> MessageBuilder.withPayload(bytes.asString()).build();
                }

                @Override
                public Buffer apply(Message<String> message) {
                    return Buffer.wrap(message.getPayload());
                }
            });
    }

}
