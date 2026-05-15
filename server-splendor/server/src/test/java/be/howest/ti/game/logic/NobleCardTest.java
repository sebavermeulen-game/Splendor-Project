package be.howest.ti.game.logic;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class NobleCardTest {
    @Test
    void testNobleCardConstructorInitializesPrestigePoints() {
        Map<GemType, Integer> neededBonuses = new EnumMap<>(GemType.class);
        neededBonuses.put(GemType.RUBY, 4);
        neededBonuses.put(GemType.EMERALD, 4);

        NobleCard nobleCard = new NobleCard("Hera", neededBonuses, 3);
        assertEquals(3, nobleCard.getPrestigePoints());
    }

    @Test
    void testNobleCardConstructorInitializesName() {
        Map<GemType, Integer> neededBonuses = new EnumMap<>(GemType.class);
        NobleCard nobleCard = new NobleCard("Hera", neededBonuses, 3);
        assertEquals("Hera", nobleCard.getName());
    }

    @Test
    void testNobleCardConstructorInitializesNeededBonuses() {
        Map<GemType, Integer> neededBonuses = new EnumMap<>(GemType.class);
        neededBonuses.put(GemType.RUBY, 4);
        neededBonuses.put(GemType.EMERALD, 4);

        NobleCard nobleCard = new NobleCard("Hera", neededBonuses, 3);
        assertEquals(neededBonuses, nobleCard.getNeededBonuses());
        assertEquals(4, nobleCard.getNeededBonuses().get(GemType.RUBY));
        assertEquals(4, nobleCard.getNeededBonuses().get(GemType.EMERALD));
    }

    @Test
    void testLoadNoblesFromFile() {
        List<NobleCard> nobles;
        try {
            nobles = NobleCard.loadNoblesFromFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertNotNull(nobles);
        assertFalse(nobles.isEmpty());

        assertTrue(nobles.size() >= 5, "At least 5 nobles should be loaded from the file");

        NobleCard firstNoble = nobles.getFirst();
        assertNotNull(firstNoble.getName());
        assertNotNull(firstNoble.getNeededBonuses());
        assertTrue(firstNoble.getPrestigePoints() > 0);
    }
}
