package clientSide;

import model.GameData;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class GameplayClient {


    private final ServerFacade server;
    private static State state = State.GAMEMODE;

    public GameplayClient(ServerFacade server) {
        this.server = server;
    }

    public State run(int gameID, String color, String mode) {

//        System.out.print(help());
        Scanner scanner = new Scanner(System.in);

        printBoard(gameID, color, mode);
        printPrompt();
//        String line = scanner.nextLine();
//        String result = eval(line);
//        System.out.print(SET_TEXT_COLOR_BLUE + result);
        return State.GAMEMODE;
    }

    public String help() {
        return null;
    }

    public void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR
                +"["+ state+"]" + ">>> " + SET_TEXT_COLOR_GREEN);
    }

    public String eval(String input) {
        return null;
    }

    public void printBoard(int gameID, String color, String mode) {
        GameData game = server.getGame(gameID);




    }
}
