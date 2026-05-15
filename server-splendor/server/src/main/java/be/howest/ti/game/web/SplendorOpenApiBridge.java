package be.howest.ti.game.web;


import be.howest.ti.game.logic.*;
import be.howest.ti.game.logic.service.SplendorService;
import be.howest.ti.game.logic.service.SplendorServiceImpl;
import be.howest.ti.game.web.tokens.PlainTextTokens;
import be.howest.ti.game.web.tokens.SplendorHTTPPlayer;
import be.howest.ti.game.web.tokens.TokenManager;
import be.howest.ti.game.web.views.request.*;
import be.howest.ti.game.web.views.response.*;

import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SplendorOpenApiBridge extends OpenApiBridge { // NOSONAR this is not a monster class, it is a bridge :-)

    private static final Logger LOGGER = Logger.getLogger(SplendorOpenApiBridge.class.getName());
    private final Supplier<SplendorService> serviceFactory;

    public SplendorOpenApiBridge() {
        this(SplendorServiceImpl::new, new PlainTextTokens());
    }

    // Factory needed to differentiate between group-tokens, can be simplified with a single service in the student version.
    SplendorOpenApiBridge(Supplier<SplendorService> serviceFactory, TokenManager tokenManager) {
        installPlayerTokenManager(tokenManager);
        this.serviceFactory = serviceFactory;
    }

    private final Map<String, SplendorService> services = new HashMap<>();

    private SplendorService getService(ContextBasedRequestView request) {
        return services.computeIfAbsent(request.getGroupSecret().toString(),
                k -> serviceFactory.get());
    }

    //region Generic Checks
    private static String ensureUrlSafe(String type, String txt) {
        if (!txt.matches("[a-zA-Z0-9]+")) { // letters and digits only or throw
            throw new IllegalArgumentException(type + " should be alphanumeric because it can be used in the url");
        }
        return txt;
    }
    //end region

    //region General operations

    @Operation("get-info")
    public GetInfoResponse getInfo(GetInfoRequest ctx) {
        return new GetInfoResponse();
    }

    @Operation("get-gems")
    public GetGemsResponse getGems(GetGemsRequest request) {
        return new GetGemsResponse();
    }

    @Operation("get-nobles")
    public NoblesResponse getNobles(BaseSplendorRequest request) {
        try{
            List<NobleCard> nobles = NobleCard.loadNoblesFromFile();
            return new NoblesResponse(nobles);
        } catch (IOException e) {
            LOGGER.log(Level.INFO, "Error loading noble cards", e);
            return new NoblesResponse(Collections.emptyList());
        }
    }

    @Operation("get-developments")
    public DevelopmentsResponse getDevelopments(BaseSplendorRequest request) {
        try {
            List<DevelopmentCard> developments = DevelopmentCard.loadDevelopmentCardsFromFile();
            return new DevelopmentsResponse(developments);
        } catch (IOException e) {
            LOGGER.log(Level.INFO, "Error loading development cards", e);
            return new DevelopmentsResponse(Collections.emptyList());
        }
    }

    //endregion

    //region Game Management operations

    @Operation("get-games")
    public GetGamesResponse getGames(GetGamesRequest request) {
        Boolean started = request.isStarted().orElse(null);
        SplendorService service = getService(request);

        List<Game> games = service.getGames(started);
        return new GetGamesResponse(games);
    }

    @Operation("create-game")
    public CreateGameResponse createGame(CreateGameRequest request) {
        String playerName = request.getPlayerName();
        String gameName = request.getGameName();
        int numberOfPlayers = request.getNumberOfPlayers();
        SplendorService service = getService(request);

        Game game = service.createGame(gameName, playerName, numberOfPlayers);
        return new CreateGameResponse(game, playerName,createToken(new SplendorHTTPPlayer(game.getGameId(), playerName)));
    }

    @Operation("delete-games")
    public AbstractResponseWithHiddenStatus deleteGames(BaseSplendorRequest request) {
        serviceFactory.get().deleteGames();
        return new AbstractResponseWithHiddenStatus(200);
    }

    @Operation("get-game-details")
    public GetGameDetailsResponse getGameDetails(GetGameDetailsRequest request) {
        int gameId = request.getGameId();
        SplendorService service = getService(request);
        Game game = service.getGameById(gameId);
        return new GetGameDetailsResponse(game);
    }

    @Operation("join-game")
    public JoinGameResponse joinGame(JoinGameRequest request) {
        String playerName = request.getPlayerName();
        int gameId = request.getGameId();
        SplendorService service = getService(request);
        Game game = service.getGameById(gameId);
        game.addPlayer(playerName);
        return new JoinGameResponse(createToken(new SplendorHTTPPlayer(gameId, playerName)), playerName, game);
    }


    //endregion

    //region Player Resources operations
    @Operation("get-player-details")
    public NotYetImplementedResponse getPlayerDetails(BaseSplendorRequest request) {
        return new NotYetImplementedResponse("get-player-details");
    }

    //endregion

    //region Game Action operations
    @Operation("update-tokens")
    public UpdateTokensResponse updateTokens(UpdateTokensRequest request) {
        int gameId = request.getGameId();
        String playerName = request.getPlayerName();
        java.util.Map<GemType, Integer> tokens = request.getTokenMap();
        SplendorService service = getService(request);
        service.updateTokens(gameId, playerName, tokens);
        Game game = service.getGameById(gameId);
        return new UpdateTokensResponse(game.getspecificplayer(playerName).getPurse());
    }

    @Operation("buy-development")
    public BuyDevelopmentResponse buyDevelopment(BuyDevelopmentRequest request) {
        String playerName = request.getPlayerName();
        int gameId = request.getGameId();
        String cardName = request.getCardName();
        Map<GemType, Integer> payment = request.getPayment();

        SplendorService service = getService(request);
        Game game = service.getGameById(gameId);
        Player player = game.getspecificplayer(playerName);

        service.checkIfPlayerHasEnoughTokens(player, payment, game);
        DevelopmentCard card = game.getMarket().getCardByName(cardName);
        game.getMarket().buyDevelopmentCard(card, player);
        service.handleReturnTokens(game, player, payment, card);

        Set<DevelopmentCard> playerCards = player.getCards();
        game.nextTurn();
        return new BuyDevelopmentResponse(playerCards, player.getPurse());
    }

    @Operation("reserve-development")
    public ReserveDevelopmentResponse reserveDevelopment(ReserveDevelopmentRequest request) {
        int gameId = request.getGameId();
        String playerName = request.getPlayerName();
        String developmentName = request.getDevelopmentName();

        SplendorService service = getService(request);
        service.reserveDevelopment(gameId, playerName, developmentName);

        Game game = service.getGameById(gameId);
        Player player = game.getspecificplayer(playerName);

        return new ReserveDevelopmentResponse(player.getReserved(), player.getPurse().getTokensMap());
    }

    @Operation("buy-reserved-development")
    public BuyReservedResponse buyReserveDevelopment(BuyReservedRequest request) {
        String playerName = request.getPlayerName();
        int gameId = request.getGameId();
        String reservedCardName = request.getCardName();
        Map<GemType, Integer> reservedPayment = request.getPayment();

        SplendorService service = getService(request);
        Game game = service.getGameById(gameId);
        Player player = game.getspecificplayer(playerName);

        service.checkIfPlayerHasEnoughTokens(player, reservedPayment, game);
        DevelopmentCard card = player.getreservedByName(reservedCardName);
        player.removeReserved(card);
        service.handleReturnTokens(game, player, reservedPayment, card);
        player.remove1GoldBonus();

        Set<DevelopmentCard> reserved = player.getReserved();
        Set<DevelopmentCard> cards = player.getCards();
        game.nextTurn();
        return new BuyReservedResponse(cards, player.getPurse(), reserved);
    }

    @Operation("choose-noble")
    public NotYetImplementedResponse chooseNoble(BaseSplendorRequest request) {
        return new NotYetImplementedResponse("choose-noble");
    }
    //endregion


}
