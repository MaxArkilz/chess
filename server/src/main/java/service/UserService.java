package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import exception.ResponseException;
import org.jetbrains.annotations.NotNull;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

public class UserService {

    private final DataAccess dao;

    public UserService(DataAccess dao){
        this.dao = dao;
    }

    public UserData.LoginResult register(@NotNull UserData.RegisterRequest regRequest)
            throws ResponseException, DataAccessException {

        if (regRequest.username() == null || regRequest.password() == null || regRequest.email() == null) {
            throw new ResponseException(400, "Missing one field");
        }
        var existing = dao.getUser(regRequest.username());
        if (existing != null) {
            throw new ResponseException(403, "Username already taken");
        }



        String hashedPassword = BCrypt.hashpw(regRequest.password(), BCrypt.gensalt());

        var newUser = new UserData(regRequest.username(), hashedPassword, regRequest.email());
        dao.createUser(newUser);

        var token = UUID.randomUUID().toString();
        var auth = new AuthData(token, newUser.username());
        dao.createAuth(auth);

        return new UserData.LoginResult(newUser.username(), token);
    }

    public UserData.LoginResult login(@NotNull UserData.LoginRequest loginRequest) throws DataAccessException {
        var user = dao.getUser(loginRequest.username());

        if (loginRequest.username() == null || loginRequest.password() == null){
            throw new ResponseException(400, "Error: bad request");
        }

        if (user == null) {
            throw new ResponseException(401, "Error: unauthorized"); // Or customize this message
        }

        var hashedPassword = user.password();


        if (!BCrypt.checkpw(loginRequest.password(), hashedPassword)){
            throw new ResponseException(401,"Error: unauthorized");
        }

        var token = UUID.randomUUID().toString();
        var auth = new AuthData(token, loginRequest.username());
        dao.createAuth(auth);

        return new UserData.LoginResult(loginRequest.username(), token);
    }

    public void logout(String authToken) throws DataAccessException {
        var auth = dao.getAuth(authToken);
        if (auth == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        dao.deleteAuth(authToken);
    }

}
