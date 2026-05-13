package com.saki.personmanagement.controller;

import com.saki.personmanagement.service.CalculatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 *
 */
@RestController
@RequestMapping("/api/calc")
@RequiredArgsConstructor
public class CalculatorCotroller {

    private final CalculatorService calculatorService;

    /**
     * GET /api/calc/add?a=10&b=20 - Adds two numbers (cached).
     * Addiert zwei Zahlen (gecacht).
     */
    @GetMapping("/add")
    public ResponseEntity<String> add(
            @RequestParam double a,
            @RequestParam double b) {

        long start = System.currentTimeMillis();
        double result = calculatorService.add(a, b);
        long duration = System.currentTimeMillis() - start;

        return ResponseEntity.ok(
                String.format("Result / Ergebnis: %.1f | Duration / Dauer: %dms | %s",
                        result, duration,
                        duration < 100 ? "⚡ FROM CACHE!" : "🔴 Calculated / Berechnet")
        );
    }

    /**
     * GET /api/calc/subtract?a=10&b=3 - Subtracts two numbers (cached).
     * Subtrahiert zwei Zahlen (gecacht).
     */
    @GetMapping("/subtract")
    public ResponseEntity<String> subtract(
            @RequestParam double a,
            @RequestParam double b) {

        long start = System.currentTimeMillis();
        double result = calculatorService.subtract(a, b);
        long duration = System.currentTimeMillis() - start;

        return ResponseEntity.ok(
                String.format("Result / Ergebnis: %.1f | Duration / Dauer: %dms | %s",
                        result, duration,
                        duration < 100 ? "⚡ FROM CACHE!" : "🔴 Calculated / Berechnet")
        );
    }

    /**
     * GET /api/calc/multiply?a=5&b=6 - Multiplies two numbers (cached).
     * Multipliziert zwei Zahlen (gecacht).
     */
    @GetMapping("/multiply")
    public ResponseEntity<String> multiply(
            @RequestParam double a,
            @RequestParam double b) {

        long start = System.currentTimeMillis();
        double result = calculatorService.multiply(a, b);
        long duration = System.currentTimeMillis() - start;

        return ResponseEntity.ok(
                String.format("Result / Ergebnis: %.1f | Duration / Dauer: %dms | %s",
                        result, duration,
                        duration < 100 ? "⚡ FROM CACHE!" : "🔴 Calculated / Berechnet")
        );
    }

    /**
     * POST /api/calc/clear - Clears the calculation cache.
     * Leert den Berechnungs-Cache.
     */
    @PostMapping("/clear")
    public ResponseEntity<String> clearCache() {
        calculatorService.clearCache();
        return ResponseEntity.ok("🗑️ Cache cleared! Next calculation will be slow again. / Cache geleert! Nächste Berechnung wird wieder langsam.");
    }

}
