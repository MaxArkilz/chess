package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessMemory;
import exception.ResponseException;
import model.GameData;

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

        int newGameID = dao.getGameID();
        var newGame = new GameData(newGameID,null,null,request.gameName(),null);
        dao.createGame(newGame);

        return new GameData.CreateGameResponse(newGameID);
    }
}
