package server;

import com.google.gson.Gson;
import handler.loginHandler;
import io.javalin.*;
import io.javalin.http.Context;
import model.UserData;
import passoff.exception.ResponseParseException;
import service.UserService;

public class Server {

    private final Javalin javalin;
    private final Javalin httpHandler;
    private final UserService service;

    public Server(UserService service) {
        this.service = service;

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        this.httpHandler = Javalin.create(config -> config.staticFiles.add("public"))
                .post("/session", loginHandler::login);

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void login(Context ctx) throws ResponseParseException {
        UserData.LoginRequest request = new Gson().fromJson(ctx.body(), UserData.LoginRequest.class);
        UserData.LoginResult result = service.login(request);
    }
}
