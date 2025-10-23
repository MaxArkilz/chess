package service;

import dataaccess.DataAccessMemory;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class GameServiceTests {

    private GameService gameService;
    private DataAccessMemory dao;
    private String momToken = "123-456-789";
    private String dadToken = "987-654-321";

    @BeforeEach
    public void setup() {
        dao = new DataAccessMemory();
        dao.clear();
        gameService = new GameService(dao);

        dao.createUser(new UserData("yourMother", "qwerty1!", "mrsamazing@gmail.com"));
        dao.createAuth(new AuthData(momToken, "yourMother"));
        dao.createUser(new UserData("yourFather", "password", "creativeemail@gmail.com"));
        dao.createAuth(new AuthData("987-654-321","yourFather"));

    }

    // list game success
    @Test
    public void listGameSuccess() throws ResponseException {
        dao.createGame(new GameData(1000, "yourMother", "", "Active Game", null));
        dao.createGame(new GameData(1001, null, null, "Empty Game", null));

        List<GameData> games = gameService.listGames(momToken);

        Assertions.assertNotNull(games);
        Assertions.assertEquals(2, games.size());
    }

    // list game unauthorized error
    @Test
    public void unauthorizedListGame() throws ResponseException{
        String badToken = "666";

        ResponseException ex = Assertions.assertThrows(ResponseException.class, () -> gameService.listGames(badToken));

        Assertions.assertEquals(401, ex.getStatusCode());
    }

    // create game success
    @Test
    public void successfulGameCreation() throws ResponseException {
        var request = new GameData.CreateGameRequest("Good luck, have fun");
        var result = gameService.createGame(momToken, request);

        GameData newGame = dao.getGame(result.gameID());
        Assertions.assertTrue(result.gameID() >= 1000);
        Assertions.assertEquals("Good luck, have fun", newGame.gameName());

    }
    // create game bad request

    @Test
    public void noNameGame() throws ResponseException {
        var forgotNameRequest = new GameData.CreateGameRequest(null);

        ResponseException ex = Assertions.assertThrows(ResponseException.class, () -> gameService.createGame(momToken, forgotNameRequest));
        Assertions.assertEquals(400, ex.getStatusCode());
    }
    // create game unauthorized request
    @Test
    public void createGameUnauthorized() {
        String badToken = "666";
        var request = new GameData.CreateGameRequest("Game");

        ResponseException ex = Assertions.assertThrows(ResponseException.class, () -> gameService.createGame(badToken, request));
        Assertions.assertEquals(401, ex.getStatusCode());
    }
    // join game success
    @Test
    public void joinGameSuccess() throws ResponseException {
        GameData game = new GameData(1002,"yourMother", null, "TheFaceOff", null);
        dao.createGame(game);

        var request = new GameData.JoinGameRequest("BLACK", 1002);
        gameService.joinGame(dadToken, request);

        GameData updatedGame = dao.getGame(1002);
        Assertions.assertEquals("yourFather", updatedGame.blackUsername());
    }
    // join game unauthorized failure
    @Test
    public void joinGameUnauthorized() {
        var request = new GameData.JoinGameRequest("WHITE", 1003);
        String badToken = "666";

        ResponseException ex = Assertions.assertThrows(ResponseException.class, () -> gameService.joinGame(badToken, request));
        Assertions.assertEquals(401, ex.getStatusCode());
    }

    // join game no existing game
    @Test
    public void joinGameNoGame() {
        var request = new GameData.JoinGameRequest("WHITE", 999);

        ResponseException ex = Assertions.assertThrows(ResponseException.class, () -> gameService.joinGame(momToken, request));
        Assertions.assertEquals(400, ex.getStatusCode());
    }

    // join game color taken
    @Test
    public void joinGameColorTaken() {
        // Set up game where WHITE is taken
        GameData game = new GameData(1007, "yourMother", null, "YouShallNotPass", null);
        dao.createGame(game);

        var request = new GameData.JoinGameRequest("WHITE", 1007);

        ResponseException ex = Assertions.assertThrows(ResponseException.class, () -> gameService.joinGame(dadToken, request));
        Assertions.assertEquals(403, ex.getStatusCode());
    }
}