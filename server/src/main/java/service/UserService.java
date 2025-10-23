package service;

import dataaccess.DataAccess;
import model.AuthData;
import model.UserData;
import exception.ResponseException;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class UserService {

    private final DataAccess dao;

    public UserService(DataAccess dao){
        this.dao = dao;
    }

    public UserData.LoginResult register(@NotNull UserData.RegisterRequest regRequest) throws ResponseException{

        if (regRequest.username() == null || regRequest.password() == null || regRequest.email() == null) {
            throw new ResponseException(400, "Missing one field");
        }
        var existing = dao.getUser(regRequest.username());
        if (existing != null) {
            throw new ResponseException(403, "Username already taken");
        }

        var newUser = new UserData(regRequest.username(), regRequest.password(), regRequest.email());
        dao.createUser(newUser);

        var token = UUID.randomUUID().toString();
        var auth = new AuthData(token, newUser.username());
        dao.createAuth(auth);

        return new UserData.LoginResult(newUser.username(), token);
    }

    public UserData.LoginResult login(@NotNull UserData.LoginRequest loginRequest){
        var user = dao.getUser(loginRequest.username());

        if (user == null || !user.password().equals(loginRequest.password())){
            throw new ResponseException(401,"Username or Password is incorrect");
        }

        var token = UUID.randomUUID().toString();
        var auth = new AuthData(token, loginRequest.username());
        dao.createAuth(auth);

        return new UserData.LoginResult(loginRequest.username(), token);
    }

    public void logout(String logoutRequest) {}

}
