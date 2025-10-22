package server;

import com.google.gson.Gson;
import exception.ResponseException;
import io.javalin.*;
import io.javalin.http.Context;
import model.UserData;
import service.GameService;
import service.UserService;

public class ChessServer {

    private final Javalin javalin;
    private final UserService userService;
    private final GameService gameService;

    public ChessServer() {

        userService = new UserService();
        gameService = new GameService();

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
        var request = new Gson().fromJson(ctx.body(), UserData.class);
        var result = userService.register(request);

    }
}
