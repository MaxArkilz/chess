package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.*;
import java.util.ArrayList;

public class MySqlDataAccess implements DataAccess{

    public MySqlDataAccess() throws DataAccessException {
        configureDatabase();
    }



    @Override
    public void clear() throws DataAccessException{
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
    public int createGame(GameData game) throws DataAccessException {
        String statement = "INSERT INTO game (name, chessGame) VALUES (?,?)";


        try (Connection connection = DatabaseManager.getConnection();

             PreparedStatement ps = connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {


            var json = new Gson().toJson(game.game());

            ps.setString(1, game.gameName());
            ps.setString(2, json);

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                } else {
                    throw new DataAccessException("No gameID returned from database!");
                }
            }

        } catch (SQLException ex){
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
    public void updateGame (GameData game) throws DataAccessException {
        String statement =
                    "UPDATE game SET whiteUsername = ?, blackUsername = ? WHERE gameID = ?";

        try (Connection connection = DatabaseManager.getConnection()){
            PreparedStatement ps = connection.prepareStatement(statement);

            ps.setString(1, game.whiteUsername());
            ps.setString(2, game.blackUsername());
            ps.setInt(3, game.gameID());

            ps.executeUpdate();

        } catch (SQLException ex) {
            throw new RuntimeException("Error updating game" + ex.getMessage());
        }
    }

    @Override
    public Iterable<GameData> listGames() throws DataAccessException {
        var games = new ArrayList<GameData>();
        try (Connection connection = DatabaseManager.getConnection()){
            var statement = "SELECT gameID, name, whiteUsername, blackUsername, chessGame FROM game";
            try (PreparedStatement ps = connection.prepareStatement(statement)){
                try (ResultSet rs = ps.executeQuery()){
                    while (rs.next()){
                        var id = rs.getInt("gameID");
                        var name = rs.getString("name");
                        var whiteUsername = rs.getString("whiteUsername");
                        var blackUsername = rs.getString("blackUsername");
                        var json = rs.getString("chessGame");
                        ChessGame game = new Gson().fromJson(json, ChessGame.class);

                        games.add(new GameData(id, whiteUsername, blackUsername, name, game));
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error listing games: " + ex.getMessage());
        }
        return games;
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(statement)) {

            ps.setString(1, auth.authToken());
            ps.setString(2, auth.username());
            ps.executeUpdate();

        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException("Error creating auth: " + ex.getMessage());
        }
    }


    @Override
    public AuthData getAuth(String token) throws DataAccessException {
        var statement = "SELECT authToken, username FROM auth WHERE authToken = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(statement)) {

            ps.setString(1, token);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new AuthData(rs.getString("authToken"), rs.getString("username"));
                }
                return null;
            }
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException("Error retrieving auth: " + ex.getMessage());
        }
    }


    @Override
    public void deleteAuth(String token) throws DataAccessException {
        var statement = "DELETE FROM auth WHERE authToken = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(statement)) {

            ps.setString(1, token);
            ps.executeUpdate();
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException("Error deleting auth: " + ex.getMessage());
        }
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
                    500,
                    String.format("Unable to configure database: %s", ex.getMessage()));
        }

    }
}
