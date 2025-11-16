package com.example.crudapp.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {
    
    @Bean
    public Counter entryCreatedCounter(MeterRegistry meterRegistry) {
        return Counter.builder("crud_app_entries_created_total")
                .description("Total number of entries created")
                .register(meterRegistry);
    }
    
    @Bean
    public Counter entryDeletedCounter(MeterRegistry meterRegistry) {
        return Counter.builder("crud_app_entries_deleted_total")
                .description("Total number of entries deleted")
                .register(meterRegistry);
    }
    
    @Bean
    public Counter cacheHitCounter(MeterRegistry meterRegistry) {
        return Counter.builder("crud_app_cache_hits_total")
                .description("Total number of cache hits")
                .register(meterRegistry);
    }
    
    @Bean
    public Counter cacheMissCounter(MeterRegistry meterRegistry) {
        return Counter.builder("crud_app_cache_misses_total")
                .description("Total number of cache misses")
                .register(meterRegistry);
    }
    
    @Bean
    public Timer databaseQueryTimer(MeterRegistry meterRegistry) {
        return Timer.builder("crud_app_database_query_duration")
                .description("Database query execution time")
                .register(meterRegistry);
    }
    
    @Bean
    public Timer redisOperationTimer(MeterRegistry meterRegistry) {
        return Timer.builder("crud_app_redis_operation_duration")
                .description("Redis operation execution time")
                .register(meterRegistry);
    }
}