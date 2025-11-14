package server;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.DataAccessMemory;
import dataaccess.MySqlDataAccess;
import exception.ResponseException;
import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.json.JavalinGson;
import model.GameData;
import model.UserData;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.UserService;

import java.sql.SQLException;
import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final UserService userService;
    private final GameService gameService;
    private final DataAccess dao;
//    private final MySqlDataAccess dao;


    public Server() {
//        this(new DataAccessMemory());
        this(createMySqlDA());
    }

    public Server(DataAccess dao) {
        this.dao = dao;
        this.userService = new UserService(dao);
        this.gameService = new GameService(dao);
        this.javalin = makeJavalin();
        registerEndpoints();
    }

    private static DataAccess createMySqlDA() {
        try {
            return new MySqlDataAccess();
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    private Javalin makeJavalin() {
        return Javalin.create(config -> {
            config.jsonMapper(new JavalinGson());
            config.staticFiles.add("/web");
        });
    }

    private void registerEndpoints() {
        // Same registration as before
        javalin.exception(ResponseException.class, ((e, context) -> {
            context.status(e.getStatusCode()); // Use the correct status code!
            context.json(Map.of("message", "Error: " + e.getMessage()));
        }));
        javalin.exception(DataAccessException.class, (e, ctx) -> {
            ctx.status(500);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        });
        javalin.exception(SQLException.class, (e, ctx) -> {
            ctx.status(500);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        });
        javalin.exception(Exception.class, (e, ctx) -> {
            ctx.status(500);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        });
        javalin.post("/user", this::register);
        javalin.delete("/db", this::clear);
        javalin.post("/session", this::login);
        javalin.delete("/session", this::logout);
        javalin.get("/game", this::listGames);
        javalin.post("/game", this::createGame);
        javalin.put("/game", this::joinGame);
        javalin.get("/game/{gameID}", this::getGame);
    }

    private void getGame(@NotNull Context context) throws DataAccessException {
        int gameID = context.pathParamAsClass("gameID", Integer.class).get();
        ChessGame result = gameService.getGame(gameID);
        context.status(200).json(result);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void register(@NotNull Context ctx) throws ResponseException, DataAccessException {
        UserData.RegisterRequest request = ctx.bodyAsClass(UserData.RegisterRequest.class);
        var result = userService.register(request);
        ctx.status(200).json(result);
    }

    private void clear(Context ctx) throws ResponseException, SQLException, DataAccessException {
        dao.clear();
        ctx.status(200).json("{}");
    }

    private void login(Context ctx) throws ResponseException, DataAccessException {
        UserData.LoginRequest request = ctx.bodyAsClass(UserData.LoginRequest.class);
        var result = userService.login(request);
        ctx.status(200).json(result);
    }

    private void logout(Context ctx) throws ResponseException, DataAccessException {
        String authToken = ctx.header("authorization");
        userService.logout(authToken);
        ctx.status(200).json("{}");
    }

    private void listGames(Context ctx) throws ResponseException, DataAccessException {
        String authToken = ctx.header("authorization");
        var result = gameService.listGames(authToken);
        ctx.status(200).json(Map.of("games", result));
    }

    private void createGame(Context ctx) throws ResponseException, DataAccessException {
        String authToken = ctx.header("authorization");
        GameData.CreateGameRequest request = ctx.bodyAsClass(GameData.CreateGameRequest.class);
        var result = gameService.createGame(authToken, request);
        ctx.status(200).json(result);

    }

    private void joinGame(Context ctx) throws ResponseException, DataAccessException {
        String authToken = ctx.header("authorization");
        var serializer = new Gson();
        GameData.JoinGameRequest request = serializer.fromJson(ctx.body(), GameData.JoinGameRequest.class);

        gameService.joinGame(authToken, request);
        ctx.status(200).json("{}");

    }

}
