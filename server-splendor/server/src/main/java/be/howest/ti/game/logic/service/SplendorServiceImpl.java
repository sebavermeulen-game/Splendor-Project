package be.howest.ti.game.logic.service;

import be.howest.ti.game.logic.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SplendorServiceImpl implements SplendorService {
    private final GameManager gameManager = new GameManager();

    private static final String ERROR_MESSAGE = "It's not this player's turn";
    @Override
    public List<Game> getGames(Boolean started) {
        List<Game> allGames = gameManager.listGames();
        List<Game> filteredGames = new ArrayList<>();

        for (Game game : allGames) {
            if (started == null ||(started && game.getState() == GameState.IN_PROGRESS) || (!started && game.getState() == GameState.WAITING)) {
                filteredGames.add(game);
            }
        }
        return filteredGames;
    }

    @Override
    public  Game createGame(String gameName, String playerName, int numberOfPlayers) {
        return gameManager.createGame(gameName, playerName, numberOfPlayers);
    }
    @Override
    public void deleteGames()
    {
        gameManager.clearGames();
    }

    @Override
    public Game getGameById(int gameId) {
        return gameManager.getGameById(gameId);
    }


    @Override
    public void updateTokens(int gameId, String playerName, Map<GemType, Integer> tokens) {
        Game game = gameManager.getGameById(gameId);
        Player player = game.getspecificplayer(playerName);
        if (player == null) {
            throw new IllegalArgumentException("Player not found");
        }

        String currentPlayer = game.getCurrentPlayer();
        if (currentPlayer == null || !currentPlayer.equals(playerName)) {
            throw new IllegalStateException(ERROR_MESSAGE);
        }

        tokens.forEach((gem, amount) -> {
            if (gem == GemType.GOLD && amount > 0) {
                throw new IllegalArgumentException("Cannot take Gold tokens directly.");
            }
        });

        int currentTotal = 0;
        for (Integer amount : player.getPurse().getTokensMap().values()) {
            currentTotal += amount;
        }
        int netChange = 0;
        for (Integer amount : tokens.values()) {
            netChange += amount;
        }
        int newTotal = currentTotal + netChange;
        if (newTotal > 10) {
            throw new IllegalArgumentException("Cannot have more than 10 tokens in total.");
        }

        int takeCount = countTokens(tokens, true);
        int returnCount = countTokens(tokens, false);

        if (takeCount > 0 && returnCount > 0) {
            throw new IllegalArgumentException("Cannot take and return tokens in one move");
        }
        if (takeCount > 0) {
            handleTakeTokens(game, player, tokens, takeCount);
        } else {
            throw new IllegalArgumentException("No tokens to update");
        }
        game.nextTurn();
    }

    private void handleTakeTokens(Game game, Player player, Map<GemType, Integer> tokens, int takeCount) {
        Map<GemType, Integer> unclaimedTokens = game.getUnclaimedTokens().getTokensMap();
        validateTake(tokens, takeCount);

        tokens.forEach((gem, amount) -> {
            if (amount == 2) {
                int available = unclaimedTokens.getOrDefault(gem, 0);
                if (available < 4) {
                    throw new IllegalStateException("Not enough tokens to take 2 of the same type (need at least 4).");
                }
            }
        });

        tokens.forEach((gem, amount) -> {
            if (amount > 0) {
                int available = unclaimedTokens.getOrDefault(gem, 0);
                if (available < amount) {
                    throw new IllegalStateException("Not enough tokens available for " + gem);
                }
            }
        });

        tokens.forEach((gem, amount) -> {
            if (amount > 0) {
                player.getPurse().addTokens(gem, amount);
                unclaimedTokens.put(gem, unclaimedTokens.get(gem) - amount);
            }
        });
    }

    public void handleReturnTokens(Game game, Player player, Map<GemType, Integer> tokens, DevelopmentCard card) {
        Map<GemType, Integer> unclaimedTokens = game.getUnclaimedTokens().getTokensMap();
        tokens.forEach((gem, amount) -> {
                Map<GemType, Integer> playerBonuses = player.getBonuses();
                int bonus = gem == GemType.GOLD ? 0 : playerBonuses.getOrDefault(gem, 0);
                if (bonus < amount) {
                    player.getPurse().removeTokens(gem, amount - bonus);
                    unclaimedTokens.put(gem, unclaimedTokens.get(gem) + (amount - bonus));
                }
        });
        player.addBonus(card.getBonus(), 1);
        player.setTotalPrestigePoints(player.getTotalPrestigePoints() + card.getPrestigePoints());
    }

    public int countTokens(Map<GemType, Integer> tokens, boolean positive) {
        int count = 0;
        for (int tokenAmount : tokens.values()) {
            if ((positive && tokenAmount > 0) || (!positive && tokenAmount < 0)) count++;
        }
        return count;
    }

    public void validateTake(Map<GemType, Integer> tokens, int takeCount) {
        boolean hasTwo = false;
        boolean allOnes = true;
        for (int tokenAmount : tokens.values()) {
            if (tokenAmount > 0) {
                if (tokenAmount == 2) hasTwo = true;
                if (tokenAmount != 1) allOnes = false;
            }
        }
        if (!((takeCount == 1 && hasTwo) || (takeCount == 3 && allOnes)))
            throw new IllegalArgumentException("Invalid token take move");
    }

    @Override
    public void reserveDevelopment(int gameId, String playerName, String developmentName) {
        Game game = getGameById(gameId);
        if (game == null) {
            throw new IllegalArgumentException("Game not found: " + gameId);
        }

        if (game.getState() != GameState.IN_PROGRESS) {
            throw new IllegalArgumentException("Game is not in progress");
        }

        Player player = game.getspecificplayer(playerName);
        if (player == null) {
            throw new IllegalArgumentException("Player not found: " + playerName);
        }

        if (!player.getName().equals(game.getCurrentPlayer())) {
            throw new IllegalArgumentException(ERROR_MESSAGE);
        }

        if (player.getReserved().size() >= 3) {
            throw new IllegalArgumentException("Player can't reserve more than 3 cards");
        }

        DevelopmentCard card = findDevelopmentCardByName(developmentName);

        if (card == null) {
            throw new IllegalArgumentException("Development card not found: " + developmentName);
        }

        player.getReserved().add(card);
        List<Tier> visibleCards = game.getMarket().getVisibleCards();
        String tierName = "tier" + card.getLevel();
        for (Tier tier : visibleCards) {
            if (tier.getName().equals(tierName)) {
                tier.getCards().remove(card);
                game.getMarket().addNewCardFromHidden(tier);
            }
        }
        player.addBonus(GemType.GOLD, 1);
        if (game.getUnclaimedTokens().getTokens(GemType.GOLD) > 0) {
            player.getPurse().addTokens(GemType.GOLD, 1);
            game.getUnclaimedTokens().removeTokens(GemType.GOLD, 1);
        }

        game.nextTurn();
    }

    private DevelopmentCard findDevelopmentCardByName(String name) {
        try {
            List<DevelopmentCard> allCards = DevelopmentCard.loadDevelopmentCardsFromFile();
            for (DevelopmentCard card : allCards) {
                if (card.getName().equals(name)) {
                    return card;
                }
            }
            return null;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load development cards", e);
        }
    }

    @Override
    public void checkIfPlayerHasEnoughTokens(Player player, Map<GemType, Integer> payment, Game game) {
        if (player == null) {
            throw new IllegalArgumentException("Player not found");
        }

        String currentPlayer = game.getCurrentPlayer();
        if (currentPlayer == null || !currentPlayer.equals(player.getName())) {
            throw new IllegalStateException(ERROR_MESSAGE);
        }

        Purse playerPurse = player.getPurse();
        Map<GemType, Integer> playerBonuses = player.getBonuses();
        payment.forEach((gem, value) -> {
            int bonus = gem == GemType.GOLD ? 0 : playerBonuses.getOrDefault(gem, 0);
            int playerTokens = playerPurse.getTokens(gem);
            int totalTokens = playerTokens + bonus;
            if (totalTokens < value) {
                throw new IllegalArgumentException("Player does not have enough tokens of type " + gem);
            }
        });
    }
}
