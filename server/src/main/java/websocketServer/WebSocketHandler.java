package websocketServer;
import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import exception.ResponseException;
import io.javalin.websocket.*;
import model.GameData;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import websocket.commands.ConnectCommand;
import websocket.commands.UserGameCommand;
import websocket.commands.UserGameCommand.CommandType;
import websocket.messages.ServerMessage;

import javax.swing.*;
import javax.websocket.Session;
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
        try {
            switch (action.getCommandType()) {
                case CONNECT -> {
                    var cmd = (ConnectCommand) action;
                    co
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext wsCloseContext) throws Exception {
        System.out.println("Websocket closed");
    }

    private void connect(ConnectCommand cmd, Session session) throws ResponseException, IOException, DataAccessException {
        try {
            var request = new GameData.JoinGameRequest(cmd.getPlayerRole(),cmd.getGameID());
            gameService.joinGame(cmd.getAuthToken(),request);

            connections.add(session);

            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            connections.broadcast(session,notification);
        } catch (ResponseException | DataAccessException ex) {
            var errorMsg = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            connections.
        }
    }
}
