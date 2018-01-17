package com.mj.beko.constants;

/**
 * Created by xiaosui on 2017/8/11.
 * 定义系统中的一些常量 constants
 */
public class BekoImsConstants {
    /*默认的开发环境*/
    public static final String SPRING_PROFILE_DEVELOPMENT = "dev";

    public static final String SPRING_PROFILE_DEFAULT = "spring.profiles.default";

    public static final String SPRING_PROFILE_PRODUCTION = "prod";

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String USER = "ROLE_USER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    public static final String LOGIN_REGEX = "^[_'.@A-Za-z0-9-]*$";

    public static final String SYSTEM_ACCOUNT = "system";
    public static final String ANONYMOUS_USER = "anonymoususer";

    private BekoImsConstants(){}

}
