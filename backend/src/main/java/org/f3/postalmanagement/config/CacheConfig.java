package org.f3.postalmanagement.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for application-level caching.
 * Caches frequently accessed, rarely changing data like provinces and wards.
 */
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
            "provinces",
            "wards",
            "administrativeRegions",
            "wardsByProvince"
        );
    }
}
