package websocketServer;
import chess.ChessGame;
import com.google.gson.Gson;
import io.javalin.websocket.*;
import org.jetbrains.annotations.NotNull;
import websocket.commands.UserGameCommand;
import websocket.commands.UserGameCommand.CommandType;

import javax.swing.*;
import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final ConnectionManager connections = new ConnectionManager();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket Connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public  void handleMessage(WsMessageContext ctx) {
        UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
        try {
            switch (command.getCommandType()) {
                case CONNECT-> handleConnect(ctx);
                case MAKE_MOVE -> handleMove(ctx,command, );
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext wsCloseContext) throws Exception {
        System.out.println("Websocket closed");
    }

    public void handleMove(WsMessageContext ctx, UserGameCommand command, String username, int gameID, ChessGame game) {
        /* TODO: extend UserGameCommand to allow command.getMove
            try/catch to make move through gameService
            pull updated game from database and push to all clients
            notify all clients of the move
         */
    }
}
