package be.howest.ti.game.logic.service;


import be.howest.ti.game.logic.DevelopmentCard;
import be.howest.ti.game.logic.Game;
import be.howest.ti.game.logic.GemType;
import be.howest.ti.game.logic.Player;

import java.util.List;
import java.util.Map;

public interface SplendorService {

    List<Game> getGames(Boolean started);

    Game createGame(String gameName, String playerName, int numberOfPlayers);
    void deleteGames();

    Game getGameById(int gameId);

    void updateTokens(int gameId, String playerName, Map<GemType, Integer> tokens);

    void reserveDevelopment(int gameId, String playerName, String developmentName);

    void checkIfPlayerHasEnoughTokens(Player player, Map<GemType, Integer> payment, Game game);

    void handleReturnTokens(Game game, Player player, Map<GemType, Integer> tokens, DevelopmentCard card);
}
