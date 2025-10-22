package server;

import dataaccess.DataAccess;
import dataaccess.DataAccessMemory;
import exception.ResponseException;
import io.javalin.*;
import io.javalin.http.Context;
import model.UserData;
import org.jetbrains.annotations.NotNull;
import service.UserService;

public class Server {

    private final Javalin javalin;
    private final UserService userService;
//    private final GameService gameService;
    private final DataAccessMemory dao;

    public Server() {

        this.dao = new DataAccessMemory();
        userService = new UserService(dao);
//        gameService = new GameService(dao);

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.post("/user", this::register);
        javalin.delete("/db", this::clear);
        javalin.post("/session", this::login);
        javalin.delete("/session", this::logout);
        javalin.exception(Exception.class, (ex, ctx) -> {
            ctx.status(500);
            ctx.json("{\"Internal Server Error\"}");
        });
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

    }
}
