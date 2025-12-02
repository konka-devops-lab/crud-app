package com.example.crudapp.repository;

import com.example.crudapp.model.Entry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EntryRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private EntryRepository entryRepository;
    
    @Test
    void whenFindById_thenReturnEntry() {
        // Given
        Entry entry = new Entry();
        entry.setAmount(new BigDecimal("150.00"));
        entry.setDescription("Test entry");
        entityManager.persist(entry);
        entityManager.flush();
        
        // When
        Entry found = entryRepository.findById(entry.getId()).orElse(null);
        
        // Then
        assertThat(found).isNotNull();
        assertThat(found.getAmount()).isEqualTo(new BigDecimal("150.00"));
        assertThat(found.getDescription()).isEqualTo("Test entry");
    }
    
    @Test
    void whenSaveEntry_thenCanRetrieve() {
        Entry entry = new Entry();
        entry.setAmount(new BigDecimal("99.99"));
        entry.setDescription("Save test");
        
        Entry saved = entryRepository.save(entry);
        
        assertThat(saved.getId()).isNotNull();
        assertThat(entryRepository.count()).isEqualTo(1);
    }
    
    @Test
    void whenDeleteEntry_thenRemovedFromDatabase() {
        Entry entry = new Entry();
        entry.setAmount(new BigDecimal("50.00"));
        entry.setDescription("To be deleted");
        entityManager.persist(entry);
        entityManager.flush();
        
        Long id = entry.getId();
        entryRepository.deleteById(id);
        
        assertThat(entryRepository.findById(id)).isEmpty();
    }
}