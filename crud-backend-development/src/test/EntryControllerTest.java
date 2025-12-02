package com.example.crudapp.controller;

import com.example.crudapp.model.Entry;
import com.example.crudapp.service.EntryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.Arrays;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EntryController.class)
class EntryControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private EntryService entryService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void getAllEntries_ShouldReturnEntriesList() throws Exception {
        // Given
        Entry entry1 = Entry.builder()
            .id(1L)
            .amount(new BigDecimal("100.00"))
            .description("First entry")
            .build();
        
        Entry entry2 = Entry.builder()
            .id(2L)
            .amount(new BigDecimal("200.00"))
            .description("Second entry")
            .build();
        
        when(entryService.getAllEntries()).thenReturn(Arrays.asList(entry1, entry2));
        
        // When & Then
        mockMvc.perform(get("/api/entries")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].amount").value(100.00))
                .andExpect(jsonPath("$[0].description").value("First entry"));
        
        verify(entryService, times(1)).getAllEntries();
    }
    
    @Test
    void getEntryById_WhenExists_ShouldReturnEntry() throws Exception {
        // Given
        Entry entry = Entry.builder()
            .id(1L)
            .amount(new BigDecimal("150.50"))
            .description("Test entry")
            .build();
        
        when(entryService.getEntryById(1L)).thenReturn(entry);
        
        // When & Then
        mockMvc.perform(get("/api/entries/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(150.50))
                .andExpect(jsonPath("$.description").value("Test entry"));
        
        verify(entryService, times(1)).getEntryById(1L);
    }
    
    @Test
    void createEntry_ShouldReturnCreatedEntry() throws Exception {
        // Given
        Entry requestEntry = Entry.builder()
            .amount(new BigDecimal("99.99"))
            .description("New entry")
            .build();
        
        Entry responseEntry = Entry.builder()
            .id(1L)
            .amount(new BigDecimal("99.99"))
            .description("New entry")
            .build();
        
        when(entryService.createEntry(any(Entry.class))).thenReturn(responseEntry);
        
        // When & Then
        mockMvc.perform(post("/api/entries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestEntry)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(99.99))
                .andExpect(jsonPath("$.description").value("New entry"));
        
        verify(entryService, times(1)).createEntry(any(Entry.class));
    }
    
    @Test
    void deleteEntry_ShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(entryService).deleteEntry(1L);
        
        // When & Then
        mockMvc.perform(delete("/api/entries/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        
        verify(entryService, times(1)).deleteEntry(1L);
    }
    
    @Test
    void getEntryById_WhenNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        when(entryService.getEntryById(999L))
            .thenThrow(new RuntimeException("Entry not found"));
        
        // When & Then
        mockMvc.perform(get("/api/entries/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}