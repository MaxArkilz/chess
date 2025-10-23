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
    private String validToken = "123-456-789";

    @BeforeEach
    public void setup() {
        dao = new DataAccessMemory();
        dao.clear();
        gameService = new GameService(dao);

        dao.createUser(new UserData("yourMother", "qwerty1!", "mrsamazing@gmail.com"));
        dao.createAuth(new AuthData(validToken, "yourMother"));

    }

    // list game success
    @Test
    public void list_game_success() throws ResponseException {
        dao.createGame(new GameData(1000, "yourMother", "", "Active Game", null));
        dao.createGame(new GameData(1001, null, null, "Empty Game", null));

        List<GameData> games = gameService.listGames(validToken);

        Assertions.assertNotNull(games);
        Assertions.assertEquals(2, games.size());
    }

    // list game unauthorized error
    @Test
    public void unauthorized_list_game() throws ResponseException{
        String badToken = "666";

        ResponseException ex = Assertions.assertThrows(ResponseException.class, () -> gameService.listGames(badToken));

        Assertions.assertEquals(401, ex.getStatusCode());
    }

    // create game success
    @Test
    public void successful_game_creation() throws ResponseException {
        var request = new GameData.CreateGameRequest("Good luck, have fun");
        var result = gameService.createGame(validToken, request);

        GameData newGame = dao.getGame(result.gameID());
        Assertions.assertTrue(result.gameID() >= 1000);
        Assertions.assertEquals("Good luck, have fun", newGame.gameName());

    }
    // create game bad request

    @Test
    public void no_name_game() throws ResponseException {
        var forgotNameRequest = new GameData.CreateGameRequest(null);

        ResponseException ex = Assertions.assertThrows(ResponseException.class, () -> gameService.createGame(validToken, forgotNameRequest));
        Assertions.assertEquals(400, ex.getStatusCode());
    }
    // create game unauthorized request
    @Test
    public void create_game_unauthorized() {
        String badToken = "666";
        var request = new GameData.CreateGameRequest("Game");

        ResponseException ex = Assertions.assertThrows(ResponseException.class, () -> gameService.createGame(badToken, request));
        Assertions.assertEquals(401, ex.getStatusCode());
    }
    // join game success
    @Test
    public void join_game_success() throws ResponseException {
        GameData game = new GameData(1002,"yourMother", null, "TheFaceOff", null);
        dao.createGame(game);

        dao.createUser(new UserData("yourFather", "password", "creativeemail@gmail.com"));
        dao.createAuth(new AuthData("987-654-321","yourFather"));

        var request = new GameData.JoinGameRequest("BLACK", 1002);
        gameService.joinGame("987-654-321", request);

        GameData updatedGame = dao.getGame(1002);
        Assertions.assertEquals("yourFather", updatedGame.blackUsername());
    }
    // join game unauthorized failure
    // join game no existing game
    // join game color taken
}