package client;

import exception.ResponseException;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import clientSide.ServerFacade;
import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static UserData.RegisterRequest testReg = new UserData.RegisterRequest(
            "SUDO","merryChristmas!","theOne@mail.com");
    private static UserData.LoginRequest testLog = new UserData.LoginRequest(
            "SUDO","merryChristmas!");
    private static GameData.CreateGameRequest testGameReq = new GameData.CreateGameRequest(
            "theMasterGame");
    private static GameData.CreateGameRequest testGameReq2 = new GameData.CreateGameRequest(
            "theBachelorGame");
    private static GameData.CreateGameRequest testGameReq3 = new GameData.CreateGameRequest(
            "theAssociateGame");



    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
        facade.clearData();


    }

    @AfterAll
    static void stopServer() {
        facade.clearData();
        server.stop();
    }




    @Test
    public void registerSuccess() throws ResponseException {
        var authData = facade.register(new UserData.RegisterRequest(
                "test1","weaksauce","test@mail.com"));
        assertNotNull(authData.authToken());
    }

    @Test
    public void registerFailureNoUsername() throws ResponseException {
        var regRequest = new UserData.RegisterRequest(
                null,"forgotIt","test@mail.com");
        assertThrows(ResponseException.class, () -> facade.register(regRequest));
    }

    @Test
    public void loginSuccess() throws ResponseException {
        facade.register(new UserData.RegisterRequest(
                "Light","godOfTheNewWorld","yagami99@hotmail.com"));
        var loginRequest = new UserData.LoginRequest(
                "Light","godOfTheNewWorld");
        var authData = facade.login(loginRequest);
        assertNotNull(authData.authToken());
    }

    @Test
    public void loginFailureIncorrectPassword() throws ResponseException {
        facade.register(new UserData.RegisterRequest(
                "L","gonnaGetYa","Lboy@Lsite.com"));
        var loginRequest = new UserData.LoginRequest(
                "L","gonnaGetya");
        assertThrows(ResponseException.class, () -> facade.login(loginRequest));
    }

    @Test
    public void logoutSuccess() throws ResponseException {
        var authData = facade.register(new UserData.RegisterRequest(
                "1","2","3@4.567"
        ));
        facade.logout(authData.authToken());
        assertThrows(ResponseException.class, () -> facade.logout(authData.authToken()));
    }

    @Test
    public void logoutFailureWrongAuth() throws ResponseException {
        var authData = facade.register(testReg);
        assertThrows(ResponseException.class, () -> facade.logout("totallyCorrectToken"));
    }

    @Test
    public void createGameSuccess() throws ResponseException {
        facade.register(testReg);
        var authData = facade.login(testLog);

        Iterable<GameData> games = facade.listGames(authData.authToken());
        assertFalse(games.iterator().hasNext());

        facade.createGame(authData.authToken(), testGameReq);

        assertNotNull(facade.listGames(authData.authToken()));
    }

    @Test
    public void createGameFailureDuplicate() throws ResponseException {
        facade.register(testReg);
        var authData = facade.login(testLog);
        facade.createGame(authData.authToken(),testGameReq);
        assertThrows(ResponseException.class, () -> facade.createGame(authData.authToken(),testGameReq));
    }

    @Test
    public void listGamesSuccess() throws ResponseException {
        facade.register(testReg);
        var authData = facade.login(testLog);

        Iterable<GameData> games = facade.listGames(authData.authToken());
        assertFalse(games.iterator().hasNext());

        facade.createGame(authData.authToken(),testGameReq);
        facade.createGame(authData.authToken(),testGameReq2);
        facade.createGame(authData.authToken(),testGameReq3);

        assertEquals(3, facade.listGames(authData.authToken()).size());
    }

    @Test
    public void listGamesFailureNoLogin() throws ResponseException {
        assertThrows(ResponseException.class, () -> facade.listGames(null));
    }


}
