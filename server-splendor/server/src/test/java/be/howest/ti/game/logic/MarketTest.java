package be.howest.ti.game.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MarketTest {

    private Market market;

    @BeforeEach
    void setUp() {
        market = new Market();
    }

    @Test
    void testMarketInitialization() {
        assertNotNull(market, "Market should be initialized");
        List<Tier> visibleCards = market.getVisibleCards();
        assertEquals(3, visibleCards.size(), "Market should have 3 tiers");

        for (int i = 0; i < visibleCards.size(); i++) {
            Tier tier = visibleCards.get(i);
            assertNotNull(tier, "Tier should not be null");
            assertEquals("tier" + (i + 1), tier.getName(), "Tier name should match expected format");
            assertEquals(4, tier.getCards().size(), "Each tier should have 4 visible cards");
        }
    }

    @Test
    void testTiersAreFilledCorrectly() {
        List<Tier> visibleCards = market.getVisibleCards();

        for (int i = 0; i < visibleCards.size(); i++) {
            Tier tier = visibleCards.get(i);
            assertNotNull(tier, "Tier should not be null");
            assertEquals("tier" + (i + 1), tier.getName(), "Tier name should match expected format");
            assertEquals(4, tier.getCards().size(), "Each tier should have 4 visible cards");
        }
    }

    @Test
    void testBuyDevelopmentCard() {
        List<Tier> visibleCards = market.getVisibleCards();
        Tier tier1 = visibleCards.get(0);
        Set<DevelopmentCard> tier1Cards = tier1.getCards();
        List<DevelopmentCard> temporaryCards = new ArrayList<>(tier1Cards);

        assertFalse(tier1Cards.isEmpty(), "Tier 1 should have cards");

        DevelopmentCard cardToBuy = temporaryCards.get(0);
        Player player = new Player("TestPlayer");

        market.buyDevelopmentCard(cardToBuy, player);

        assertFalse(tier1.getCards().contains(cardToBuy), "Card should be removed from the tier after purchase");
        assertTrue(player.getCards().contains(cardToBuy), "Player should have the purchased card");
        assertEquals(4, tier1.getCards().size(), "Tier should still have 4 cards after replacing the purchased card");
    }

    @Test
    void testReserveCard() {
        List<Tier> visibleCards = market.getVisibleCards();
        Tier tier1 = visibleCards.get(0);
        Set<DevelopmentCard> tier1Cards = tier1.getCards();
        List<DevelopmentCard> temporaryCards = new ArrayList<>(tier1Cards);

        assertFalse(tier1Cards.isEmpty(), "Tier 1 should have cards");

        DevelopmentCard cardToReserve = temporaryCards.get(0);
        Player player = new Player("TestPlayer");

        player.getReserved().add(cardToReserve);
        tier1.getCards().remove(cardToReserve);

        assertTrue(player.getReserved().contains(cardToReserve), "Player should have the reserved card");
        assertFalse(tier1.getCards().contains(cardToReserve), "Reserved card should be removed from the tier");
    }

    @Test
    void testAddNewCardAfterReservation() {
        List<Tier> visibleCards = market.getVisibleCards();
        Tier tier1 = visibleCards.get(0);
        Set<DevelopmentCard> tier1Cards = tier1.getCards();
        List<DevelopmentCard> temporaryCards = new ArrayList<>(tier1Cards);

        assertFalse(tier1Cards.isEmpty(), "Tier 1 should have cards");

        DevelopmentCard cardToReserve = temporaryCards.get(0);
        tier1.getCards().remove(cardToReserve);

        market.buyDevelopmentCard(cardToReserve, new Player("TestPlayer"));

        assertEquals(4, tier1.getCards().size(), "Tier should still have 4 cards after replacing the reserved card");
    }
}