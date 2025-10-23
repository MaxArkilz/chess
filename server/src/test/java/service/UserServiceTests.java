package service;

import dataaccess.DataAccessMemory;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserServiceTests {

    private UserService userService;
    private DataAccessMemory dao;

    @BeforeEach
    public void setup(){
        dao = new DataAccessMemory();
        dao.clear();
        userService = new UserService(dao);

        dao.createUser(new UserData("theFirstUser", "weakPassword1", "user@email.com"));
        String token = "123-456-789";
        dao.createAuth(new AuthData(token, "theFirstUser"));
    }

    // Successful registration
    @Test
    public void register_success() throws ResponseException {
        var request = UserData.register("ajb263", "P@$$worD1","ajb263@byu.edu");
        var result = userService.register(request);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("ajb263", result.username());
        Assertions.assertNotNull(result.authToken());
    }

    // registration fail due to missing username
    @Test
    public void register_missing_username() throws ResponseException {
        var request = UserData.register(null, "P@$$worD1", "ajb263@byu.edu");
        ResponseException ex = Assertions.assertThrows(
                ResponseException.class,
                () -> userService.register(request),
                "Missing one field"
        );

        Assertions.assertEquals(400, ex.getStatusCode());

    }

    // successful login
    @Test
    public void login_success() throws ResponseException {
        var request = UserData.login("theFirstUser", "weakPassword1");
        var result = userService.login(request);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("theFirstUser", result.username());
        Assertions.assertNotNull(result.authToken());
    }

    // incorrect username login
    @Test
    public void login_failure_username() throws ResponseException {
        var request = UserData.login("theFirstHacker", "weakPassword1");
        ResponseException ex = Assertions.assertThrows(
                ResponseException.class,
                () -> userService.login(request),
                "incorrect Username"
        );

        Assertions.assertEquals(401, ex.getStatusCode());
    }

    // logout success
    @Test
    public void logout_success() throws ResponseException {

        String validToken = "123-456-789";
        userService.logout(validToken);
        Assertions.assertNull(dao.getAuth(validToken));
    }

    @Test
    public void logout_auth_failure() throws ResponseException {
        String invalidToken = "312-465-987";
        ResponseException ex = Assertions.assertThrows(ResponseException.class, () -> userService.logout(invalidToken));
        Assertions.assertEquals(401, ex.getStatusCode());
    }
}
