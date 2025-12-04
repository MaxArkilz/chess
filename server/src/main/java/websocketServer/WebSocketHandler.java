package websocketServer;
import chess.ChessGame;
import com.google.gson.Gson;
import io.javalin.websocket.*;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import websocket.commands.UserGameCommand;
import websocket.commands.UserGameCommand.CommandType;

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
        try {
            switch (action.getCommandType()) {
                case CONNECT -> {
                    var cmd = (C)
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

    public void handleMove(WsMessageContext ctx, UserGameCommand command) {
        /* TODO: extend UserGameCommand to allow command.getMove
            try/catch to make move through gameService
            pull updated game from database and push to all clients
            notify all clients of the move
         */
    }
}
