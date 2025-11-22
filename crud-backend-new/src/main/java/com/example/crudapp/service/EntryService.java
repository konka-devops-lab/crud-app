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
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class EntryService {
    
    private static final Logger logger = LoggerFactory.getLogger(EntryService.class);
    private static final String ALL_ENTRIES_CACHE_KEY = "all_entries";
    private static final String ENTRY_CACHE_KEY_PREFIX = "entry_";
    private static final int CACHE_TTL = 60; // seconds
    
    // OpenTelemetry Tracer
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("entry-service");
    
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
    private final Counter cacheClearCounter;
    private final Counter redisErrorCounter;
    private final Counter jsonErrorCounter;
    
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
        this.cacheClearCounter = Counter.builder("app.cache.clear")
                .description("Number of cache clears")
                .register(meterRegistry);
        this.redisErrorCounter = Counter.builder("app.redis.errors")
                .description("Number of Redis errors")
                .register(meterRegistry);
        this.jsonErrorCounter = Counter.builder("app.json.errors")
                .description("Number of JSON processing errors")
                .register(meterRegistry);
    }
    
    public List<Entry> getAllEntries() {
        // Start OpenTelemetry span
        Span span = tracer.spanBuilder("EntryService.getAllEntries").startSpan();
        
        try (Scope scope = span.makeCurrent()) {
            logger.info("Fetching all entries");
            
            // Add span attributes
            span.setAttribute("operation", "getAllEntries");
            span.setAttribute("component", "service");
            span.setAttribute("cache.key", ALL_ENTRIES_CACHE_KEY);
            
            // Try to get from cache first
            String cachedData = redisTemplate.opsForValue().get(ALL_ENTRIES_CACHE_KEY);
            
            if (cachedData != null) {
                logger.info("Serving all entries from Redis cache");
                cacheHitCounter.increment();
                span.setAttribute("cache.hit", true);
                entriesReadCounter.increment();
                return objectMapper.readValue(cachedData, 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Entry.class));
            } else {
                logger.info("Cache miss: No cache found for all entries, fetching from database");
                cacheMissCounter.increment();
                span.setAttribute("cache.hit", false);
            }
            
            // Fetch from database
            List<Entry> entries = entryRepository.findAll();
            
            // Cache the result
            logger.info("Serving all entries from Database and caching the result");
            String jsonData = objectMapper.writeValueAsString(entries);
            redisTemplate.opsForValue().set(ALL_ENTRIES_CACHE_KEY, jsonData, CACHE_TTL, TimeUnit.SECONDS);
            
            span.setAttribute("entries.count", entries.size());
            entriesReadCounter.increment(entries.size());
            logger.info("Retrieved {} entries from database", entries.size());
            
            return entries;
            
        } catch (JsonProcessingException e) {
            logger.error("Error processing JSON for cache", e);
            jsonErrorCounter.increment();
            span.recordException(e);
            span.setAttribute("error", true);
            span.setAttribute("error.type", "json_processing");
            // Fallback to database only
            return entryRepository.findAll();
        } catch (Exception e) {
            logger.error("Redis Fetch Error", e);
            redisErrorCounter.increment();
            span.recordException(e);
            span.setAttribute("error", true);
            span.setAttribute("error.type", "redis_error");
            // Fallback to database only
            return entryRepository.findAll();
        } finally {
            span.end();
        }
    }
    
    public Entry getEntryById(Long id) {
        String cacheKey = ENTRY_CACHE_KEY_PREFIX + id;
        Span span = tracer.spanBuilder("EntryService.getEntryById").startSpan();
        
        try (Scope scope = span.makeCurrent()) {
            logger.info("Fetching entry by ID: {}", id);
            
            span.setAttribute("operation", "getEntryById");
            span.setAttribute("component", "service");
            span.setAttribute("entry.id", id);
            span.setAttribute("cache.key", cacheKey);
            
            // Try to get from cache first
            String cachedData = redisTemplate.opsForValue().get(cacheKey);
            
            if (cachedData != null) {
                logger.info("Serving entry {} from Redis cache", id);
                cacheHitCounter.increment();
                span.setAttribute("cache.hit", true);
                entriesReadCounter.increment();
                return objectMapper.readValue(cachedData, Entry.class);
            } else {
                logger.info("Cache miss: No cache found for entry {}, fetching from database", id);
                cacheMissCounter.increment();
                span.setAttribute("cache.hit", false);
            }
            
            // Fetch from database
            Optional<Entry> entry = entryRepository.findById(id);
            
            if (entry.isPresent()) {
                // Cache the result
                logger.info("Serving entry {} from Database and caching the result", id);
                String jsonData = objectMapper.writeValueAsString(entry.get());
                redisTemplate.opsForValue().set(cacheKey, jsonData, CACHE_TTL, TimeUnit.SECONDS);
                
                entriesReadCounter.increment();
                span.setAttribute("entry.found", true);
                return entry.get();
            }
            
            logger.warn("Entry with ID {} not found", id);
            span.setAttribute("entry.found", false);
            return null;
            
        } catch (JsonProcessingException e) {
            logger.error("Error processing JSON for cache", e);
            jsonErrorCounter.increment();
            span.recordException(e);
            span.setAttribute("error", true);
            span.setAttribute("error.type", "json_processing");
            // Fallback to database only
            return entryRepository.findById(id).orElse(null);
        } catch (Exception e) {
            logger.error("Redis Fetch Error for entry {}", id, e);
            redisErrorCounter.increment();
            span.recordException(e);
            span.setAttribute("error", true);
            span.setAttribute("error.type", "redis_error");
            // Fallback to database only
            return entryRepository.findById(id).orElse(null);
        } finally {
            span.end();
        }
    }
    
    public Entry createEntry(Entry entry) {
        Span span = tracer.spanBuilder("EntryService.createEntry").startSpan();
        
        try (Scope scope = span.makeCurrent()) {
            logger.info("Creating new entry: amount={}, description={}", 
                entry.getAmount(), entry.getDescription());
            
            span.setAttribute("operation", "createEntry");
            span.setAttribute("component", "service");
            span.setAttribute("entry.amount", entry.getAmount());
            span.setAttribute("entry.description", entry.getDescription());
            if (entry.getDate() != null) {
                span.setAttribute("entry.date", entry.getDate().toString());
            }
            
            Entry savedEntry = entryRepository.save(entry);
            
            entriesCreatedCounter.increment();
            span.setAttribute("entry.id", savedEntry.getId());
            
            logger.info("Inserted entry with ID: {}", savedEntry.getId());
            
            // Clear the cache because data changed
            clearAllEntriesCache();
            
            return savedEntry;
        } catch (Exception e) {
            logger.error("Error creating entry", e);
            span.recordException(e);
            span.setAttribute("error", true);
            throw e;
        } finally {
            span.end();
        }
    }
    
    public Entry updateEntry(Long id, Entry entryDetails) {
        Span span = tracer.spanBuilder("EntryService.updateEntry").startSpan();
        
        try (Scope scope = span.makeCurrent()) {
            logger.info("Updating entry with ID: {}", id);
            
            span.setAttribute("operation", "updateEntry");
            span.setAttribute("component", "service");
            span.setAttribute("entry.id", id);
            span.setAttribute("entry.amount", entryDetails.getAmount());
            span.setAttribute("entry.description", entryDetails.getDescription());
            if (entryDetails.getDate() != null) {
                span.setAttribute("entry.date", entryDetails.getDate().toString());
            }
            
            Optional<Entry> optionalEntry = entryRepository.findById(id);
            
            if (optionalEntry.isPresent()) {
                Entry existingEntry = optionalEntry.get();
                existingEntry.setAmount(entryDetails.getAmount());
                existingEntry.setDescription(entryDetails.getDescription());
                existingEntry.setDate(entryDetails.getDate());
                
                Entry updatedEntry = entryRepository.save(existingEntry);
                entriesUpdatedCounter.increment();
                
                logger.info("Updated entry with ID: {}", id);
                
                // Clear relevant caches because data changed
                clearAllEntriesCache();
                clearEntryCache(id);
                
                return updatedEntry;
            }
            
            logger.warn("Update failed: Entry with ID {} not found", id);
            span.setAttribute("entry.found", false);
            return null;
            
        } catch (Exception e) {
            logger.error("Error updating entry with ID: {}", id, e);
            span.recordException(e);
            span.setAttribute("error", true);
            throw e;
        } finally {
            span.end();
        }
    }
    
    public boolean deleteEntry(Long id) {
        Span span = tracer.spanBuilder("EntryService.deleteEntry").startSpan();
        
        try (Scope scope = span.makeCurrent()) {
            logger.info("Deleting entry with ID: {}", id);
            
            span.setAttribute("operation", "deleteEntry");
            span.setAttribute("component", "service");
            span.setAttribute("entry.id", id);
            
            Optional<Entry> entry = entryRepository.findById(id);
            
            if (entry.isPresent()) {
                entryRepository.deleteById(id);
                entriesDeletedCounter.increment();
                
                logger.info("Deleted entry with ID: {}", id);
                
                // Clear relevant caches because data changed
                clearAllEntriesCache();
                clearEntryCache(id);
                
                span.setAttribute("entry.deleted", true);
                return true;
            }
            
            logger.warn("Delete failed: Entry with ID {} not found", id);
            span.setAttribute("entry.found", false);
            return false;
            
        } catch (Exception e) {
            logger.error("Error deleting entry with ID: {}", id, e);
            span.recordException(e);
            span.setAttribute("error", true);
            throw e;
        } finally {
            span.end();
        }
    }
    
    private void clearAllEntriesCache() {
        Span span = tracer.spanBuilder("EntryService.clearAllEntriesCache").startSpan();
        
        try (Scope scope = span.makeCurrent()) {
            span.setAttribute("operation", "clearCache");
            span.setAttribute("cache.key", ALL_ENTRIES_CACHE_KEY);
            
            redisTemplate.delete(ALL_ENTRIES_CACHE_KEY);
            cacheClearCounter.increment();
            
            logger.info("Cache cleared for {}", ALL_ENTRIES_CACHE_KEY);
            span.addEvent("Cache cleared successfully");
        } catch (Exception e) {
            logger.error("Error clearing all entries cache", e);
            redisErrorCounter.increment();
            span.recordException(e);
            span.setAttribute("error", true);
        } finally {
            span.end();
        }
    }
    
    private void clearEntryCache(Long id) {
        Span span = tracer.spanBuilder("EntryService.clearEntryCache").startSpan();
        
        try (Scope scope = span.makeCurrent()) {
            String cacheKey = ENTRY_CACHE_KEY_PREFIX + id;
            span.setAttribute("operation", "clearCache");
            span.setAttribute("cache.key", cacheKey);
            span.setAttribute("entry.id", id);
            
            redisTemplate.delete(cacheKey);
            cacheClearCounter.increment();
            
            logger.info("Cache cleared for {}", cacheKey);
            span.addEvent("Entry cache cleared successfully");
        } catch (Exception e) {
            logger.error("Error clearing entry cache for ID: {}", id, e);
            redisErrorCounter.increment();
            span.recordException(e);
            span.setAttribute("error", true);
        } finally {
            span.end();
        }
    }
    
    // Optional: Method to clear all caches (useful for maintenance)
    public void clearAllCaches() {
        Span span = tracer.spanBuilder("EntryService.clearAllCaches").startSpan();
        
        try (Scope scope = span.makeCurrent()) {
            span.setAttribute("operation", "clearAllCaches");
            
            // Clear all entries cache
            redisTemplate.delete(ALL_ENTRIES_CACHE_KEY);
            cacheClearCounter.increment();
            
            // Note: In production, you might want to use Redis patterns to delete all entry_* keys
            logger.info("All caches cleared");
            span.addEvent("All caches cleared successfully");
        } catch (Exception e) {
            logger.error("Error clearing all caches", e);
            redisErrorCounter.increment();
            span.recordException(e);
            span.setAttribute("error", true);
        } finally {
            span.end();
        }
    }
}