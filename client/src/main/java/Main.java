import chess.*;

import clientSide.*;

import static ui.EscapeSequences.RESET_TEXT_COLOR;

public class Main {
    private static State state = State.SIGNEDOUT;

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
                state = prelog.run();
            }
            if (state == State.SIGNEDIN) {
                state = postlog.run();
            }
            if (state == State.GAMEMODE){
                state = game.run();
            }

        }

    }
}