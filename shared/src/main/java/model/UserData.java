package model;

public record UserData() {
    public record LoginRequest(String username, String password) {}

    public record LoginResult(String username,String authToken){}
}

