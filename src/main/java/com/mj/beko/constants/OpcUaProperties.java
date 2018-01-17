package com.mj.beko.constants;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

/**
 * @author wanghb
 */
@ConfigurationProperties(prefix = "opcua", ignoreUnknownFields = false)
public class OpcUaProperties {

    private Retry retry;

    private long publishingRate;

    private List<Map<String, String>> plcList;

    public List<Map<String, String>> getPlcList() {
        return plcList;
    }

    public void setPlcList(List<Map<String, String>> plcList) {
        this.plcList = plcList;
    }

    public Retry getRetry() {
        return retry;
    }

    public void setRetry(Retry retry) {
        this.retry = retry;
    }

    public long getPublishingRate() {
        return publishingRate;
    }

    public void setPublishingRate(long publishingRate) {
        this.publishingRate = publishingRate;
    }

    public static class Retry {
        private long connBackOffPeriod = 10000L;
        private int maxAttempts = 3;
        private long backOffPeriod = 1000L;

        public long getConnBackOffPeriod() {
            return connBackOffPeriod;
        }

        public void setConnBackOffPeriod(long connBackOffPeriod) {
            this.connBackOffPeriod = connBackOffPeriod;
        }

        public int getMaxAttempts() {
            return maxAttempts;
        }

        public void setMaxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
        }

        public long getBackOffPeriod() {
            return backOffPeriod;
        }

        public void setBackOffPeriod(long backOffPeriod) {
            this.backOffPeriod = backOffPeriod;
        }
    }
}
