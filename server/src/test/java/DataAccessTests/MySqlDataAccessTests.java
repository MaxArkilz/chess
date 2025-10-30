package DataAccessTests;

import dataaccess.DataAccessException;
import dataaccess.MySqlDataAccess;
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
        dao.createUser(new UserData("HereToStay","1234","test@mail.com"));
    }

    @Test
    public void clearSuccess() throws Exception {
        UserData user = dao.getUser("HereToStay");
        Assertions.assertNotNull(user);

        dao.clear();

        user = dao.getUser("HereToStay");
        Assertions.assertNull(user);
    }
}
