package com.example.crudapp.controller;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.search.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/metrics")
public class MetricsController {
    
    @Autowired
    private MeterRegistry meterRegistry;
    
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getMetricsSummary() {
        Map<String, Object> metrics = new HashMap<>();
        
        // Custom application metrics
        metrics.put("entries_created_total", getCounterValue("crud_app_entries_created_total"));
        metrics.put("entries_deleted_total", getCounterValue("crud_app_entries_deleted_total"));
        metrics.put("cache_hits_total", getCounterValue("crud_app_cache_hits_total"));
        metrics.put("cache_misses_total", getCounterValue("crud_app_cache_misses_total"));
        
        // HTTP metrics
        metrics.put("http_requests_total", getCounterValue("http_server_requests_seconds_count"));
        
        // JVM metrics
        metrics.put("jvm_memory_used_bytes", getGaugeValue("jvm_memory_used_bytes"));
        metrics.put("jvm_threads_live", getGaugeValue("jvm_threads_live"));
        
        // Database connection pool metrics
        metrics.put("hikaricp_connections_active", getGaugeValue("hikaricp_connections_active"));
        metrics.put("hikaricp_connections_idle", getGaugeValue("hikaricp_connections_idle"));
        
        return ResponseEntity.ok(metrics);
    }
    
    private Double getCounterValue(String meterName) {
        return Search.in(meterRegistry)
                .name(meterName)
                .counter()
                .count();
    }
    
    private Double getGaugeValue(String meterName) {
        return Search.in(meterRegistry)
                .name(meterName)
                .gauge()
                .value();
    }
}