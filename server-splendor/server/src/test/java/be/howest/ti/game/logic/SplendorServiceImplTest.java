package be.howest.ti.game.logic;

import be.howest.ti.game.logic.service.SplendorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SplendorServiceImplTest {

    private SplendorServiceImpl service;
    private Game game;
    private Player player;

    @BeforeEach
    void setUp() {
        service = new SplendorServiceImpl();
        service.deleteGames();
        game = service.createGame("TestGame", "Alice", 2);
        game.addPlayer("Bob");
        game.setState(GameState.IN_PROGRESS);
        player = game.getspecificplayer("Alice");
    }

    @Test
    void testCreateGame() {
        service.deleteGames();
        Game game1 = service.createGame("TestCreateGame", "Alice",2);
        assertNotNull(game1);
        assertEquals(GameState.WAITING, game1.getState());
        assertEquals(1, game1.getPlayers().size());
    }

    @Test
    void testGetGamesNullFilter() {
        service.deleteGames();
        service.createGame("TestGame", "Alice",2);
        List<Game> games = service.getGames(null);
        assertEquals(1, games.size());
    }

    @Test
    void testGetGamesWaiting() {
        Game waitingGame = service.createGame("WaitingGame", "Bob",2);
        List<Game> waitingGames = service.getGames(false);
        assertTrue(waitingGames.contains(waitingGame));
    }

    @Test
    void testGetGamesInProgress() {
        List<Game> inProgressGames = service.getGames(true);
        assertTrue(inProgressGames.contains(game));
    }

    @Test
    void testDeleteGames() {
        assertFalse(service.getGames(null).isEmpty());
        service.deleteGames();
        assertTrue(service.getGames(null).isEmpty());
    }

    @Test
    void testReserveDevelopmentValid() {
        Map<GemType, Integer> cost = new EnumMap<>(GemType.class);
        cost.put(GemType.DIAMOND, 3);
        cost.put(GemType.SAPPHIRE, 2);
        cost.put(GemType.EMERALD, 2);
        DevelopmentCard card = new DevelopmentCard("Obsidian Vault", 2, GemType.ONYX, 1, cost);

        service.reserveDevelopment(game.getGameId(), "Alice", "Obsidian Vault");

        assertTrue(player.getReserved().contains(card));
        assertEquals(1, player.getReserved().size());
        assertEquals(1, player.getPurse().getTokens(GemType.GOLD));
        assertEquals(4, game.getUnclaimedTokens().getTokens(GemType.GOLD));
    }

    @Test
    void testReserveDevelopmentGameNotFound() {
        int missingGameId = 999;
        String playerName = "Alice";
        String cardName = "Obsidian Vault";

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        () -> service.reserveDevelopment(missingGameId, playerName, cardName));

        assertEquals("Game not found: " + missingGameId, exception.getMessage());
    }

    @Test
    void testReserveDevelopmentGameNotInProgress() {
        Game game3 = service.createGame("TestGame", "Alice", 2);
        int gameId = game3.getGameId();
        String playerName = "Alice";
        String cardName = "Obsidian Vault";

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        () -> service.reserveDevelopment(gameId, playerName, cardName));

        assertEquals("Game is not in progress", exception.getMessage());
    }

    @Test
    void testReserveDevelopmentPlayerNotFound() {
        Game game4 = service.createGame("TestGame", "Alice", 2);
        game4.setState(GameState.IN_PROGRESS);
        int gameId = game4.getGameId();
        String missingPlayer = "Bob";
        String cardName = "Obsidian Vault";

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        () -> service.reserveDevelopment(gameId, missingPlayer, cardName));

        assertEquals("Player not found: " + missingPlayer, exception.getMessage());
    }


    @Test
    void testReserveDevelopmentNotPlayersTurn() {
        int gameId = game.getGameId();
        String playerName = "Bob";
        String cardName = "Obsidian Vault";

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        () -> service.reserveDevelopment(gameId, playerName, cardName));

        assertEquals("It's not this player's turn", exception.getMessage());
    }

    @Test
    void testReserveDevelopmentPlayerReservedLimit() {
        Map<GemType, Integer> cost = new EnumMap<>(GemType.class);
        cost.put(GemType.SAPPHIRE, 2);
        cost.put(GemType.ONYX, 2);
        player.getReserved().add(new DevelopmentCard("Obsidian Vault", 1, GemType.DIAMOND, 3, cost));
        player.getReserved().add(new DevelopmentCard("Emerald Collection", 1, GemType.RUBY, 2, cost));
        player.getReserved().add(new DevelopmentCard("Onyx Refinery", 1, GemType.EMERALD, 1, cost));

        int gameId = game.getGameId();
        String playerName = "Alice";
        String newCard = "Diamond Mine";

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        () -> service.reserveDevelopment(gameId, playerName, newCard));

        assertEquals("Player can't reserve more than 3 cards", exception.getMessage());
    }

    @Test
    void testReserveDevelopmentCardNotFound() {
        int gameId = game.getGameId();
        String playerName = "Alice";
        String missingCard = "NonExistentCard";

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        () -> service.reserveDevelopment(gameId, playerName, missingCard));

        assertEquals("Development card not found: " + missingCard, exception.getMessage());
    }

    @Test
    void testUpdateTokensTakeValid() {
        Map<GemType, Integer> tokens = new EnumMap<>(GemType.class);
        tokens.put(GemType.DIAMOND, 1);
        tokens.put(GemType.SAPPHIRE, 1);
        tokens.put(GemType.EMERALD, 1);
        service.updateTokens(game.getGameId(), "Alice", tokens);
        assertEquals(1, player.getPurse().getTokens(GemType.DIAMOND));
        assertEquals(1, player.getPurse().getTokens(GemType.SAPPHIRE));
        assertEquals(1, player.getPurse().getTokens(GemType.EMERALD));
    }

    @Test
    void testUpdateTokensTakeGoldNotAllowed() {
        Map<GemType, Integer> tokens = new EnumMap<>(GemType.class);
        tokens.put(GemType.GOLD, 1);
        int gameId = game.getGameId();
        String playerName = "Alice";
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.updateTokens(gameId, playerName, tokens)
        );
        assertEquals("Cannot take Gold tokens directly.", ex.getMessage());
    }

    @Test
    void testUpdateTokensOverTenTokens() {
        for (GemType gem : GemType.values()) {
            player.getPurse().addTokens(gem, 2);
        }
        Map<GemType, Integer> tokens = new EnumMap<>(GemType.class);
        tokens.put(GemType.DIAMOND, 1);
        int gameId = game.getGameId();
        String playerName = "Alice";
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.updateTokens(gameId, playerName, tokens)
        );
        assertEquals("Cannot have more than 10 tokens in total.", ex.getMessage());
    }

    @Test
    void testCountTokens() {
        Map<GemType, Integer> tokens = new EnumMap<>(GemType.class);
        tokens.put(GemType.DIAMOND, 2);
        tokens.put(GemType.SAPPHIRE, -1);
        assertEquals(1, service.countTokens(tokens, true));
        assertEquals(1, service.countTokens(tokens, false));
    }

    @Test
    void testValidateTakeValid() {
        Map<GemType, Integer> tokens = new EnumMap<>(GemType.class);
        tokens.put(GemType.DIAMOND, 1);
        tokens.put(GemType.SAPPHIRE, 1);
        tokens.put(GemType.EMERALD, 1);
        assertDoesNotThrow(() -> service.validateTake(tokens, 3));
    }

    @Test
    void testValidateTakeInvalid() {
        Map<GemType, Integer> tokens = new EnumMap<>(GemType.class);
        tokens.put(GemType.DIAMOND, 2);
        tokens.put(GemType.SAPPHIRE, 2);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.validateTake(tokens, 2));
        assertEquals("Invalid token take move", ex.getMessage());
    }

    @Test
    void testGetGamesFilter() {
        service.deleteGames();
        Game waitingGame = service.createGame("WaitingGame", "Bob", 2);
        Game inProgressGame = service.createGame("InProgressGame", "Alice", 2);
        inProgressGame.setState(GameState.IN_PROGRESS);

        List<Game> allGames = service.getGames(null);
        List<Game> waitingGames = service.getGames(false);
        List<Game> inProgressGames = service.getGames(true);

        assertTrue(allGames.contains(waitingGame) && allGames.contains(inProgressGame));
        assertTrue(waitingGames.contains(waitingGame) && !waitingGames.contains(inProgressGame));
        assertTrue(inProgressGames.contains(inProgressGame) && !inProgressGames.contains(waitingGame));
    }

    @Test
    void testUpdateTokensPlayerNotFound() {
        Map<GemType, Integer> tokens = new EnumMap<>(GemType.class);
        int gameId = game.getGameId();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.updateTokens(gameId, "NonExistentPlayer", tokens));
        assertEquals("Player not found", ex.getMessage());
    }

    @Test
    void testUpdateTokensNotPlayersTurn() {
        Map<GemType, Integer> tokens = new EnumMap<>(GemType.class);
        int gameId = game.getGameId();
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.updateTokens(gameId, "Bob", tokens));
        assertEquals("It's not this player's turn", ex.getMessage());
    }

    @Test
    void testUpdateTokensTakeAndReturnTokens() {
        Map<GemType, Integer> tokens = new EnumMap<>(GemType.class);
        tokens.put(GemType.DIAMOND, 1);
        tokens.put(GemType.SAPPHIRE, -1);
        int gameId = game.getGameId();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.updateTokens(gameId, "Alice", tokens));
        assertEquals("Cannot take and return tokens in one move", ex.getMessage());
    }

    @Test
    void testUpdateTokensNoTokensToUpdate() {
        Map<GemType, Integer> tokens = new EnumMap<>(GemType.class);
        int gameId = game.getGameId();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.updateTokens(gameId, "Alice", tokens));
        assertEquals("No tokens to update", ex.getMessage());
    }

    @Test
    void testUpdateTokensInvalidTakeMove() {
        Map<GemType, Integer> tokens = new EnumMap<>(GemType.class);
        tokens.put(GemType.DIAMOND, 5);
        int gameId = game.getGameId();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.updateTokens(gameId, "Alice", tokens));
        assertEquals("Invalid token take move", ex.getMessage());
    }

    @Test
    void testReserveDevelopmentGoldTokenAwarded() {
        Map<GemType, Integer> cost = new EnumMap<>(GemType.class);
        cost.put(GemType.DIAMOND, 3);
        new DevelopmentCard("Obsidian Vault", 2, GemType.ONYX, 1, cost);

        service.reserveDevelopment(game.getGameId(), "Alice", "Obsidian Vault");

        assertEquals(1, player.getPurse().getTokens(GemType.GOLD));
        assertEquals(4, game.getUnclaimedTokens().getTokens(GemType.GOLD));
    }

    @Test
    void testBuyReservedDevelopmentValid() {
        Map<GemType, Integer> cost = new EnumMap<>(GemType.class);
        cost.put(GemType.DIAMOND, 2);
        cost.put(GemType.SAPPHIRE, 1);
        DevelopmentCard reservedCard = new DevelopmentCard("Obsidian Vault", 2, GemType.ONYX, 3, cost);
        player.getReserved().add(reservedCard);
        player.getPurse().addTokens(GemType.DIAMOND, 2);
        player.getPurse().addTokens(GemType.SAPPHIRE, 1);

        Map<GemType, Integer> payment = new EnumMap<>(GemType.class);
        payment.put(GemType.DIAMOND, 2);
        payment.put(GemType.SAPPHIRE, 1);

        service.checkIfPlayerHasEnoughTokens(player, payment, game);
        player.removeReserved(reservedCard);
        service.handleReturnTokens(game, player, payment, reservedCard);

        assertTrue(player.getCards().contains(reservedCard), "Player should have the reserved card in their cards");
        assertEquals(0, player.getPurse().getTokens(GemType.DIAMOND), "Player's Diamond tokens should be gone");
        assertEquals(0, player.getPurse().getTokens(GemType.SAPPHIRE), "Player's Sapphire tokens should be gone");
        assertEquals(1, player.getBonuses().get(GemType.ONYX), "Player should have the bonus from the card");
        assertEquals(3, player.getTotalPrestigePoints(), "Player should have the prestige points from the card");
    }

    @Test
    void testBuyDevelopmentValid() {
        Map<GemType, Integer> cost = new EnumMap<>(GemType.class);
        cost.put(GemType.EMERALD, 3);
        cost.put(GemType.RUBY, 2);
        DevelopmentCard cardToBuy = new DevelopmentCard("Emerald Tower", 1, GemType.DIAMOND, 2, cost);
        game.getMarket().getVisibleCards().getFirst().getCards().add(cardToBuy);
        player.getPurse().addTokens(GemType.EMERALD, 3);
        player.getPurse().addTokens(GemType.RUBY, 2);

        Map<GemType, Integer> payment = new EnumMap<>(GemType.class);
        payment.put(GemType.EMERALD, 3);
        payment.put(GemType.RUBY, 2);

        service.checkIfPlayerHasEnoughTokens(player, payment, game);
        game.getMarket().buyDevelopmentCard(cardToBuy, player);
        service.handleReturnTokens(game, player, payment, cardToBuy);

        assertTrue(player.getCards().contains(cardToBuy), "Player should have the purchased card in their cards");
        assertEquals(0, player.getPurse().getTokens(GemType.EMERALD), "Player's Emerald tokens should be gone");
        assertEquals(0, player.getPurse().getTokens(GemType.RUBY), "Player's Ruby tokens should be gone");
        assertEquals(1, player.getBonuses().get(GemType.DIAMOND), "Player should have the bonus from the card");
        assertEquals(2, player.getTotalPrestigePoints(), "Player should have the prestige points from the card");
    }
}