package be.howest.ti.game.logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PurseTest {

    @Test
    void testAddTokens() {
        Purse purse = new Purse();
        purse.addTokens(GemType.GOLD, 1);
        assertEquals(1, purse.getTokens(GemType.GOLD));
    }

    @Test
    void testRemoveTokens() {
        Purse purse = new Purse();
        purse.addTokens(GemType.SAPPHIRE, 5);
        purse.removeTokens(GemType.SAPPHIRE, 3);
        assertEquals(2, purse.getTokens(GemType.SAPPHIRE));
    }

    @Test
    void testRemoveTokensNotEnough() {
        Purse purse = new Purse();
        purse.addTokens(GemType.RUBY, 2);
        assertThrows(IllegalArgumentException.class, () -> purse.removeTokens(GemType.RUBY, 3));
    }

    @Test
    void testGetTokens() {
        Purse purse = new Purse();
        purse.addTokens(GemType.RUBY, 5);
        assertEquals(5, purse.getTokens(GemType.RUBY));
        assertEquals(0, purse.getTokens(GemType.SAPPHIRE));
    }

    @Test
    void testInvalidGemCode() {
        String invalidCode = "INVALID_CODE";
        assertThrows(IllegalArgumentException.class, () -> GemType.mapGemCodeToName(invalidCode));
    }
}
