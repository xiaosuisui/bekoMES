package com.mj.beko.constants;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author wanghb
 */
@ConfigurationProperties(prefix = "httpclient", ignoreUnknownFields = false)
public class HttpClientProperties {

    private final Pool pool = new Pool();

    private final Api api = new Api();


    private final Request request = new Request();

    private final Retry retry = new Retry();

    private final Beko beko = new Beko();

    public Api getApi() {
        return api;
    }
    public Pool getPool() {
        return pool;
    }

    public Request getRequest() {
        return request;
    }

    public Retry getRetry() {
        return retry;
    }

    public Beko getBeko() {
        return beko;
    }

    public static class Api{
        private String employInfoByRfidCard;

        public String getEmployInfoByRfidCard() {
            return employInfoByRfidCard;
        }
        public void setEmployInfoByRfidCard(String employInfoByRfidCard) {
            this.employInfoByRfidCard = employInfoByRfidCard;
        }
    }

    public static class Pool {
        private int maxTotal = 200;
        private int defaultMaxPerRoute = 100;

        public int getMaxTotal() {
            return maxTotal;
        }

        public void setMaxTotal(int maxTotal) {
            this.maxTotal = maxTotal;
        }

        public int getDefaultMaxPerRoute() {
            return defaultMaxPerRoute;
        }

        public void setDefaultMaxPerRoute(int defaultMaxPerRoute) {
            this.defaultMaxPerRoute = defaultMaxPerRoute;
        }
    }

    public static class Request {
        private int connectTimeout = 5000;
        private int readTimeout = 10000;
        private int connectionRequestTimeout = 200;

        public int getConnectTimeout() {
            return connectTimeout;
        }

        public void setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
        }

        public int getReadTimeout() {
            return readTimeout;
        }

        public void setReadTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
        }

        public int getConnectionRequestTimeout() {
            return connectionRequestTimeout;
        }

        public void setConnectionRequestTimeout(int connectionRequestTimeout) {
            this.connectionRequestTimeout = connectionRequestTimeout;
        }
    }

    public static class Retry {
        private int retryCount = 3;
        private boolean requestSentRetryEnabled = true;

        public int getRetryCount() {
            return retryCount;
        }

        public void setRetryCount(int retryCount) {
            this.retryCount = retryCount;
        }

        public boolean isRequestSentRetryEnabled() {
            return requestSentRetryEnabled;
        }

        public void setRequestSentRetryEnabled(boolean requestSentRetryEnabled) {
            this.requestSentRetryEnabled = requestSentRetryEnabled;
        }
    }

    public static class Beko {
        private String host = "localhost";
        private int port = 8080;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }
}
