package com.mj.beko.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;

@Configuration
@EnableCaching
public class CacheConfiguration {
    @Bean
    public JCacheCacheManager jcacheCacheManager(){
        JCacheCacheManager cm = new JCacheCacheManager();
        cm.setCacheManager(jsr107cacheManager());
        return cm;
    }
    @Bean
    @Primary
    public CacheManager jsr107cacheManager(){
        CachingProvider provider = Caching.getCachingProvider();
        CacheManager cacheManager = provider.getCacheManager();

        MutableConfiguration<Long, String> configuration =
            new MutableConfiguration<Long, String>()
                .setStoreByValue(false).setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration.ONE_HOUR));
        cacheManager.createCache("foo", configuration);

        return cacheManager;
    }
}
