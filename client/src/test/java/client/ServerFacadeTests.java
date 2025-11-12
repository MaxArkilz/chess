package client;

import exception.ResponseException;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import clientSide.ServerFacade;
import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerSuccess() throws ResponseException {
        var authData = facade.register(new UserData.RegisterRequest(
                "test1","weaksauce","test@mail.com"));
        assertNotNull(authData.authToken());
    }

}
