package server;

import com.google.gson.Gson;
import dataaccess.DataAccessMemory;
import exception.ResponseException;
import io.javalin.*;
import io.javalin.http.Context;
import model.UserData;
import service.GameService;
import service.UserService;

public class ChessServer {

    private final Javalin javalin;
    private final UserService userService;
//    private final GameService gameService;

    public ChessServer() {

        DataAccessMemory dao = new DataAccessMemory();

        userService = new UserService(dao);
//        gameService = new GameService(dao);

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.post("/user", this::register);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void register(Context ctx) throws ResponseException {
        UserData.RegisterRequest request = ctx.bodyAsClass(UserData.RegisterRequest.class);
        var result = userService.register(request);
        ctx.status(200).json(result);

    }
}
