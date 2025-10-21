package dataaccess;
import model.*;

import java.util.HashMap;

public class DataAccessMemory implements DataAccess{

    final private HashMap<Integer, UserData> users = new HashMap<>();

    public UserData getUser(String id) {
        return users.get(id);
    }
}
