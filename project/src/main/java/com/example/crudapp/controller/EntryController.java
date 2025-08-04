package com.example.crudapp.controller;

import com.example.crudapp.model.Entry;
import com.example.crudapp.service.EntryService;
import jakarta.validation.Valid;
import org.slf4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class EntryController {
    
    private static final Logger logger = LoggerFactory.getLogger(EntryController.class);
    
    @Autowired
    private EntryService entryService;
    
    @GetMapping("/entries")
    public ResponseEntity<List<Entry>> getAllEntries(HttpServletRequest request) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        MDC.put("endpoint", "GET /api/entries");
        MDC.put("clientIp", getClientIpAddress(request));
        
        try {
            logger.info("Fetching all entries");
            List<Entry> entries = entryService.getAllEntries();
            logger.info("Successfully fetched {} entries", entries.size());
            return ResponseEntity.ok(entries);
        } catch (Exception e) {
            logger.error("Error fetching entries", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } finally {
            MDC.clear();
        }
    }
    
    @PostMapping("/entries")
    public ResponseEntity<?> createEntry(@Valid @RequestBody Entry entry, HttpServletRequest request) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        MDC.put("endpoint", "POST /api/entries");
        MDC.put("clientIp", getClientIpAddress(request));
        
        try {
            if (entry.getAmount() == null || entry.getDescription() == null || entry.getDescription().trim().isEmpty()) {
                logger.warn("Invalid entry data: amount={}, description={}", entry.getAmount(), entry.getDescription());
                Map<String, String> error = new HashMap<>();
                error.put("error", "Amount and description are required");
                return ResponseEntity.badRequest().body(error);
            }
            
            logger.info("Creating new entry: amount={}, description={}", entry.getAmount(), entry.getDescription());
            Entry savedEntry = entryService.createEntry(entry);
            logger.info("Successfully created entry with ID: {}", savedEntry.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedEntry);
            
        } catch (Exception e) {
            logger.error("Error creating entry", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to insert entry");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        } finally {
            MDC.clear();
        }
    }
    
    @DeleteMapping("/entries/{id}")
    public ResponseEntity<?> deleteEntry(@PathVariable Long id, HttpServletRequest request) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        MDC.put("endpoint", "DELETE /api/entries/" + id);
        MDC.put("clientIp", getClientIpAddress(request));
        
        try {
            logger.info("Attempting to delete entry with ID: {}", id);
            boolean deleted = entryService.deleteEntry(id);
            
            if (deleted) {
                logger.info("Successfully deleted entry with ID: {}", id);
                Map<String, String> response = new HashMap<>();
                response.put("message", "Entry deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                logger.warn("Entry not found for deletion: ID={}", id);
                Map<String, String> error = new HashMap<>();
                error.put("error", "Entry not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            
        } catch (Exception e) {
            logger.error("Error deleting entry", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete entry");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        } finally {
            MDC.clear();
        }
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}