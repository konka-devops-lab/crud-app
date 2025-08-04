package com.example.crudapp.service;

import com.example.crudapp.model.Entry;
import com.example.crudapp.repository.EntryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class EntryService {
    
    private static final Logger logger = LoggerFactory.getLogger(EntryService.class);
    private static final String CACHE_KEY = "all_entries";
    private static final int CACHE_TTL = 60; // seconds
    
    @Autowired
    private EntryRepository entryRepository;
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private Counter cacheHitCounter;
    
    @Autowired
    private Counter cacheMissCounter;
    
    @Autowired
    private Counter entryCreatedCounter;
    
    @Autowired
    private Counter entryDeletedCounter;
    
    @Autowired
    private Timer databaseQueryTimer;
    
    @Autowired
    private Timer redisOperationTimer;
    
    public List<Entry> getAllEntries() {
        MDC.put("operation", "getAllEntries");
        Timer.Sample redisTimer = Timer.start();
        
        try {
            // Try to get from cache first
            String cachedData = redisTemplate.opsForValue().get(CACHE_KEY);
            redisTimer.stop(redisOperationTimer);
            
            if (cachedData != null) {
                cacheHitCounter.increment();
                logger.info("Serving from Redis cache");
                return objectMapper.readValue(cachedData, 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Entry.class));
            } else {
                cacheMissCounter.increment();
                logger.info("Cache miss: No cache found, fetching from database");
            }
            
            // Fetch from database
            Timer.Sample dbTimer = Timer.start();
            List<Entry> entries = entryRepository.findAll();
            dbTimer.stop(databaseQueryTimer);
            
            // Cache the result
            logger.info("Serving from Database and caching the result");
            Timer.Sample cacheTimer = Timer.start();
            String jsonData = objectMapper.writeValueAsString(entries);
            redisTemplate.opsForValue().set(CACHE_KEY, jsonData, CACHE_TTL, TimeUnit.SECONDS);
            cacheTimer.stop(redisOperationTimer);
            
            return entries;
            
        } catch (JsonProcessingException e) {
            logger.error("Error processing JSON for cache", e);
            // Fallback to database only
            Timer.Sample dbTimer = Timer.start();
            List<Entry> entries = entryRepository.findAll();
            dbTimer.stop(databaseQueryTimer);
            return entries;
        } catch (Exception e) {
            logger.error("Redis Fetch Error", e);
            // Fallback to database only
            Timer.Sample dbTimer = Timer.start();
            List<Entry> entries = entryRepository.findAll();
            dbTimer.stop(databaseQueryTimer);
            return entries;
        } finally {
            MDC.clear();
        }
    }
    
    public Entry createEntry(Entry entry) {
        MDC.put("operation", "createEntry");
        MDC.put("entryDescription", entry.getDescription());
        
        Timer.Sample dbTimer = Timer.start();
        Entry savedEntry = entryRepository.save(entry);
        dbTimer.stop(databaseQueryTimer);
        
        entryCreatedCounter.increment();
        logger.info("Inserted entry with ID: {}", savedEntry.getId());
        
        // Clear the cache because data changed
        clearCache();
        
        MDC.clear();
        return savedEntry;
    }
    
    public boolean deleteEntry(Long id) {
        MDC.put("operation", "deleteEntry");
        MDC.put("entryId", String.valueOf(id));
        
        Timer.Sample dbTimer = Timer.start();
        Optional<Entry> entry = entryRepository.findById(id);
        dbTimer.stop(databaseQueryTimer);
        
        if (entry.isPresent()) {
            Timer.Sample deleteTimer = Timer.start();
            entryRepository.deleteById(id);
            deleteTimer.stop(databaseQueryTimer);
            
            entryDeletedCounter.increment();
            logger.info("Deleted entry with ID: {}", id);
            
            // Clear the cache because data changed
            clearCache();
            
            MDC.clear();
            return true;
        }
        
        MDC.clear();
        return false;
    }
    
    private void clearCache() {
        try {
            Timer.Sample redisTimer = Timer.start();
            redisTemplate.delete(CACHE_KEY);
            redisTimer.stop(redisOperationTimer);
            logger.info("Cache cleared for {}", CACHE_KEY);
        } catch (Exception e) {
            logger.error("Error clearing cache", e);
        }
    }
}