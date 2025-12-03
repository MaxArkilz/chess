import clientside.*;
import websocket.messages.ServerMessage;
import websocketClient.NotificationHandler;
import websocketClient.WebSocketFacade;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

public class Main implements NotificationHandler{
    private static State state = State.SIGNEDOUT;
    private static String authToken = null;
    private static int gameID = 0;
    private static String color = null;
    private static String mode = null;

    public void main(String[] args) {
        String serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }

        ServerFacade server = new ServerFacade(serverUrl);
        WebSocketFacade ws = new WebSocketFacade(serverUrl, this);

        PreloginClient prelog = new PreloginClient(server);
        PostloginClient postlog = new PostloginClient(server);
        GameplayClient game = new GameplayClient(server, ws);

        System.out.println(RESET_TEXT_COLOR + "\n ♕ Welcome to 240 chess. Type 'Help' to get started. ♛ ");
        while (state != State.EXIT) {
            if (state == State.SIGNEDOUT) {
                PreloginClient.PrelogResult result = prelog.run(state);
                authToken = result.authToken();
                state = result.state();
            }
            else if (state == State.SIGNEDIN) {
                PostloginClient.GameplayInfo result = postlog.run(authToken, state);
                state = result.state();
                gameID = result.gameId();
                color = result.playerColor();
                mode = result.mode();
            }
            else if (state == State.GAMEMODE) {
                ws.connect(authToken, gameID);
                state = game.run(gameID,color,mode,state);
            }

        }

    }


    @Override
    public void notify(ServerMessage notification) {
        // TODO: implement ws notifications
    }
}