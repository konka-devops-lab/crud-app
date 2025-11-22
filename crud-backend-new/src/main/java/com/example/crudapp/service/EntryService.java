// package com.example.crudapp.service;

// import com.example.crudapp.model.Entry;
// import com.example.crudapp.repository.EntryRepository;
// import com.fasterxml.jackson.core.JsonProcessingException;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.redis.core.RedisTemplate;
// import org.springframework.stereotype.Service;

// import java.util.List;
// import java.util.Optional;
// import java.util.concurrent.TimeUnit;

// @Service
// public class EntryService {
    
//     private static final Logger logger = LoggerFactory.getLogger(EntryService.class);
//     private static final String ALL_ENTRIES_CACHE_KEY = "all_entries";
//     private static final String ENTRY_CACHE_KEY_PREFIX = "entry_";
//     private static final int CACHE_TTL = 60; // seconds
    
//     @Autowired
//     private EntryRepository entryRepository;
    
//     @Autowired
//     private RedisTemplate<String, String> redisTemplate;
    
//     @Autowired
//     private ObjectMapper objectMapper;
    
//     public List<Entry> getAllEntries() {
//         try {
//             // Try to get from cache first
//             String cachedData = redisTemplate.opsForValue().get(ALL_ENTRIES_CACHE_KEY);
            
//             if (cachedData != null) {
//                 logger.info("Serving all entries from Redis cache");
//                 return objectMapper.readValue(cachedData, 
//                     objectMapper.getTypeFactory().constructCollectionType(List.class, Entry.class));
//             } else {
//                 logger.info("Cache miss: No cache found for all entries, fetching from database");
//             }
            
//             // Fetch from database
//             List<Entry> entries = entryRepository.findAll();
            
//             // Cache the result
//             logger.info("Serving all entries from Database and caching the result");
//             String jsonData = objectMapper.writeValueAsString(entries);
//             redisTemplate.opsForValue().set(ALL_ENTRIES_CACHE_KEY, jsonData, CACHE_TTL, TimeUnit.SECONDS);
            
//             return entries;
            
//         } catch (JsonProcessingException e) {
//             logger.error("Error processing JSON for cache", e);
//             // Fallback to database only
//             return entryRepository.findAll();
//         } catch (Exception e) {
//             logger.error("Redis Fetch Error", e);
//             // Fallback to database only
//             return entryRepository.findAll();
//         }
//     }
    
//     public Entry getEntryById(Long id) {
//         String cacheKey = ENTRY_CACHE_KEY_PREFIX + id;
        
//         try {
//             // Try to get from cache first
//             String cachedData = redisTemplate.opsForValue().get(cacheKey);
            
//             if (cachedData != null) {
//                 logger.info("Serving entry {} from Redis cache", id);
//                 return objectMapper.readValue(cachedData, Entry.class);
//             } else {
//                 logger.info("Cache miss: No cache found for entry {}, fetching from database", id);
//             }
            
//             // Fetch from database
//             Optional<Entry> entry = entryRepository.findById(id);
            
//             if (entry.isPresent()) {
//                 // Cache the result
//                 logger.info("Serving entry {} from Database and caching the result", id);
//                 String jsonData = objectMapper.writeValueAsString(entry.get());
//                 redisTemplate.opsForValue().set(cacheKey, jsonData, CACHE_TTL, TimeUnit.SECONDS);
                
//                 return entry.get();
//             }
            
//             return null;
            
//         } catch (JsonProcessingException e) {
//             logger.error("Error processing JSON for cache", e);
//             // Fallback to database only
//             return entryRepository.findById(id).orElse(null);
//         } catch (Exception e) {
//             logger.error("Redis Fetch Error for entry {}", id, e);
//             // Fallback to database only
//             return entryRepository.findById(id).orElse(null);
//         }
//     }
    
//     public Entry createEntry(Entry entry) {
//         Entry savedEntry = entryRepository.save(entry);
//         logger.info("Inserted entry with ID: {}", savedEntry.getId());
        
//         // Clear the cache because data changed
//         clearAllEntriesCache();
        
//         return savedEntry;
//     }
    
//     public Entry updateEntry(Long id, Entry entryDetails) {
//         Optional<Entry> optionalEntry = entryRepository.findById(id);
        
