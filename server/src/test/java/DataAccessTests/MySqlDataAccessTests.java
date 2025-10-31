package DataAccessTests;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MySqlDataAccess;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MySqlDataAccessTests {

    private MySqlDataAccess dao;

    @BeforeEach
    public void setup() throws DataAccessException {
        dao = new MySqlDataAccess();
        dao.clear();

    }

    @Test
    public void clearSuccessTest() throws DataAccessException {
        dao.createUser(new UserData("HereToStay","1234","test@mail.com"));
        UserData user = dao.getUser("HereToStay");
        Assertions.assertNotNull(user);

        dao.clear();

        user = dao.getUser("HereToStay");
        Assertions.assertNull(user);
    }

    @Test
    public void registerUserTestSuccess() throws DataAccessException {
        dao.createUser(new UserData("Test1", "weak","hi@mail.com"));
        UserData user = dao.getUser("Test1");
        String expected = "Test1";

        Assertions.assertEquals(expected, user.username());
    }

    @Test
    public void createGameTestSuccess() throws DataAccessException {

        dao.createGame(new GameData(1,null,null,"testGame",new ChessGame()));
        GameData game = dao.getGame(1);
        String expected = "testGame";

        Assertions.assertEquals(expected,game.gameName());
    }
}
