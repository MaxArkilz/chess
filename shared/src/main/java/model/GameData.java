package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {


    public record CreateGameRequest(String gameName) {}
    public record CreateGameResponse(int gameID) {}
    public record JoinGameRequest(String color, int gameID) {}
}
