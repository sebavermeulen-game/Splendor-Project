package be.howest.ti.game.logic;

import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class DevelopmentCardTest {

    @Test
    void testConstructorAndGetters() {
        String name = "Diamond Vein";
        int level = 1;
        GemType bonus = GemType.DIAMOND;
        int prestigePoints = 0;
        Map<GemType, Integer> cost = new EnumMap<>(GemType.class);
        cost.put(GemType.SAPPHIRE, 2);
        cost.put(GemType.ONYX, 2);

        DevelopmentCard card = new DevelopmentCard(name, level, bonus, prestigePoints, cost);

        assertEquals(name, card.getName());
        assertEquals(level, card.getLevel());
        assertEquals(bonus, card.getBonus());
        assertEquals(prestigePoints, card.getPrestigePoints());
        assertEquals(cost, card.getCost());
    }

    @Test
    void testEquals() {
        Map<GemType, Integer> cost1 = new EnumMap<>(GemType.class);
        cost1.put(GemType.SAPPHIRE, 2);
        cost1.put(GemType.ONYX, 2);

        Map<GemType, Integer> cost2 = new EnumMap<>(GemType.class);
        cost2.put(GemType.SAPPHIRE, 2);
        cost2.put(GemType.ONYX, 2);

        DevelopmentCard card1 = new DevelopmentCard("Diamond Vein", 1, GemType.DIAMOND, 0, cost1);
        DevelopmentCard card2 = new DevelopmentCard("Diamond Vein", 1, GemType.DIAMOND, 0, cost2);
        DevelopmentCard card3 = new DevelopmentCard("Ruby Mine", 2, GemType.RUBY, 1, cost1);

        assertEquals(card1, card2);

        assertNotEquals(card1, card3);
        assertNotEquals(card2, card3);
    }
}