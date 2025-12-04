package websocketServer;
import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import exception.ResponseException;
import io.javalin.websocket.*;
import model.GameData;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import websocket.commands.*;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand.CommandType;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.swing.*;
import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final Gson gson = new Gson();
    private final GameService gameService;

    public WebSocketHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket Connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public  void handleMessage(WsMessageContext ctx) {
        UserGameCommand action = gson.fromJson(ctx.message(), UserGameCommand.class);
        Session session = (Session) ctx.session;
        try {
            switch (action.getCommandType()) {
                case CONNECT -> {
                    ConnectCommand cmd = (ConnectCommand) action;
                    connect(cmd,session);
                }
                case MAKE_MOVE -> {
                    MakeMoveCommand cmd = (MakeMoveCommand) action;
                    makeMove(cmd, session);
                }
                case LEAVE -> {
                    LeaveCommand cmd = (LeaveCommand) action;
                    leave(cmd, session);
                }
                case RESIGN -> {
                    ResignCommand cmd = (ResignCommand) action;
                    resign(cmd, session);
                }
            }
        } catch (IOException ex) {
            sendError(session, "invalid command - " + ex.getMessage());
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext wsCloseContext) throws Exception {
        System.out.println("Websocket closed");
    }

    private void connect(ConnectCommand cmd, Session session) throws ResponseException, IOException {
        try {
            var request = new GameData.JoinGameRequest(cmd.getPlayerRole(),cmd.getGameID());
            gameService.joinGame(cmd.getAuthToken(),request);

            connections.add(session);

            ChessGame gameData = gameService.getGame(cmd.getGameID());
            var loadMsg = new LoadGameMessage(gameData);
            String loadJson = gson.toJson(loadMsg);
            session.getRemote().sendString(loadJson);

            String role = cmd.getPlayerRole().equalsIgnoreCase("WHITE") ||
                    cmd.getPlayerRole().equalsIgnoreCase("BLACK") ? " as " +
                    cmd.getPlayerRole().toUpperCase() : " as an observer";

//            String noteText = gameData + " joined game " + cmd.getGameID() + role;

//            var notification = new NotificationMessage(noteText);
//            connections.broadcast(session, notification);

        } catch (ResponseException | DataAccessException ex) {
            try {
                var errorMsg = new ErrorMessage(ex.getMessage());
                String errorJson = new Gson().toJson(errorMsg);
                session.getRemote().sendString(errorJson);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void makeMove(MakeMoveCommand cmd, Session session) {
        // validate auth/game, update game, broadcast LOAD_GAME to all, NOTIFICATION to others
    }

    private void leave(LeaveCommand cmd, Session session) {
        // remove this session from this game's notifications, broadcast NOTIFICATION to others
    }

    private void resign(ResignCommand cmd, Session session) {
        // mark game over, broadcast LOAD_GAME (final state) and NOTIFICATION that player resigned
    }

    private void sendError(Session session, String message) {
        try {
            ErrorMessage error = new ErrorMessage(message);
            String json = gson.toJson(error);
            session.getRemote().sendString(json);
        } catch (IOException ignored) {
        }
    }
}
