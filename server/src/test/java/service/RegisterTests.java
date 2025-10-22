package java.service;

import dataaccess.DataAccessMemory;
import exception.ResponseException;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.UserService;

public class RegisterTests {

    private UserService userService;

    @BeforeAll
    public static void setup(){
        DataAccessMemory dao = new DataAccessMemory();
        dao.clear();
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
}
