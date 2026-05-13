package com.saki.personmanagement.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Cache configuration using Caffeine for production-ready caching.
 * Cache-Konfiguration mit Caffeine für produktionsreifes Caching.
 *
 * @author saki
 */
@Configuration
public class CacheConfig {

    /**
     * Cache names / Cache-Namen
     */
    public static final String CACHE_PERSONS = "persons";
    public static final String CACHE_CALCULATIONS = "calculations";

    /**
     * Configures Caffeine as the cache manager.
     * Konfiguriert Caffeine als Cache-Manager.
     *
     * maximumSize  → max entries before eviction / max Einträge vor Verdrängung
     * expireAfterWrite → TTL after which entries are deleted / TTL nach dem Einträge gelöscht werden
     * recordStats  → enables hit/miss monitoring / aktiviert Hit/Miss Monitoring
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                CACHE_PERSONS,
                CACHE_CALCULATIONS
        );

        cacheManager.setCaffeine(
                Caffeine.newBuilder()
                        .maximumSize(200) // Max 200 entries
                        .expireAfterWrite(10, TimeUnit.MINUTES) // TTL: 10 minutes
                        .recordStats() // Enable monitoring

        );
        return cacheManager;

    }


}
