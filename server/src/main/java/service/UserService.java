package service;

import model.UserData;
import exception.ResponseException;

public class UserService {
    public UserData.RegisterRequest register(UserData.RegisterRequest registerRequest)throws ResponseException{
        if (registerRequest.username() != null) {
            throw new ResponseException(403);
        }
    }

    public UserData login(UserData.LoginRequest loginRequest){
        return null;
    }

    public void logout(String logoutRequest) {}

}
