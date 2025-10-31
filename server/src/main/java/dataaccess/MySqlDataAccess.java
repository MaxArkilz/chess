package dataaccess;

import chess.ChessGame;
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
    public void createGame(GameData game) throws DataAccessException {
        var statement =
                "INSERT INTO game (name,chessGame) VALUES (?,?)";

        try (Connection connection = DatabaseManager.getConnection();

             PreparedStatement ps = connection.prepareStatement(statement)) {

            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SHOW DATABASES");
            while (resultSet.next()) {
                String dbName = resultSet.getString(1);
                System.out.println("Database: " + dbName);  // or collect/assert values
            }

            ps.setString(1, game.gameName());
            var json = new Gson().toJson(game.game());
            ps.setString(2, json);

            ps.executeUpdate();

        } catch (SQLException | DataAccessException ex){
            throw new DataAccessException("Error creating user: " + ex.getMessage());
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        var statement =
                "SELECT gameID, whiteUsername, blackUsername, playerColor,name, chessGame FROM game WHERE gameID = ?";
        try (Connection connection = DatabaseManager.getConnection()) {
            var ps = connection.prepareStatement(statement);
            ps.setInt(1,gameID);


            try (var rs = ps.executeQuery()){
                if (rs.next()) {
                    var json = rs.getString("chessGame");
                    var game = new Gson().fromJson(json, ChessGame.class);

                    return new GameData(
                            rs.getInt("gameID"),
                            rs.getString("whiteUsername"),
                            rs.getString("blackUsername"),
                            rs.getString("name"),
                            game
                    );
                }
                return null;
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error getting game" + ex.getMessage());
        }
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
            `whiteUsername` varchar(256) DEFAULT NULL,
            `blackUsername` varchar(256) DEFAULT NULL,
            `playerColor` ENUM('WHITE', 'BLACK') DEFAULT 'WHITE',
            `name` varchar(256) NOT NULL,
            `chessGame` JSON DEFAULT NULL,
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
}
