package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessMemory;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GameService {

    private final DataAccess dao;

    public GameService(DataAccessMemory dao) {
        this.dao = dao;
    }

    public List<GameData> listGames(String authToken) {
        var auth = dao.getAuth(authToken);
        if (auth == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }

        Iterable<GameData> games = dao.listGames();

        List<GameData> gameList = new ArrayList<>();
        games.forEach(gameList::add);

        return gameList;
    }

    public GameData.CreateGameResponse createGame(String authToken, GameData.CreateGameRequest request) {
        var auth = dao.getAuth(authToken);
        if (auth == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }

        if (request.gameName() == null){
            throw new ResponseException(400, "Error: bad request");
        }

        int newGameID = dao.getGameID();
        var newGame = new GameData(newGameID,null,null,request.gameName(),null);
        dao.createGame(newGame);

        return new GameData.CreateGameResponse(newGameID);
    }

    public void joinGame(String authToken, GameData.JoinGameRequest request) {
        var auth = dao.getAuth(authToken);
        var game = dao.getGame(request.gameID());
        String username = auth.username();
        var playerColor = request.color();

        if (auth == null) {
            throw new ResponseException(401, "Error: unauthorized");}
        if (playerColor != ChessGame.TeamColor.WHITE && playerColor != ChessGame.TeamColor.BLACK) {
            throw new ResponseException(400, "Error: bad request");}
        if (game == null) {
            throw new ResponseException(400, "Error: game not found");}

        if (playerColor == ChessGame.TeamColor.WHITE && game.whiteUsername() != null || playerColor == ChessGame.TeamColor.BLACK && game.blackUsername() != null){
            throw new ResponseException(403, "Error: already taken");}

        GameData updatedGame;
        if (playerColor == ChessGame.TeamColor.WHITE) {
            updatedGame = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
        } else {
            updatedGame = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());}
        dao.createGame(updatedGame);
    }
}
