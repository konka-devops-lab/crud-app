// package com.example.crudapp.model;

// import org.junit.jupiter.api.Test;

// import java.time.LocalDate;

// import static org.junit.jupiter.api.Assertions.*;

// class EntryTest {

//     @Test
//     void shouldCreateEntryWithConstructor() {
//         // Arrange & Act
//         // ========== RELEASE 2.0 - START (Constructor with Date) ==========
//         Entry entry = new Entry(100.0, "Test description", LocalDate.of(2024, 1, 15));
//         // ========== RELEASE 2.0 - END ==========
        
//         // Assert
//         assertNull(entry.getId());
//         assertEquals(100.0, entry.getAmount());
//         assertEquals("Test description", entry.getDescription());
//         // ========== RELEASE 2.0 - START (Date Assertion) ==========
//         assertEquals(LocalDate.of(2024, 1, 15), entry.getDate());
//         // ========== RELEASE 2.0 - END ==========
//     }

//     @Test
//     void shouldSetAndGetProperties() {
//         // Arrange
//         Entry entry = new Entry();
        
//         // Act
//         entry.setId(1L);
//         entry.setAmount(200.0);
//         entry.setDescription("Updated description");
//         // ========== RELEASE 2.0 - START (Date Setter/Getter) ==========
//         entry.setDate(LocalDate.of(2024, 1, 20));
//         // ========== RELEASE 2.0 - END ==========
        
//         // Assert
//         assertEquals(1L, entry.getId());
//         assertEquals(200.0, entry.getAmount());
//         assertEquals("Updated description", entry.getDescription());
//         // ========== RELEASE 2.0 - START (Date Assertion) ==========
//         assertEquals(LocalDate.of(2024, 1, 20), entry.getDate());
//         // ========== RELEASE 2.0 - END ==========
//     }

//     @Test
//     void toString_ShouldReturnNonEmptyString() {
//         // Arrange
//         // ========== RELEASE 2.0 - START (Entry with Date) ==========
//         Entry entry = new Entry(100.0, "Test", LocalDate.of(2024, 1, 15));
//         // ========== RELEASE 2.0 - END ==========
//         entry.setId(1L);
        
//         // Act
//         String result = entry.toString();
        
//         // Assert - Just verify it returns a non-empty string
//         assertNotNull(result);
//         assertFalse(result.isEmpty());
//     }
    
//     // ========== RELEASE 1.0 - START (Basic Constructor Test - Add this for Release 1.0) ==========
//     /*
//     @Test
//     void shouldCreateEntryWithBasicConstructor() {
//         // Arrange & Act - For Release 1.0 (without date)
//         Entry entry = new Entry(100.0, "Test description");
        
//         // Assert
//         assertNull(entry.getId());
//         assertEquals(100.0, entry.getAmount());
//         assertEquals("Test description", entry.getDescription());
//         // Note: Date would be null in Release 1.0
//     }
//     */
//     // ========== RELEASE 1.0 - END ==========
// }