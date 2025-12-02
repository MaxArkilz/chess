package websocketClient;
import com.google.gson.Gson;
import exception.ResponseException;
import websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http","ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                public void onMessage(String message) {
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                    notificationHandler.notify(notification);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}

    /* TODO:
    *   -CONNECT endpoint
    *   -MAKE_MOVE endpoint
    *   -LEAVE endpoint
    *   -RESIGN endpoint
    * */

    public void connect(String authToken, int gameID) throws ResponseException{
        sendCommand(new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID));
    }

    public void sendCommand(UserGameCommand command) throws ResponseException {
        try {
            var json = new Gson().toJson(command);
            session.getBasicRemote().sendText(json);
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
}
