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
            throw new ResponseException(ResponseException.Code.Unauthorized, "Error: unauthorized");
        }

        Iterable<GameData> games = dao.listGames();

        List<GameData> gameList = new ArrayList<>();
        games.forEach(gameList::add);

        return gameList;
    }

    public GameData.CreateGameResponse createGame(String authToken, GameData.CreateGameRequest request) throws DataAccessException {
        var auth = dao.getAuth(authToken);
        if (auth == null) {
            throw new ResponseException(ResponseException.Code.Unauthorized, "Error: unauthorized");
        }

        if (request.gameName() == null){
            throw new ResponseException(ResponseException.Code.ClientError, "Error: bad request");
        }

        int newGameID = dao.getGameID();
        var newGame = new GameData(newGameID,null,null,request.gameName(),null);
        dao.createGame(newGame);

        return new GameData.CreateGameResponse(newGameID);
    }

    public void joinGame(String authToken, GameData.JoinGameRequest request) throws DataAccessException {
        var auth = dao.getAuth(authToken);
        if (auth == null) {
            throw new ResponseException(ResponseException.Code.Unauthorized, "Error: unauthorized");}
        if (request.playerColor() == null){
            throw new ResponseException(ResponseException.Code.ClientError, "Error: bad request");}

        var game = dao.getGame(request.gameID());
        String username = auth.username();


        if (!request.playerColor().equals("WHITE") && !request.playerColor().equals("BLACK")) {
            throw new ResponseException(ResponseException.Code.ClientError, "Error: bad request");}
        if (game == null) {
            throw new ResponseException(ResponseException.Code.ClientError, "Error: game not found");}

        if ((request.playerColor().equals("WHITE")
                && game.whiteUsername() != null)
                || (request.playerColor().equals("BLACK")
                && game.blackUsername() != null)){
            throw new ResponseException(ResponseException.Code.Forbidden, "Error: already taken");}

        GameData updatedGame;
        if (request.playerColor().equals("WHITE")) {
            updatedGame = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
        } else {
            updatedGame = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());}
        dao.createGame(updatedGame);
    }
}
