package dataaccess;

import model.*;

public interface DataAccess {

    void clear();

    void createUser(UserData user);
    UserData getUser(String username);
//
    void createGame(GameData game);
    GameData getGame(int gameID);
    Iterable<GameData> listGames();
    int getGameID();
//    void updateGame(GameData game);
//
    void createAuth(AuthData auth);
    AuthData getAuth(String token);
    void deleteAuth(String token);
}
