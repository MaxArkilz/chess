package dataaccess;

import exception.ResponseException;

import java.sql.Connection;
import java.sql.SQLException;

public class MySqlDataAccess {


    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS game(
            'id' int NOT NULL AUTO_INCREMENT,
            'name' varchar(256) NOT NULL,
            'playerColor' ENUM('WHITE', 'BLACK') DEFAULT 'WHITE',
            'json' TEXT DEFAULT NULL,
            PRIMARY KEY ('id'),
            INDEX(name)
            )
"""
    };
    
    private void configureDatabase() throws DataAccessException, SQLException {
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
