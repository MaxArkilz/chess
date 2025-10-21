package server;

import com.google.gson.Gson;
import handler.loginHandler;
import io.javalin.*;
import io.javalin.http.Context;
import model.UserData;
import passoff.exception.ResponseParseException;
import service.GameService;
import service.UserService;

public class ChessServer {

    private final Javalin httpHandler;
    private final UserService userService;
    private final GameService gameService;

    public ChessServer(UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;

        // Register your endpoints and exception handlers here.
        this.httpHandler = Javalin.create(config -> config.staticFiles.add("public"))
                .post("/session", this::login)
                .delete("/session", this::logout)
                .post("/user", this::register)
                .get("/game", this::listGames)
                .post("/game", this::createGame)
                .put("/game", this::joinGame)
                .delete("/db", this::clearApplication)
                .exception(ResponseParseException.class, this::exeptionHandler);

    }

    public ChessServer run(int desiredPort) {
        httpHandler.start(desiredPort);
        return this;
    }

    public void stop() {
        httpHandler.stop();
    }

    private void exceptionHandler(ResponseParseException ex, Context ctx) {
        ctx.status(ex.toHttpStatusCode());
        ctx.json(ex.toJson());
    }

    private void login(Context ctx) throws ResponseParseException {
        UserData.LoginRequest request = new Gson().fromJson(ctx.body(), UserData.LoginRequest.class);
        UserData.LoginResult result = userService.login(request);
    }

    private void clearApplication(Context ctx) {

    }

    private void register(Context ctx){

    }

    private void logout(Context ctx){

    }

    private void listGames(Context ctx){

    }

    private void createGame(Context ctx) {

    }

    private void joinGame(Context ctx) {

    }
}
