package com.example.crudapp.service;

import com.example.crudapp.model.Entry;
import com.example.crudapp.repository.EntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class EntryServiceTest {
    
    @Mock
    private EntryRepository entryRepository;
    
    @InjectMocks
    private EntryService entryService;
    
    private Entry entry;
    
    @BeforeEach
    void setUp() {
        entry = new Entry();
        entry.setId(1L);
        entry.setAmount(new BigDecimal("100.00"));
        entry.setDescription("Test entry");
    }
    
    @Test
    void getAllEntries_ShouldReturnAllEntries() {
        // Given
        Entry entry2 = new Entry();
        entry2.setId(2L);
        entry2.setAmount(new BigDecimal("200.00"));
        entry2.setDescription("Second entry");
        
        when(entryRepository.findAll()).thenReturn(Arrays.asList(entry, entry2));
        
        // When
        List<Entry> entries = entryService.getAllEntries();
        
        // Then
        assertThat(entries).hasSize(2);
        assertThat(entries.get(0).getAmount()).isEqualTo(new BigDecimal("100.00"));
        verify(entryRepository, times(1)).findAll();
    }
    
    @Test
    void getEntryById_WhenEntryExists_ShouldReturnEntry() {
        // Given
        when(entryRepository.findById(1L)).thenReturn(Optional.of(entry));
        
        // When
        Entry found = entryService.getEntryById(1L);
        
        // Then
        assertThat(found).isNotNull();
        assertThat(found.getDescription()).isEqualTo("Test entry");
        verify(entryRepository, times(1)).findById(1L);
    }
    
    @Test
    void getEntryById_WhenEntryNotExists_ShouldThrowException() {
        // Given
        when(entryRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(RuntimeException.class, () -> {
            entryService.getEntryById(999L);
        });
    }
    
    @Test
    void createEntry_ShouldSaveAndReturnEntry() {
        // Given
        when(entryRepository.save(any(Entry.class))).thenReturn(entry);
        
        // When
        Entry created = entryService.createEntry(entry);
        
        // Then
        assertThat(created).isNotNull();
        assertThat(created.getId()).isEqualTo(1L);
        verify(entryRepository, times(1)).save(any(Entry.class));
    }
    
    @Test
    void deleteEntry_ShouldCallRepositoryDelete() {
        // Given
        doNothing().when(entryRepository).deleteById(1L);
        
        // When
        entryService.deleteEntry(1L);
        
        // Then
        verify(entryRepository, times(1)).deleteById(1L);
    }
}