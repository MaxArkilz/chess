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
        dao.createGame(new GameData(
                1, null,null,"theOG",new ChessGame()));
        dao.createUser(new UserData("MyBeloved", "$tr0ngP@sSworD94", "realMail@mail.com"));


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
    public void createUserTestSuccess() throws DataAccessException {
        dao.createUser(new UserData("Test1", "weak","hi@mail.com"));
        UserData user = dao.getUser("Test1");
        String expected = "Test1";

        Assertions.assertEquals(expected, user.username());
    }

    @Test
    public void createUserDuplicateUserFail() {
        UserData repeatUser =
                new UserData("MyBeloved", "$tr0ngP@sSworD94", "realMail@mail.com");
        Assertions.assertThrows(DataAccessException.class, () ->
            dao.createUser(repeatUser));
    }

    @Test
    public void createUserMissingUsernameFail() {
        UserData sloppyJoe =
                new UserData(null, "2000", "idk@mail.com");
        Assertions.assertThrows(DataAccessException.class, () ->
                dao.createUser(sloppyJoe));
    }

    @Test
    public void getUserSuccessTest() throws DataAccessException {
        UserData user = dao.getUser("MyBeloved");
        Assertions.assertNotNull(user);
        Assertions.assertEquals("MyBeloved", user.username());
    }

    @Test
    public void getUserNotFoundTestFail() throws DataAccessException {
        UserData user = dao.getUser("Jonny Sims");
        Assertions.assertNull(user);
    }

    @Test
    public void createGameTestSuccess() throws DataAccessException {

        int gameID = dao.createGame(new GameData(
                0,null,null,"testGame",new ChessGame()));
        GameData game = dao.getGame(gameID);
        String expected = "testGame";

        Assertions.assertEquals(expected,game.gameName());
    }

    public void getGameTestSuccess() throws DataAccessException {




    }

}
