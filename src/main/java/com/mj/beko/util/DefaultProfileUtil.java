package com.mj.beko.util;

import com.mj.beko.constants.BekoImsConstants;
import org.springframework.boot.SpringApplication;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ricardo on 2017/8/11.
 */
public final class DefaultProfileUtil {

    private DefaultProfileUtil() {
    }

    /**
     * 添加默认的开发环境
     * @param app
     */
    public static void addDefaultProfile(SpringApplication app) {
        Map<String, Object> defProperties =  new HashMap<>();
        /*
        * The default profile to use when no other profiles are defined
        * This cannot be set in the <code>application.yml</code> file.
        */
        defProperties.put(BekoImsConstants.SPRING_PROFILE_DEFAULT, BekoImsConstants.SPRING_PROFILE_DEVELOPMENT);
        app.setDefaultProperties(defProperties);
    }

}
