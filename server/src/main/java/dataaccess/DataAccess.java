package dataaccess;

import exception.ResponseException;
import model.*;

import java.sql.SQLException;

public interface DataAccess {

    void clear() throws ResponseException, DataAccessException, SQLException;

    void createUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
//
    void createGame(GameData game) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    Iterable<GameData> listGames();
    int getGameID();
//    void updateGame(GameData game);
//
    void createAuth(AuthData auth);
    AuthData getAuth(String token);
    void deleteAuth(String token);
}
