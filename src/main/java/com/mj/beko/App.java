package com.mj.beko;

import com.mj.beko.constants.HttpClientProperties;
import com.mj.beko.constants.OpcUaProperties;
import com.mj.beko.constants.JHipsterProperties;
import com.mj.beko.constants.TcsProperties;
import com.mj.beko.database.DynamicDataSourceRegister;
import com.mj.beko.util.DefaultProfileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 启动类
 */
@SpringBootApplication
@Slf4j
@Import({DynamicDataSourceRegister.class})
@EnableScheduling
@EnableConfigurationProperties({OpcUaProperties.class, JHipsterProperties.class, TcsProperties.class,
        HttpClientProperties.class})
public class App extends SpringBootServletInitializer {
    public static void main( String[] args ) throws UnknownHostException
    {
        SpringApplication app = new SpringApplication(App.class);
        DefaultProfileUtil.addDefaultProfile(app);
        Environment env = app.run(args).getEnvironment();
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        log.info("\n----------------------------------------------------------\n\t" +
                        "Application '{}' is running! Access URLs:\n\t" +
                        "Local: \t\t{}://localhost:{}\n\t" +
                        "External: \t{}://{}:{}\n\t" +
                        "Profile(s): \t{}\n----------------------------------------------------------",
                env.getProperty("spring.application.name"),
                protocol,
                env.getProperty("server.port"),
                protocol,
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"),
                env.getActiveProfiles());
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        DefaultProfileUtil.addDefaultProfile(builder.application());
        return builder.sources(App.class);
    }
}