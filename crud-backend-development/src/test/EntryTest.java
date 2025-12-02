package com.example.crudapp.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class EntryTest {
    
    @Test
    void testEntryCreation() {
        Entry entry = new Entry();
        entry.setId(1L);
        entry.setAmount(new BigDecimal("100.50"));
        entry.setDescription("Test description");
        
        assertEquals(1L, entry.getId());
        assertEquals(new BigDecimal("100.50"), entry.getAmount());
        assertEquals("Test description", entry.getDescription());
    }
    
    @Test
    void testEntryBuilder() {
        Entry entry = Entry.builder()
            .id(1L)
            .amount(new BigDecimal("200.75"))
            .description("Builder test")
            .build();
        
        assertNotNull(entry);
        assertEquals(1L, entry.getId());
        assertEquals(new BigDecimal("200.75"), entry.getAmount());
    }
}