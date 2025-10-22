package model;

public record UserData() {
    public record LoginRequest(String username, String password) {}

    public record LoginResult(String username,String authToken){}

    public record RegisterRequest(String username, String password, String email){}
}

