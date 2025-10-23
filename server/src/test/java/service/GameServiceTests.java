package service;

import dataaccess.DataAccessMemory;
import org.junit.jupiter.api.BeforeEach;

public class GameServiceTests {

    private GameService gameService;
    private DataAccessMemory dao;

    @BeforeEach
    public void setup() {
        dao = new DataAccessMemory();
        dao.clear();
        gameService = new GameService(dao);

    }
}