package be.howest.ti.game.logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InfoTest {
    @Test
    void testAuthors() {
        String[] authors = Info.getAuthors();
        assertEquals(5, authors.length);
        assertEquals("Lohani Pratik", authors[0]);
        assertEquals("Taris Wouter", authors[1]);
        assertEquals("Van Mulders Michiel", authors[2]);
        assertEquals("Vermeulen Sebastiaan", authors[3]);
        assertEquals("Windens Jitse", authors[4]);
    }

    @Test
    void testConstructor() {
        assertThrows(UnsupportedOperationException.class, Info::new);
    }
}
