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
    // create game success
    // create game bad request
    // create game unauthorized request
    // join game success
    // join game unauthorized failure
    // join game no existing game
    // join game color taken
}