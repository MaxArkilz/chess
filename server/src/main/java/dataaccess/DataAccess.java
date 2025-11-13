package dataaccess;

import exception.ResponseException;
import model.*;

import java.sql.SQLException;

public interface DataAccess {

    void clear() throws ResponseException, DataAccessException, SQLException;

    void createUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
//
    int createGame(GameData game) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    GameData gameName(String gameName) throws DataAccessException;
    Iterable<GameData> listGames() throws DataAccessException;
    void updateGame(GameData gameID) throws DataAccessException;
//
    void createAuth(AuthData auth) throws DataAccessException;
    AuthData getAuth(String token) throws DataAccessException;
    void deleteAuth(String token) throws DataAccessException;
}
