package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class MySqlDataAccessTests {

    private MySqlDataAccess dao;

    @BeforeEach
    public void setup() throws DataAccessException {
        dao = new MySqlDataAccess();
        dao.clear();
        dao.createGame(new GameData(
                1, null,null,"theOG",new ChessGame()));
        dao.createUser(new UserData("MyBeloved", "$tr0ngP@sSworD94", "realMail@mail.com"));
        AuthData auth = new AuthData("a-a-142j12-sajawryuig", "MyBeloved");
        dao.createAuth(auth);


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

    @Test
    public void createGameMissingNameTestFail() {
        GameData game = new GameData(0, null, null, null, new ChessGame());
        Assertions.assertThrows(DataAccessException.class, ()-> dao.createGame(game));
    }

    @Test
    public void getGameTestSuccess() throws DataAccessException {
        GameData game = dao.getGame(1);
        Assertions.assertNotNull(game);
        Assertions.assertEquals("theOG", game.gameName());
    }

    @Test
    public void getGameFailTest() throws DataAccessException {
        Assertions.assertNull(dao.getGame(984785));
    }

    @Test
    void updateGameSuccessTest() throws DataAccessException {
        GameData updated =
                new GameData(
                        1,
                        "StormTrooper",
                        "DeathTrooper",
                        "theOG",
                        new ChessGame());
        dao.updateGame(updated);
        GameData fromDb = dao.getGame(1);
        Assertions.assertEquals("StormTrooper", fromDb.whiteUsername());
        Assertions.assertEquals("DeathTrooper", fromDb.blackUsername());
    }

    @Test
    void updateGameNotFoundFailTest() {
        GameData fake =
                new GameData(
                        987654321,
                        "White",
                        null,
                        "sorryWrongNumber",
                        null);
        Assertions.assertThrows(DataAccessException.class, () -> dao.updateGame(fake));
    }

    @Test
    void listGamesSuccessTest() throws DataAccessException {
        int game1 = dao.createGame(new GameData(0, null,null,"tired", new ChessGame()));
        int game2 = dao.createGame(new GameData(0, null,null,"isLike1AM", new ChessGame()));

        ArrayList<GameData> games = new ArrayList<>();
        dao.listGames().forEach(games::add);
        Assertions.assertTrue(games.stream().anyMatch(g -> g.gameName().equals("tired")));
        Assertions.assertTrue(games.stream().anyMatch(g -> g.gameName().equals("isLike1AM")));
    }

    @Test
    void listGamesFailTest() throws DataAccessException {
        dao.clear();

        Iterable<GameData> games = dao.listGames();

        Assertions.assertFalse(games.iterator().hasNext());
    }

    @Test
    void createAuthSuccessTest() throws DataAccessException {
        AuthData auth = new AuthData("123456789", "baberham");
        dao.createUser(new UserData("baberham", "typicalpassword", "abe@email.com"));
        dao.createAuth(auth);
        AuthData fromDb = dao.getAuth("123456789");
        Assertions.assertNotNull(fromDb);
        Assertions.assertEquals("baberham", fromDb.username());
    }

    @Test
    void createAuthFailTest() {
        AuthData auth = new AuthData("987654321", "totallyNotAHacker");
        Assertions.assertThrows(DataAccessException.class, () -> dao.createAuth(auth));
    }

    @Test
    void getAuthSuccessTest() throws DataAccessException {

        AuthData fromDb = dao.getAuth("a-a-142j12-sajawryuig");
        Assertions.assertNotNull(fromDb);
        Assertions.assertEquals("MyBeloved", fromDb.username());
    }

    @Test
    void getAuthFailTest() throws DataAccessException {
        Assertions.assertNull(dao.getAuth("fakeNews10192"));
    }

    @Test
    void deleteAuthSuccessTest() throws DataAccessException {

        dao.deleteAuth("a-a-142j12-sajawryuig");
        Assertions.assertNull(dao.getAuth("a-a-142j12-sajawryuig"));
    }

    @Test
    void deleteAuthFailTest() {
        Assertions.assertThrows(DataAccessException.class, ()-> dao.deleteAuth("totallyreal"));
    }


}
