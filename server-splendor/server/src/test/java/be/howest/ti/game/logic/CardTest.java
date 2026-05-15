package be.howest.ti.game.logic;

import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {
    @Test
    void testCardConstructorInitializesPrestigePoints() {
        Card card = new Card(3);
        assertEquals(3, card.getPrestigePoints());
    }

    @Test
    void testCardConstructorInitializesEmptyCost() {
        Card card = new Card(3);
        for (GemType gem : GemType.values()) {
            assertEquals(0, card.getGemCostAmount(gem));
        }
    }

    @Test
    void testGetPrestigePoints() {
        Card card = new Card(5);
        assertEquals(5, card.getPrestigePoints());
    }

    @Test
    void testGetCost() {
        Card card = new Card(3);
        Map<GemType, Integer> cost = new EnumMap<>(GemType.class);
        cost.put(GemType.RUBY, 2);
        card.setCost(cost);

        assertEquals(cost, card.getCost());
    }

    @Test
    void testSetCost() {
        Card card = new Card(3);
        Map<GemType, Integer> cost = new EnumMap<>(GemType.class);
        cost.put(GemType.DIAMOND, 3);

        card.setCost(cost);

        assertEquals(3, card.getGemCostAmount(GemType.DIAMOND));
    }

    @Test
    void testToString() {
        Card card = new Card(5);
        Map<GemType, Integer> cost = new EnumMap<>(GemType.class);
        cost.put(GemType.SAPPHIRE, 1);
        card.setCost(cost);

        String expected = "{cost:{SAPPHIRE=1}, prestigePoints:5}";
        assertEquals(expected, card.toString());
    }

    @Test
    void testEquals() {
        Card card1 = new Card(4);
        Card card2 = new Card(4);

        Map<GemType, Integer> cost = new EnumMap<>(GemType.class);
        cost.put(GemType.EMERALD, 2);

        card1.setCost(cost);
        card2.setCost(cost);

        assertEquals(card1, card2);
    }

    @Test
    void testNotEquals() {
        Card card1 = new Card(4);
        Card card2 = new Card(5);

        assertNotEquals(card1, card2);
    }
}
