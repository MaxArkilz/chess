package dataaccess;
import model.*;
import java.util.*;

public class DataAccessMemory implements DataAccess{

    private final Map<String, UserData> users = new HashMap<>();
    private final Map<Integer, GameData> games = new HashMap<>();
    private final Map<String, AuthData> auths = new HashMap<>();
    private int GameID = 1000;

    @Override
    public void clear() {
        users.clear();
        games.clear();
        auths.clear();
        GameID = 1000;
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
    public void createGame(GameData game) {
        games.put(game.gameID(), game);
    }

    public int getGameID(){

        int currentGameID = GameID;
        GameID += 1;
        return currentGameID;
    }

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    @Override
    public Iterable<GameData> listGames() {
        return games.values();
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
