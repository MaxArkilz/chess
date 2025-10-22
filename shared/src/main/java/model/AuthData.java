package model;

public record AuthData(String authToken, String username) {

    public static createUser newUser(String authToken, String username){
        return new createUser(authToken,username);
    }

    public record createUser(String username, String password) {}
}
