import chess.*;

import clientSide.*;

import static ui.EscapeSequences.RESET_TEXT_COLOR;

public class Main {
    private static State state = State.SIGNEDOUT;
    private static String authToken = null;
    private static int gameID = 0;
    private static String color = null;
    private static String mode = null;

    public static void main(String[] args) {
        String serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }

        ServerFacade server = new ServerFacade(serverUrl);

        PreloginClient prelog = new PreloginClient(server);
        PostloginClient postlog = new PostloginClient(server);
        GameplayClient game = new GameplayClient(server);

        System.out.println(RESET_TEXT_COLOR + "\n ♕ Welcome to 240 chess. Type 'Help' to get started. ♛ ");
        while (state != State.EXIT) {
            if (state == State.SIGNEDOUT) {
                PreloginClient.PrelogResult result = prelog.run();
                authToken = result.authToken();
                state = result.state();
            }
            if (state == State.SIGNEDIN) {
                PostloginClient.GameplayInfo result = postlog.run(authToken);
                state = result.state();
                gameID = result.gameId();
                color = result.playerColor();
                mode = result.mode();
            }
            if (state == State.GAMEMODE) {
                state = game.run(gameID,color,mode);
            }

        }

    }
}