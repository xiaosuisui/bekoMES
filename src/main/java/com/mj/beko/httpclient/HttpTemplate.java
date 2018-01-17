package com.mj.beko.httpclient;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.*;
import java.net.URI;
import java.util.Collections;

/**
 * @author wanghb
 */
public class HttpTemplate extends RestTemplate {

    public static final int MAX_ATTEMPTS = 3;
    public static final long BACK_OFF_PERIOD = 500;

    private String bekoApiHost;

    private int bekoApiPort;

    private String bekoApiHttpSchemeHierarchical;

    // retry template
    private final RetryTemplate retryTemplate = new RetryTemplate();

    public HttpTemplate(ClientHttpRequestFactory clientHttpRequestFactory, String bekoApiHost, int bekoApiPort) {
        super(clientHttpRequestFactory);
        this.bekoApiHost = bekoApiHost;
        this.bekoApiPort = bekoApiPort;
        this.bekoApiHttpSchemeHierarchical = "http://" + this.bekoApiHost + ":" + this.bekoApiPort;

        // retry policy
        SimpleRetryPolicy policy = new SimpleRetryPolicy(MAX_ATTEMPTS,
            Collections.<Class<? extends Throwable>, Boolean>singletonMap(ResourceAccessException.class, true));
        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(BACK_OFF_PERIOD);
        retryTemplate.setRetryPolicy(policy);
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);
    }

    public String getBekoApiHost() {
        return bekoApiHost;
    }

    public void setBekoApiHost(String bekoApiHost) {
        this.bekoApiHost = bekoApiHost;
    }

    public int getBekoApiPort() {
        return bekoApiPort;
    }

    public void setBekoApiPort(int bekoApiPort) {
        this.bekoApiPort = bekoApiPort;
    }

    public String getBekoApiHttpSchemeHierarchical() {
        return bekoApiHttpSchemeHierarchical;
    }

    public void setBekoApiHttpSchemeHierarchical(String bekoApiHttpSchemeHierarchical) {
        this.bekoApiHttpSchemeHierarchical = bekoApiHttpSchemeHierarchical;
    }

    @Override
    protected <T> T doExecute(URI url, HttpMethod method, RequestCallback requestCallback,
                              ResponseExtractor<T> responseExtractor) throws RestClientException {
        return retryTemplate.execute(
            context -> HttpTemplate.super.doExecute(url, method, requestCallback, responseExtractor));
    }
}
