package clientSide;
import chess.ChessGame;
import model.GameData;
import ui.EscapeSequences;

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

        String line = scanner.nextLine();
        String result = eval(line);
        System.out.print(SET_TEXT_COLOR_BLUE + result);
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

        ;
    }

    public void printBoard(int gameID, String color, String mode) {

        String[][] board = {
                {"r","n","b","q","k","b","n","r"},
                {"p","p","p","p","p","p","p","p"},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"P","P","P","P","P","P","P","P"},
                {"R","N","B","Q","K","B","N","R"}
        };
        for (int row = 0; row < 8; row ++) {
            for (int col = 0; col < 8; col++) {
                boolean lightSquare = (row + col) % 2 == 0;
                String square = lightSquare ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_DARK_GREY;
                String piece = pieceUpgrades(board[row][col]);
                System.out.print(square + piece + RESET_BG_COLOR);
            }
            System.out.println();
        }
    }

    private static String pieceUpgrades(String piece) {
        return switch (piece) {
            case "K" -> WHITE_KING;
            case "Q" -> WHITE_QUEEN;
            case "R" -> WHITE_ROOK;
            case "B" -> WHITE_BISHOP;
            case "N" -> WHITE_KNIGHT;
            case "P" -> WHITE_PAWN;
            case "k" -> BLACK_KING;
            case "q" -> BLACK_QUEEN;
            case "r" -> BLACK_ROOK;
            case "b" -> BLACK_BISHOP;
            case "n" -> BLACK_KNIGHT;
            case "p" -> BLACK_PAWN;
            default   -> EMPTY;
        };
    }

}
