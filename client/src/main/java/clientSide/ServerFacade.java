package clientSide;

import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {serverUrl = url;}

    public void clearData() throws ResponseException {
        var request = buildRequest("DELETE","/db",null,null);
        var result = sendRequest(request);
        handleResponse(result, null);
    }

    public AuthData register(UserData.RegisterRequest req) throws ResponseException{
        var request = buildRequest("POST", "/user", req,null);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    public AuthData login(UserData.LoginRequest req) throws ResponseException {
        var request = buildRequest("POST", "/session", req, null);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    public void logout(String authToken) throws ResponseException {
        var request = buildRequest("DELETE","/session",null,authToken);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public GameData createGame(String authToken, GameData.CreateGameRequest req) throws ResponseException {
        var request = buildRequest("POST", "/game", req, authToken);
        var response = sendRequest(request);
        return handleResponse(response, GameData.class);
    }

    public List<GameData> listGames(String authToken) throws ResponseException {
        var request = buildRequest("GET", "/game",null,authToken);
        var response = sendRequest(request);
        var gamesWrapper = handleResponse(response, GamesWrapper.class);
        if (gamesWrapper != null) {
            return gamesWrapper.games;
        }
        else {
            return List.of();
        }
    }

    public void joinGame(String authToken, GameData.JoinGameRequest req) throws ResponseException {
        var request = buildRequest("PUT", "/game", req,authToken);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public GameData getGame(int gameID) {
        var request = buildRequest("GET", "/game/" + gameID, gameID, null);
        var response = sendRequest(request);
        return handleResponse(response, GameData.class);
    }


    //helper functions
    private static class GamesWrapper{
        List<GameData> games;
    }

    private HttpRequest buildRequest(String method, String path, Object body, String authToken){
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        if (authToken != null) {
            request.setHeader("authorization", authToken);
        }
        return request.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw ResponseException.fromJson(body);
            }
            throw new ResponseException(ResponseException.fromHttpStatusCode(status), "other failure: " + status);
        }
        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }
        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

}
