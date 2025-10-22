package passoff.service;

import dataaccess.DataAccessMemory;
import exception.ResponseException;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserService;

public class UserServiceTests {

    private UserService userService;

    @BeforeEach
    public void setup(){
        DataAccessMemory dao = new DataAccessMemory();
        dao.clear();
        userService = new UserService(dao);
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

    // fail due to missing username
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
}
