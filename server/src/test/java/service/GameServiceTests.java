package service;

import dataaccess.DataAccessMemory;
import exception.ResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameServiceTests {

    private GameService gameService;
    private DataAccessMemory dao;

    @BeforeEach
    public void setup() {
        dao = new DataAccessMemory();
        dao.clear();
        gameService = new GameService(dao);

    }

    // list game success
    @Test
    public void list_game_success() throws ResponseException {

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