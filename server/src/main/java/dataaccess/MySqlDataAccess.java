package dataaccess;

import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.*;

public class MySqlDataAccess implements DataAccess{

    public MySqlDataAccess() throws ResponseException, DataAccessException {
        configureDatabase();
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS user(
            `username` varchar(256) NOT NULL,
            `password` varchar(256) NOT NULL,
            `email` varchar(256) NOT NULL,
            PRIMARY KEY (`username`)
            )
""",
            """
            CREATE TABLE IF NOT EXISTS auth(
            `authToken` varchar(256),
            `username` varchar(256),
            FOREIGN KEY (`username`) REFERENCES user (`username`)
            )
""",
            """
            CREATE TABLE IF NOT EXISTS game(
            `gameID` int NOT NULL AUTO_INCREMENT,
            `name` varchar(256) NOT NULL,
            `playerColor` ENUM('WHITE', 'BLACK') DEFAULT 'WHITE',
            `json` JSON DEFAULT NULL,
            PRIMARY KEY (`gameID`),
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
    public void clear() throws ResponseException, DataAccessException {
            Connection connection = DatabaseManager.getConnection();
            try (var statement = connection.createStatement()) {

                statement.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");
                statement.executeUpdate("TRUNCATE TABLE user");
                statement.executeUpdate("TRUNCATE TABLE auth");
                statement.executeUpdate("TRUNCATE TABLE game");
                statement.executeUpdate("SET FOREIGN_KEY_CHECKS = 1");

            } catch (SQLException ex) {
                throw new DataAccessException("Error clearing tables: " + ex.getMessage());
            }

    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        var statement = "INSERT INTO user (username,password,email) VALUES (?,?,?)";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(statement)) {
            ps.setString(1, user.username());
            ps.setString(2, user.password());
            ps.setString(3, user.email());

            ps.executeUpdate();

        } catch (SQLException ex){
            throw new DataAccessException("Error creating user " + ex.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        var statement = "SELECT username, password, email FROM user WHERE username = ?";
        try (Connection connection = DatabaseManager.getConnection()) {
            var ps = connection.prepareStatement(statement);
            ps.setString(1,username);
            try (var rs = ps.executeQuery()){
                if (rs.next()) {
                    return new UserData(rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"));
                }
                return null;
            }
        } catch (SQLException ex){
            throw new DataAccessException("Error getting user " + ex.getMessage());
        }
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
