package be.howest.ti.game.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameManagerTest {
    private GameManager gameManager;

    @BeforeEach
    void setUp() {
        gameManager = new GameManager();
    }

    @Test
    void testCreateGame() {
        Game game = gameManager.createGame("Test Game","alice",4);
        assertNotNull(game);
        assertEquals("Test Game", game.getGameName());
        assertEquals(1, game.getGameId());
    }

    @Test
    void testGetGame() {
        Game createdGame = gameManager.createGame("Test Game","alice",4);
        Game retrievedGame = gameManager.getGameById(createdGame.getGameId());
        assertNotNull(retrievedGame);
        assertEquals(createdGame, retrievedGame);
    }

    @Test
    void testListGames() {
        gameManager.createGame("Game 1","alice",4);
        gameManager.createGame("Game 2","alice",4);
        List<Game> games = gameManager.listGames();
        assertNotNull(games);
        assertEquals(2, games.size());
        assertEquals("Game 1", games.get(0).getGameName());
        assertEquals("Game 2", games.get(1).getGameName());
    }
}