//         if (optionalEntry.isPresent()) {
//             Entry existingEntry = optionalEntry.get();
//             existingEntry.setAmount(entryDetails.getAmount());
//             existingEntry.setDescription(entryDetails.getDescription());
//             existingEntry.setDate(entryDetails.getDate());
            
//             Entry updatedEntry = entryRepository.save(existingEntry);
//             logger.info("Updated entry with ID: {}", id);
            
//             // Clear relevant caches because data changed
//             clearAllEntriesCache();
//             clearEntryCache(id);
            
//             return updatedEntry;
//         }
        
//         logger.warn("Update failed: Entry with ID {} not found", id);
//         return null;
//     }
    
//     public boolean deleteEntry(Long id) {
//         Optional<Entry> entry = entryRepository.findById(id);
        
//         if (entry.isPresent()) {
//             entryRepository.deleteById(id);
//             logger.info("Deleted entry with ID: {}", id);
            
//             // Clear relevant caches because data changed
//             clearAllEntriesCache();
//             clearEntryCache(id);
            
//             return true;
//         }
        
//         logger.warn("Delete failed: Entry with ID {} not found", id);
//         return false;
//     }
    
//     private void clearAllEntriesCache() {
//         try {
//             redisTemplate.delete(ALL_ENTRIES_CACHE_KEY);
//             logger.info("Cache cleared for {}", ALL_ENTRIES_CACHE_KEY);
//         } catch (Exception e) {
//             logger.error("Error clearing all entries cache", e);
//         }
//     }
    
//     private void clearEntryCache(Long id) {
//         try {
//             String cacheKey = ENTRY_CACHE_KEY_PREFIX + id;
//             redisTemplate.delete(cacheKey);
//             logger.info("Cache cleared for {}", cacheKey);
//         } catch (Exception e) {
//             logger.error("Error clearing entry cache for ID: {}", id, e);
//         }
//     }
    
//     // Optional: Method to clear all caches (useful for maintenance)
//     public void clearAllCaches() {
//         try {
//             // Clear all entries cache
//             redisTemplate.delete(ALL_ENTRIES_CACHE_KEY);
            
