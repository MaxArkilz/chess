package handler;

import com.google.gson.Gson;
import io.javalin.http.Context;
import model.UserData;
import passoff.exception.ResponseParseException;
import service.UserService;

public class loginHandler {
    private final UserService service;

    public loginHandler(UserService service){
        this.service = service;
    }

    public static void login(Context context) throws ResponseParseException {

        }
    }
