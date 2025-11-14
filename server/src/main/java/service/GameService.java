package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.DataAccessMemory;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GameService {

    private final DataAccess dao;

    public GameService(DataAccess dao) {
        this.dao = dao;
    }

    public List<GameData> listGames(String authToken) throws DataAccessException {
        var auth = dao.getAuth(authToken);
        if (auth == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }

        Iterable<GameData> games = dao.listGames();

        List<GameData> gameList = new ArrayList<>();
        games.forEach(gameList::add);

        return gameList;
    }

    public GameData.CreateGameResponse createGame(String authToken, GameData.CreateGameRequest request) throws DataAccessException {
        var auth = dao.getAuth(authToken);
        if (auth == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }

        if (request.gameName() == null){
            throw new ResponseException(400, "Error: bad request");
        }

        if (dao.gameName(request.gameName()) != null){
            throw new ResponseException(400, "Error: game already exists with that name");
        }

        var newGame = new GameData(0,null,null,request.gameName(),new ChessGame());
        int newGameID = dao.createGame(newGame);

        return new GameData.CreateGameResponse(newGameID);
    }

    public void joinGame(String authToken, GameData.JoinGameRequest request) throws DataAccessException {
        var auth = dao.getAuth(authToken);
        if (auth == null) {
            throw new ResponseException(401, "Error: unauthorized");}
        if (request.playerColor() == null){
            throw new ResponseException(400, "Error: bad request");}

        var game = dao.getGame(request.gameID());
        String username = auth.username();


        if (!request.playerColor().equalsIgnoreCase("WHITE")
                && !request.playerColor().equalsIgnoreCase("BLACK")) {
            throw new ResponseException(400, "Error: bad request");}
        if (game == null) {
            throw new ResponseException(400, "Error: game not found");}

        if ((request.playerColor().equalsIgnoreCase("WHITE")
                && game.whiteUsername() != null)
                || (request.playerColor().equalsIgnoreCase("BLACK")
                && game.blackUsername() != null)){
            throw new ResponseException(403, "Error: already taken");}

        GameData updatedGame;
        if (request.playerColor().equalsIgnoreCase("WHITE")) {
            updatedGame = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
        } else {
            updatedGame = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());}
        dao.updateGame(updatedGame);
    }

    public ChessGame getGame(int gameID) throws DataAccessException {
        GameData game = dao.getGame(gameID);
        return game.game();
    }
}
