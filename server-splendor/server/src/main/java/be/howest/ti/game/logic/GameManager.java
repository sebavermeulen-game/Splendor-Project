package be.howest.ti.game.logic;
import java.util.Map;
import java.util.*;

public class GameManager {
    private  final Map<Integer, Game> games = new HashMap<>();
    private int nextGameId = 1;


    public Game createGame(String gameName, String playerName, int numberOfPlayers) {
        int gameId = nextGameId++;
        Game newGame = new Game(gameId, gameName, false, numberOfPlayers);
        newGame.addPlayer(playerName);
        games.put(gameId, newGame);
        return newGame;
    }

    public Game getGameById(int gameId) {
        return games.get(gameId);
    }

    public  List<Game> listGames() {
        return new ArrayList<>(games.values());
    }

    public void clearGames() {
        games.clear();
    }
}

