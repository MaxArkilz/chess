package server;

import dataaccess.DataAccessMemory;
import exception.ResponseException;
import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.json.JavalinGson;
import model.GameData;
import model.UserData;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.UserService;

import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final UserService userService;
    private final GameService gameService;
    private final DataAccessMemory dao;

    public Server() {

        this.dao = new DataAccessMemory();
        userService = new UserService(dao);
        gameService = new GameService(dao);

        javalin = Javalin.create(config -> {
            config.jsonMapper(new JavalinGson());
            config.staticFiles.add("/web");
        });

        // Register your endpoints and exception handlers here.
        javalin.exception(ResponseException.class, ((e, context) -> {
            context.status(e.getStatusCode());
            context.json(Map.of("message", "Error: " + e.getMessage()));
        } ));
        javalin.post("/user", this::register);
        javalin.delete("/db", this::clear);
        javalin.post("/session", this::login);
        javalin.delete("/session", this::logout);
        javalin.get("/game", this::listGames);
        javalin.post("/game", this::createGame);
        javalin.put("/game", this::joinGame);

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void register(@NotNull Context ctx) throws ResponseException {
        UserData.RegisterRequest request = ctx.bodyAsClass(UserData.RegisterRequest.class);
        var result = userService.register(request);
        ctx.status(200).json(result);
    }

    private void clear(Context ctx) throws ResponseException {
        dao.clear();
        ctx.status(200).json("{}");
    }

    private void login(Context ctx) throws  ResponseException{
        UserData.LoginRequest request = ctx.bodyAsClass(UserData.LoginRequest.class);
        var result = userService.login(request);
        ctx.status(200).json(result);
    }

    private void logout(Context ctx) throws ResponseException {
        String authToken = ctx.header("authorization");
        userService.logout(authToken);
        ctx.status(200).json("{}");
    }

    private void listGames(Context ctx) throws ResponseException{
        String authToken = ctx.header("authorization");
        var result = gameService.listGames(authToken);
        ctx.status(200).json(Map.of("games", result));
    }

    private void createGame(Context ctx) throws ResponseException{
        String authToken = ctx.header("authorization");
        GameData.CreateGameRequest request = ctx.bodyAsClass(GameData.CreateGameRequest.class);
        var result = gameService.createGame(authToken, request);
        ctx.status(200).json(result);

    }

    private void joinGame(Context ctx) throws ResponseException{
        String authToken = ctx.header("authorization");
        GameData.JoinGameRequest request = ctx.bodyAsClass(GameData.JoinGameRequest.class);
        gameService.joinGame(authToken, request);
        ctx.status(200).json("{}");

    }
}
