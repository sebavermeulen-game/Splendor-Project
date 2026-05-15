package be.howest.ti.game.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    private Player player;
    @BeforeEach
    void setUp() {
         player = new Player("Alice");
    }

    @Test
    void testPlayerConstructorInitializesName() {
        assertEquals("Alice", player.getName());
    }

    @Test
    void testPlayerConstructorInitializesEmptyPurse() {
        assertNotNull(player.getPurse());
    }

    @Test
    void testPlayerConstructorInitializesEmptyCards() {
        assertTrue(player.getCards().isEmpty());
    }

    @Test
    void testPlayerConstructorInitializesEmptyReservedCards() {
        assertTrue(player.getReserved().isEmpty());
    }

    @Test
    void testPlayerConstructorInitializesEmptyAcquiredNobles() {
        assertTrue(player.getAcquiredNobles().isEmpty());
    }

    @Test
    void testPlayerConstructorInitializesTotalPrestigePointsToZero() {
        assertEquals(0, player.getTotalPrestigePoints());
    }

    @Test
    void testGetBonuses() {
        assertTrue(player.getBonuses().isEmpty(), "Bonuses should initially be empty");

        player.getBonuses().put(GemType.RUBY, 2);
        player.getBonuses().put(GemType.SAPPHIRE, 1);

        assertEquals(2, player.getBonuses().get(GemType.RUBY));
        assertEquals(1, player.getBonuses().get(GemType.SAPPHIRE));
        assertEquals(0, player.getBonuses().getOrDefault(GemType.EMERALD, 0));
    }

    @Test
    void testRemove1GoldBonus() {
        player.addBonus(GemType.GOLD, 1);
        assertEquals(1, player.getBonuses().get(GemType.GOLD));
        player.remove1GoldBonus();
        assertEquals(0, player.getBonuses().get(GemType.GOLD));
    }
}