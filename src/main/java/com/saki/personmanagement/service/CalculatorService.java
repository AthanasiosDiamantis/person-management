package com.saki.personmanagement.service;

import com.saki.personmanagement.config.CacheConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Calculator service demonstrating Spring Boot Caching.
 * Calculator-Service der Spring Boot Caching demonstriert.
 *
 * Uses artificial 2-second delay to make cache effect visible.
 * Nutzt künstliche 2-Sekunden-Verzögerung um Cache-Effekt sichtbar zu machen.
 *
 * @author saki
 */
@Service
@Slf4j
public class CalculatorService {

    /**
     * Adds two numbers - result is cached!
     * Addiert zwei Zahlen - Ergebnis wird gecacht!
     *
     * Cache-Key: methodName + a + b → unique per operation
     * Cache-Key: methodName + a + b → eindeutig pro Operation
     */
    @Cacheable(
            value = CacheConfig.CACHE_CALCULATIONS,
            key = "#root.methodName + '_' + #a + '_' + #b"
    )
    public double add(double a, double b) {
        log.info("🔴 CALCULATING (not from cache): {} + {}", a, b);
        simulateSlowOperation();
        return a + b;
    }

    /**
     * Subtracts two numbers - result is cached!
     * Subtrahiert zwei Zahlen - Ergebnis wird gecacht!
     */
    @Cacheable(
            value = CacheConfig.CACHE_CALCULATIONS,
            key = "#root.methodName + '_' + #a + '_' + #b"
    )
    public double subtract(double a, double b) {
        log.info("🔴 CALCULATING (not from cache): {} - {} / BERECHNUNG (nicht aus Cache): {} - {}",
                a, b, a, b);
        simulateSlowOperation();
        return a - b;
    }

    /**
     * Multiplies two numbers - result is cached!
     * Multipliziert zwei Zahlen - Ergebnis wird gecacht!
     */
    @Cacheable(
            value = CacheConfig.CACHE_CALCULATIONS,
            key = "#root.methodName + '_' + #a + '_' + #b"
    )
    public double multiply(double a, double b) {
        log.info("🔴 CALCULATING (not from cache): {} * {} / BERECHNUNG (nicht aus Cache): {} * {}",
                a, b, a, b);
        simulateSlowOperation();
        return a * b;
    }

    /**
     * Clears the entire calculation cache.
     * Leert den gesamten Berechnungs-Cache.
     */
    @CacheEvict(value = CacheConfig.CACHE_CALCULATIONS, allEntries = true)
    public void clearCache() {
        log.info("🗑️ Calculation cache cleared / Berechnungs-Cache geleert");
    }


    private void simulateSlowOperation() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
