package be.howest.ti.game.logic;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;

public class Game {
    private int gameId;
    private String gameName;
    private boolean returnExcessTokens;
    private int numberOfPlayers;
    private final List<Player> players;
    private Purse unclaimedTokens;
    private Market market;
    private Set<NobleCard> nobles;
    private String currentPlayer;
    private GameState state;


    public Game(int id, String gameName, boolean returnExcessTokens, int numberOfPlayers) {
        this.gameId = id;
        this.gameName = gameName;
        this.returnExcessTokens = returnExcessTokens;
        this.numberOfPlayers = numberOfPlayers;
        this.unclaimedTokens = prepareGamePurse(numberOfPlayers);
        this.players = new ArrayList<>();
        this.nobles = new HashSet<>();
        this.state = GameState.WAITING;
        this.market = new Market();
    }

    private Purse prepareGamePurse(int numberOfPlayers) {
        Purse purse = new Purse();
        int numberOfTokensInPurse;

        numberOfTokensInPurse = switch (numberOfPlayers) {
            case 2 -> 4;
            case 3 -> 5;
            case 4 -> 7;
            default -> throw new IllegalArgumentException("Invalid number of players: " + numberOfPlayers);
        };

        for (GemType gem : GemType.values()) {
            if (gem == GemType.GOLD) {
                purse.addTokens(gem, 5);
            }else {
                purse.addTokens(gem, numberOfTokensInPurse);
            }
        }
        return purse;
    }

    public void startGame() {
        this.state = GameState.IN_PROGRESS;
        if (!players.isEmpty()) {
            this.currentPlayer = players.getFirst().getName();
        }

        List<NobleCard> allNobles = null;
        try {
            allNobles = NobleCard.loadNoblesFromFile();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load noble cards", e);
        }
        Collections.shuffle(allNobles);

        int numberOfNoblesToSelect;
        int sizeOfPlayers = players.size();
        numberOfNoblesToSelect = switch (sizeOfPlayers) {
            case 2 -> 3;
            case 3 -> 4;
            case 4 -> 5;
            default -> throw new IllegalArgumentException("Invalid number of players: " + sizeOfPlayers);
        };

        Set<NobleCard> selectedNobles = new HashSet<>();
        for (int i = 0; i < numberOfNoblesToSelect; i++) {
            selectedNobles.add(allNobles.get(i));
        }
        setNobles(selectedNobles);

        this.market = new Market();
    }

    public void endGame() {

        this.state = GameState.COMPLETED;

    }
    public void addPlayer(String playerName) {
        Player newPlayer = new Player(playerName);
        players.add(newPlayer);
        if (players.size() == numberOfPlayers) {
            startGame();
        }
    }

    public void nextTurn() {
        if (players.isEmpty() || currentPlayer == null) {
            return;
        }
        for (Player player : players) {
            if (player.getTotalPrestigePoints() >= 15) {
                int currentIndex = players.indexOf(getspecificplayer(currentPlayer));
                if (currentIndex == players.size() - 1) {
                    endGame();
                } else {
                    currentPlayer = players.get(currentIndex + 1).getName();
                }
                return;
            }
        }
        int currentIndex = players.indexOf(getspecificplayer(currentPlayer));
        int nextIndex = (currentIndex + 1) % players.size();
        currentPlayer = players.get(nextIndex).getName();
        Player player = getspecificplayer(currentPlayer);
        player.checkIfPlayerHasEnoughTokens(player, this);
    }
    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public boolean currentReturnExcessTokens() {
        return returnExcessTokens;
    }

    public void setReturnExcessTokens(boolean returnExcessTokens) {
        this.returnExcessTokens = returnExcessTokens;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Market getMarket() {
        return market;
    }

    public void setMarket(Market market) {
        this.market = market;
    }

    public Set<NobleCard> getNobles() {
        return nobles;
    }

    public void setNobles(Set<NobleCard> nobles) {
        this.nobles = nobles;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public Player getspecificplayer(String playerName) {
        for (Player player : players) {
            if (player.getName().equals(playerName)) {
                return player;
            }
        }
        return null;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public Purse getUnclaimedTokens() {
        return unclaimedTokens;
    }

}
