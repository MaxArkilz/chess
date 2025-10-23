package dataaccess;
import model.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DataAccessMemory implements DataAccess{

    private final Map<String, UserData> users = new HashMap<>();
    private final Map<Integer, GameData> games = new HashMap<>();
    private final Map<String, AuthData> auths = new HashMap<>();
    private final AtomicInteger nextGameID = new AtomicInteger(1);

    @Override
    public void clear() {
        users.clear();
        games.clear();
        auths.clear();
        nextGameID.set(1);
    }

    @Override
    public void createUser(UserData user) {
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public void createAuth(AuthData auth) {
        auths.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String token) {
        return auths.get(token);
    }

    @Override
    public void deleteAuth(String token) {
        auths.remove(token);
    }


}