//             // Clear all individual entry caches (this is a simplified approach)
//             // In production, you might want to use Redis patterns to delete all entry_* keys
//             logger.info("All caches cleared");
//         } catch (Exception e) {
//             logger.error("Error clearing all caches", e);
//         }
//     }
// }

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
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class EntryService {
    
    private static final Logger logger = LoggerFactory.getLogger(EntryService.class);
    private static final String ALL_ENTRIES_CACHE_KEY = "all_entries";
    private static final String ENTRY_CACHE_KEY_PREFIX = "entry_";
    private static final int CACHE_TTL = 60; // seconds
    
    @Autowired
    private EntryRepository entryRepository;
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private MeterRegistry meterRegistry;
    
    // Metrics counters
    private final Counter cacheHitCounter;
    private final Counter cacheMissCounter;
    private final Counter entriesCreatedCounter;
    private final Counter entriesUpdatedCounter;
    private final Counter entriesDeletedCounter;
    private final Counter entriesReadCounter;
    private final Counter redisErrorCounter;
    private final Counter jsonErrorCounter;
    private final Timer databaseTimer;
    
    public EntryService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.cacheHitCounter = Counter.builder("app.cache.hits")
                .description("Number of cache hits")
                .register(meterRegistry);
        this.cacheMissCounter = Counter.builder("app.cache.misses")
                .description("Number of cache misses")
                .register(meterRegistry);
        this.entriesCreatedCounter = Counter.builder("app.entries.created")
                .description("Number of entries created")
                .register(meterRegistry);
        this.entriesUpdatedCounter = Counter.builder("app.entries.updated")
                .description("Number of entries updated")
                .register(meterRegistry);
        this.entriesDeletedCounter = Counter.builder("app.entries.deleted")
                .description("Number of entries deleted")
                .register(meterRegistry);
        this.entriesReadCounter = Counter.builder("app.entries.read")
                .description("Number of entries read")
                .register(meterRegistry);
        this.redisErrorCounter = Counter.builder("app.redis.errors")
                .description("Number of Redis errors")
                .register(meterRegistry);
        this.jsonErrorCounter = Counter.builder("app.json.errors")
                .description("Number of JSON processing errors")
                .register(meterRegistry);
        this.databaseTimer = Timer.builder("app.database.operations")
                .description("Time spent on database operations")
                .register(meterRegistry);
    }
    
    public List<Entry> getAllEntries() {
        String traceId = generateTraceId();
        MDC.put("traceId", traceId);
        
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            logger.info("Fetching all entries");
            
            // Try to get from cache first
            String cachedData = redisTemplate.opsForValue().get(ALL_ENTRIES_CACHE_KEY);
            
            if (cachedData != null) {
                logger.info("Serving all entries from Redis cache");
                cacheHitCounter.increment();
                entriesReadCounter.increment();
                return objectMapper.readValue(cachedData, 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Entry.class));
            } else {
                logger.info("Cache miss: No cache found for all entries, fetching from database");
                cacheMissCounter.increment();
            }
            
            // Fetch from database with timing
            List<Entry> entries = databaseTimer.record(() -> entryRepository.findAll());
            
            // Cache the result
            logger.info("Serving all entries from Database and caching the result");
            String jsonData = objectMapper.writeValueAsString(entries);
            redisTemplate.opsForValue().set(ALL_ENTRIES_CACHE_KEY, jsonData, CACHE_TTL, TimeUnit.SECONDS);
            
            entriesReadCounter.increment(entries.size());
            logger.info("Retrieved {} entries from database", entries.size());
            
            return entries;
            
        } catch (JsonProcessingException e) {
            logger.error("Error processing JSON for cache", e);
            jsonErrorCounter.increment();
            // Fallback to database only
            return entryRepository.findAll();
        } catch (Exception e) {
            logger.error("Redis Fetch Error", e);
            redisErrorCounter.increment();
            // Fallback to database only
            return entryRepository.findAll();
        } finally {
            sample.stop(Timer.builder("app.get_all_entries.duration")
                    .tag("operation", "getAllEntries")
                    .register(meterRegistry));
            MDC.clear();
        }
    }
    
    public Entry getEntryById(Long id) {
        String traceId = generateTraceId();
        MDC.put("traceId", traceId);
        
        Timer.Sample sample = Timer.start(meterRegistry);
        String cacheKey = ENTRY_CACHE_KEY_PREFIX + id;
        
        try {
            logger.info("Fetching entry by ID: {}", id);
            
            // Try to get from cache first
            String cachedData = redisTemplate.opsForValue().get(cacheKey);
            
            if (cachedData != null) {
                logger.info("Serving entry {} from Redis cache", id);
                cacheHitCounter.increment();
                entriesReadCounter.increment();
                return objectMapper.readValue(cachedData, Entry.class);
            } else {
                logger.info("Cache miss: No cache found for entry {}, fetching from database", id);
                cacheMissCounter.increment();
            }
            
            // Fetch from database
            Optional<Entry> entry = databaseTimer.record(() -> entryRepository.findById(id));
            
            if (entry.isPresent()) {
                // Cache the result
                logger.info("Serving entry {} from Database and caching the result", id);
                String jsonData = objectMapper.writeValueAsString(entry.get());
                redisTemplate.opsForValue().set(cacheKey, jsonData, CACHE_TTL, TimeUnit.SECONDS);
                
                entriesReadCounter.increment();
                return entry.get();
            }
            
            logger.warn("Entry with ID {} not found", id);
            return null;
            
        } catch (JsonProcessingException e) {
            logger.error("Error processing JSON for cache", e);
            jsonErrorCounter.increment();
            // Fallback to database only
            return entryRepository.findById(id).orElse(null);
        } catch (Exception e) {
            logger.error("Redis Fetch Error for entry {}", id, e);
            redisErrorCounter.increment();
            // Fallback to database only
            return entryRepository.findById(id).orElse(null);
        } finally {
            sample.stop(Timer.builder("app.get_entry_by_id.duration")
                    .tag("operation", "getEntryById")
                    .tag("entryId", id.toString())
                    .register(meterRegistry));
            MDC.clear();
        }
    }
    
    public Entry createEntry(Entry entry) {
        String traceId = generateTraceId();
        MDC.put("traceId", traceId);
        
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            logger.info("Creating new entry: amount={}, description={}", 
                entry.getAmount(), entry.getDescription());
            
            Entry savedEntry = databaseTimer.record(() -> entryRepository.save(entry));
            
            entriesCreatedCounter.increment();
            logger.info("Inserted entry with ID: {}", savedEntry.getId());
            
            // Clear the cache because data changed
            clearAllEntriesCache();
            
            return savedEntry;
        } catch (Exception e) {
            logger.error("Error creating entry", e);
            throw e;
        } finally {
            sample.stop(Timer.builder("app.create_entry.duration")
                    .tag("operation", "createEntry")
                    .register(meterRegistry));
            MDC.clear();
        }
    }
    
    public Entry updateEntry(Long id, Entry entryDetails) {
        String traceId = generateTraceId();
        MDC.put("traceId", traceId);
        
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            logger.info("Updating entry with ID: {}", id);
            
            Optional<Entry> optionalEntry = databaseTimer.record(() -> entryRepository.findById(id));
            
            if (optionalEntry.isPresent()) {
                Entry existingEntry = optionalEntry.get();
                existingEntry.setAmount(entryDetails.getAmount());
                existingEntry.setDescription(entryDetails.getDescription());
                existingEntry.setDate(entryDetails.getDate());
                
                Entry updatedEntry = databaseTimer.record(() -> entryRepository.save(existingEntry));
                entriesUpdatedCounter.increment();
                
                logger.info("Updated entry with ID: {}", id);
                
                // Clear relevant caches because data changed
                clearAllEntriesCache();
                clearEntryCache(id);
                
                return updatedEntry;
            }
            
            logger.warn("Update failed: Entry with ID {} not found", id);
            return null;
            
        } catch (Exception e) {
            logger.error("Error updating entry with ID: {}", id, e);
            throw e;
        } finally {
            sample.stop(Timer.builder("app.update_entry.duration")
                    .tag("operation", "updateEntry")
                    .tag("entryId", id.toString())
                    .register(meterRegistry));
            MDC.clear();
        }
    }
    
    public boolean deleteEntry(Long id) {
        String traceId = generateTraceId();
        MDC.put("traceId", traceId);
        
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            logger.info("Deleting entry with ID: {}", id);
            
            Optional<Entry> entry = databaseTimer.record(() -> entryRepository.findById(id));
            
            if (entry.isPresent()) {
                databaseTimer.record(() -> entryRepository.deleteById(id));
                entriesDeletedCounter.increment();
                
                logger.info("Deleted entry with ID: {}", id);
                
                // Clear relevant caches because data changed
                clearAllEntriesCache();
                clearEntryCache(id);
                
                return true;
            }
            
            logger.warn("Delete failed: Entry with ID {} not found", id);
            return false;
            
        } catch (Exception e) {
            logger.error("Error deleting entry with ID: {}", id, e);
            throw e;
        } finally {
            sample.stop(Timer.builder("app.delete_entry.duration")
                    .tag("operation", "deleteEntry")
                    .tag("entryId", id.toString())
                    .register(meterRegistry));
            MDC.clear();
        }
    }
    
    private void clearAllEntriesCache() {
        try {
            redisTemplate.delete(ALL_ENTRIES_CACHE_KEY);
            logger.info("Cache cleared for {}", ALL_ENTRIES_CACHE_KEY);
        } catch (Exception e) {
            logger.error("Error clearing all entries cache", e);
            redisErrorCounter.increment();
        }
    }
    
    private void clearEntryCache(Long id) {
        try {
            String cacheKey = ENTRY_CACHE_KEY_PREFIX + id;
            redisTemplate.delete(cacheKey);
            logger.info("Cache cleared for {}", cacheKey);
        } catch (Exception e) {
            logger.error("Error clearing entry cache for ID: {}", id, e);
            redisErrorCounter.increment();
        }
    }
    
    // Optional: Method to clear all caches (useful for maintenance)
    public void clearAllCaches() {
        try {
            // Clear all entries cache
            redisTemplate.delete(ALL_ENTRIES_CACHE_KEY);
            logger.info("All caches cleared");
        } catch (Exception e) {
            logger.error("Error clearing all caches", e);
            redisErrorCounter.increment();
        }
    }
    
    private String generateTraceId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}