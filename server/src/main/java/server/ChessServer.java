package server;

import com.google.gson.Gson;
import handler.loginHandler;
import io.javalin.*;
import io.javalin.http.Context;
import model.UserData;
import passoff.exception.ResponseParseException;
import service.UserService;

public class ChessServer {

    private final Javalin httpHandler;
    private final UserService service;

    public ChessServer(UserService service) {
        this.service = service;

        // Register your endpoints and exception handlers here.
        this.httpHandler = Javalin.create(config -> config.staticFiles.add("public"))
                .post("/session", this::login)
                .delete("/db", this::clearApplication)
                .post("/user", this::register)
                .delete("/session", this::logout)
                .get("/game", this::listGames)
                .post("/game", this::createGame)
                .put("/game", this::joinGame);

    }

    public ChessServer run(int desiredPort) {
        httpHandler.start(desiredPort);
        return this;
    }

    public void stop() {
        httpHandler.stop();
    }


    private void login(Context ctx) throws ResponseParseException {
        UserData.LoginRequest request = new Gson().fromJson(ctx.body(), UserData.LoginRequest.class);
        UserData.LoginResult result = service.login(request);
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
