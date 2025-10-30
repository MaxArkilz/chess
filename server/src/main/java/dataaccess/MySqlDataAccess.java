package dataaccess;

import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.Connection;
import java.sql.SQLException;

public class MySqlDataAccess implements DataAccess{

    public MySqlDataAccess() throws ResponseException, DataAccessException {
        configureDatabase();
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS game(
            `id` int NOT NULL AUTO_INCREMENT,
            `name` varchar(256) NOT NULL,
            `playerColor` ENUM('WHITE', 'BLACK') DEFAULT 'WHITE',
            `json` JSON DEFAULT NULL,
            PRIMARY KEY (`id`),
            INDEX(`name`)
            )
"""
    };
    
    private void configureDatabase() throws DataAccessException{
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)){
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new ResponseException(
                    ResponseException.Code.ServerError,
                    String.format("Unable to configure database: %s", ex.getMessage()));
        }

    }

    @Override
    public void clear() {

    }

    @Override
    public void createUser(UserData user) {

    }

    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public void createGame(GameData game) {

    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public Iterable<GameData> listGames() {
        return null;
    }

    @Override
    public int getGameID() {
        return 0;
    }

    @Override
    public void createAuth(AuthData auth) {

    }

    @Override
    public AuthData getAuth(String token) {
        return null;
    }

    @Override
    public void deleteAuth(String token) {

    }
}
