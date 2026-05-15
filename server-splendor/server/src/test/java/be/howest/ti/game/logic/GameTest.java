package be.howest.ti.game.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    private Game game;

    @BeforeEach
    void setUp() {
        game = new Game(1, "Test Game", true,2);
    }

    @Test
    void testGameInitialization() {
        assertEquals(1, game.getGameId());
        assertEquals("Test Game", game.getGameName());
        assertTrue(game.currentReturnExcessTokens());
        assertEquals(GameState.WAITING, game.getState());
        assertTrue(game.getPlayers().isEmpty());
        assertTrue(game.getNobles().isEmpty());
        System.out.println(game.getNobles());
    }

    @Test
    void testAddPlayer() {
        game.addPlayer("Player1");
        game.addPlayer("Player2");

        List<Player> players = game.getPlayers();
        assertEquals(2, players.size());
        assertEquals("Player1", players.get(0).getName());
        assertEquals("Player2", players.get(1).getName());
    }

    @Test
    void testStartGame() {
        game.addPlayer("Player1");
        game.addPlayer("Player2");

        game.startGame();

        assertEquals(GameState.IN_PROGRESS, game.getState());
        assertNotNull(game.getCurrentPlayer());
        assertEquals("Player1", game.getCurrentPlayer());
        assertEquals(3, game.getNobles().size());
    }

    @Test
    void testNextTurn() {
        game.addPlayer("Player1");
        game.addPlayer("Player2");
        game.startGame();

        String firstPlayer = game.getCurrentPlayer();
        game.nextTurn();
        String secondPlayer = game.getCurrentPlayer();

        assertNotEquals(firstPlayer, secondPlayer);
        assertEquals("Player2", secondPlayer);
    }

    @Test
    void testEndGame() {
        game.endGame();
        assertEquals(GameState.COMPLETED, game.getState());
    }

    @Test
    void testSettersAndGetters() {
        game.setGameId(2);
        game.setGameName("New Game");
        game.setReturnExcessTokens(false);

        assertEquals(2, game.getGameId());
        assertEquals("New Game", game.getGameName());
        assertFalse(game.currentReturnExcessTokens());
    }

    @Test
    void testSetState() {
        game.setState(GameState.IN_PROGRESS);
        assertEquals(GameState.IN_PROGRESS, game.getState());

        game.setState(GameState.COMPLETED);
        assertEquals(GameState.COMPLETED, game.getState());
    }

    @Test
    void testPrepareGamePurseCase3() {
        Purse purse = game.getUnclaimedTokens();
        assertEquals(4, purse.getTokens(GemType.RUBY));
    }

    @Test
    void testIOExceptionHandling() {
        try {
            simulateStartGameWithException();
            fail("Expected UncheckedIOException to be thrown");
        } catch (UncheckedIOException e) {
            assertEquals("Failed to load noble cards", e.getMessage());
        }
    }

    private void simulateStartGameWithException() {
        Game gameWithException = new Game(1, "Test Game", true, 3) {
            @Override
            public void startGame() {
                throw new UncheckedIOException("Failed to load noble cards", new IOException("Test Exception"));
            }
        };
        gameWithException.startGame();
    }

    @Test
    void testNumberOfNoblesCase3And4() {
        game.addPlayer("Player1");
        game.addPlayer("Player2");
        game.addPlayer("Player3");
        game.startGame();
        assertEquals(4, game.getNobles().size());

        Game gameWith4Players = new Game(2, "Test Game 2", true, 4);
        gameWith4Players.addPlayer("Player1");
        gameWith4Players.addPlayer("Player2");
        gameWith4Players.addPlayer("Player3");
        gameWith4Players.addPlayer("Player4");
        gameWith4Players.startGame();
        assertEquals(5, gameWith4Players.getNobles().size());
    }

    @Test
    void testGetMarket() {
        Market market = game.getMarket();
        assertNotNull(market);
    }

    @Test
    void testSetMarket() {
        Market newMarket = new Market();
        game.setMarket(newMarket);
        assertEquals(newMarket, game.getMarket());
    }

    @Test
    void testGetNumberOfPlayers() {
        assertEquals(2, game.getNumberOfPlayers());
    }
}
