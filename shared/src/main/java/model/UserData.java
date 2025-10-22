package model;
import java.util.UUID;

public record UserData(String username, String password, String email) {

    public static LoginRequest login(String username, String password) {
        return new LoginRequest(username, password);
    }

    public static LoginResult result(String username,String authToken){
        return new LoginResult(username, UUID.randomUUID().toString());
    }

    public static RegisterRequest register(String username, String password, String email){
        return new RegisterRequest(username, password, email);
    }

    public record LoginRequest(String username, String password) {}
    public record RegisterRequest(String username, String password, String email) {}
    public record LoginResult(String username, String authToken) {}

}

