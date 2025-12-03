package clientside;
import exception.ResponseException;
import websocketClient.WebSocketFacade;

import java.util.Arrays;
import java.util.Scanner;
import static ui.EscapeSequences.*;
public class GameplayClient {


    private static State state;

    public GameplayClient(ServerFacade server, WebSocketFacade ws) {
    }

    public State run(int gameID, String color, String mode, State s) {

//        System.out.print(help());
        state = s;
        Scanner scanner = new Scanner(System.in);

        printBoard(gameID, color, mode);
        printPrompt();

        String line = scanner.nextLine();
        String result = eval(line);
        System.out.print(SET_TEXT_COLOR_BLUE + result);
        return state;
    }

    public String help() {
        return SET_TEXT_COLOR_BLUE + """
                \nexit  - exit this game
                help   - show this menu
                """;

    }

    public void printPrompt() {
        System.out.print("\n\n" + RESET_TEXT_COLOR
                +"["+ state+"]" + ">>> " + SET_TEXT_COLOR_GREEN);
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "exit" -> exit();
                default -> help();
            };
        } catch (ResponseException e) {
            return SET_TEXT_COLOR_RED + e.getMessage();
        }
    }

    public String exit() {
        state = State.SIGNEDIN;
        return SET_TEXT_COLOR_MAGENTA + "Returning to home!\n" + RESET_TEXT_COLOR;
    }

    public void printBoard(int gameID, String color, String mode) {

        String[][] board;
        String[] columns;
        String[] rows;
        if (color.equalsIgnoreCase("WHITE")) {
            board = new String[][]{
                    {"r","n","b","q","k","b","n","r"},
                    {"p","p","p","p","p","p","p","p"},
                    {"","","","","","","",""},
                    {"","","","","","","",""},
                    {"","","","","","","",""},
                    {"","","","","","","",""},
                    {"P","P","P","P","P","P","P","P"},
                    {"R","N","B","Q","K","B","N","R"}
            };
            columns = new String[]{"A","B","C","D","E","F","G","H"};
            rows = new String[]{"8","7","6","5","4","3","2","1"};
        } else {
            board = new String[][]{
                    {"R","N","B","Q","K","B","N","R"},
                    {"P","P","P","P","P","P","P","P"},
                    {"","","","","","","",""},
                    {"","","","","","","",""},
                    {"","","","","","","",""},
                    {"","","","","","","",""},
                    {"p","p","p","p","p","p","p","p"},
                    {"r","n","b","q","k","b","n","r"}
            };
            columns = new String[]{"H","G","F","E","D","C","B","A"};
            rows = new String[]{"1","2","3","4","5","6","7","8"};
        }

        System.out.print(SET_BG_COLOR_BLACK + "   "+"\u2004");
        for (String col : columns) {
            System.out.print(SET_TEXT_COLOR_WHITE+" "+col+"\u2003");
        }
        System.out.println();

        for (int row = 0; row < 8; row ++) {
            System.out.print(SET_TEXT_COLOR_WHITE + " " +rows[row]+" ");
            for (int col = 0; col < 8; col++) {
                boolean lightSquare = (row + col) % 2 == 0;
                String br;
                String tr;
                if (color.equalsIgnoreCase("white")){
                    br = SET_BG_COLOR_LIGHT_GREY;
                    tr=SET_BG_COLOR_DARK_GREY;} else {br=SET_BG_COLOR_DARK_GREY;tr=SET_BG_COLOR_LIGHT_GREY;}
                String square = lightSquare ? br : tr;
                String piece = pieceUpgrades(board[row][col]);
                System.out.print(square + piece + RESET_BG_COLOR);
            }
            System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + " " + rows[row] + " ");
            System.out.println();
        }
        System.out.print(SET_BG_COLOR_BLACK + "   "+"\u2004");
        for (String col : columns) {
            System.out.print(SET_TEXT_COLOR_WHITE+" "+col+"\u2003");
        }
        System.out.println();
        System.out.print(RESET_BG_COLOR);
    }

    private static String pieceUpgrades(String piece) {
        return switch (piece) {
            case "K" -> SET_TEXT_COLOR_WHITE + WHITE_KING;
            case "Q" -> SET_TEXT_COLOR_WHITE + WHITE_QUEEN;
            case "R" -> SET_TEXT_COLOR_WHITE + WHITE_ROOK;
            case "B" -> SET_TEXT_COLOR_WHITE + WHITE_BISHOP;
            case "N" -> SET_TEXT_COLOR_WHITE + WHITE_KNIGHT;
            case "P" -> SET_TEXT_COLOR_WHITE + WHITE_PAWN;
            case "k" -> SET_TEXT_COLOR_BLACK + BLACK_KING;
            case "q" -> SET_TEXT_COLOR_BLACK + BLACK_QUEEN;
            case "r" -> SET_TEXT_COLOR_BLACK + BLACK_ROOK;
            case "b" -> SET_TEXT_COLOR_BLACK + BLACK_BISHOP;
            case "n" -> SET_TEXT_COLOR_BLACK + BLACK_KNIGHT;
            case "p" -> SET_TEXT_COLOR_BLACK + BLACK_PAWN;
            default   -> EMPTY;
        };
    }

}
