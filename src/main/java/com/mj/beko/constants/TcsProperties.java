package com.mj.beko.constants;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by jc on 2017/8/1.
 */
@ConfigurationProperties(prefix = "tcs", ignoreUnknownFields = false)
@Data
public class TcsProperties {

    private String host = "localhost";

    private int port = 5672;

    private int statusPort = 7890;

}
