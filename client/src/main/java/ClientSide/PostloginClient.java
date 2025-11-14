package ClientSide;

import exception.ResponseException;
import model.GameData;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PostloginClient {

    private final ServerFacade server;
    private static State state = State.SIGNEDIN;
    private String authToken = null;
    private int gameID = 0;
    private String playerColor = null;
    private String mode = null;

    public record GameplayInfo(State state, int gameId, String mode, String playerColor) {}

    public PostloginClient(ServerFacade server) {
        this.server = server;
    }

    public GameplayInfo run(String auth) {

        authToken = auth;
        Scanner scanner = new Scanner(System.in);

        printPrompt();
        String line = scanner.nextLine();
        String result = eval(line);
        System.out.print(SET_TEXT_COLOR_BLUE + result);

        return new GameplayInfo(state, gameID, mode, playerColor);
    }

    public String help() {

        return SET_TEXT_COLOR_BLUE + """
              \ncreate <NAME>            - make a new game
              list                     - list all games
              join <ID> [WHITE|BLACK]  - join an existing game
              observe <ID>             - watch an existing game
              logout                   - logout of this session
              quit                     - close the program
              help                     - show this menu
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
            String[] params = Arrays.copyOfRange(tokens,1,tokens.length);
            return switch (cmd) {
                case "create" -> create(params);
                case "list" -> list();
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "logout" -> logout();
                case "quit" -> quit();
                default -> help();
            };
        } catch (Exception e) {
            return SET_TEXT_COLOR_RED + "Error: " + e.getMessage();
        }
    }

    public String create(String[] params) {
        if (params.length < 1) {
            return SET_TEXT_COLOR_RED + "Usage: create <NAME>.";
        }
        String gameName = params[0];
        try {
            GameData gameData = server.createGame(authToken,new GameData.CreateGameRequest(gameName));
            return SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE +
                    " Successfully created game: " + gameName + " with game ID: " + gameData.gameID()+ " "
                    + RESET_BG_COLOR+ RESET_TEXT_COLOR;
        } catch (ResponseException e) {
            return SET_TEXT_COLOR_RED + "Game creation failed: " + e.getMessage();
        }
    }

    public String list() {
        try {
            List<GameData> gameList = server.listGames(authToken);
            System.out.println(
                    SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE +
                    " GAMES LIST " +
                    RESET_BG_COLOR + RESET_TEXT_COLOR);
            int lineNum = 1;
            for (GameData game : gameList) {
                System.out.println(SET_TEXT_COLOR_BLACK +
                        lineNum + "-- " + "Name: "+game.gameName() + " | ID: " + game.gameID() + " | White Player: "
                + game.whiteUsername() + " | Black Player: " + game.blackUsername());
                lineNum += 1;
            }
            return SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE + " End of games list "
                    + RESET_BG_COLOR+ RESET_TEXT_COLOR;
        } catch (ResponseException e) {
            return SET_TEXT_COLOR_RED + "Failed to list games: " + e.getMessage();
        }
    }

    public String join(String[] params) {
        if (params.length < 2){
            return SET_TEXT_COLOR_RED +
                    "Usage: join <ID> [WHITE|BLACK]. (Input game id and choose WHITE or BLACK).";}
        String s = params[0];
        int id = Integer.parseInt(s);
        String color = params[1];

        try {
            server.joinGame(authToken,new GameData.JoinGameRequest(color, id));
            state = State.GAMEMODE;
            gameID = id;
            mode = "join";
            playerColor = color;
            return SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE + "Successfully joined game: "
                    + id + " as " + color + RESET_BG_COLOR+ RESET_TEXT_COLOR+ "\n\n";
        } catch (ResponseException e) {
            return SET_TEXT_COLOR_RED + "Failed to join game" + id + ": " + e.getMessage();
        }
    }

    public String observe(String[] params) {
        if (params.length < 1){
            return SET_TEXT_COLOR_RED +
                    "Usage: observe <ID>. (Input game id).";}
        int id;
        try {
            String s = params[0];
            id = Integer.parseInt(s);
        } catch (Exception e){
            return SET_TEXT_COLOR_RED + "ID must be an integer.";
        }
        try {
            List<GameData> gameList = server.listGames(authToken);
            boolean gameExists = false;
            for(GameData game : gameList){
                if (game.gameID() == id) {
                    gameExists = true;
                    break;
                }
            }
            if (!gameExists) {
                return SET_TEXT_COLOR_RED + "No game found with ID " + id;
            }
            state = State.GAMEMODE;
            gameID = id;
            mode = "observe";
            playerColor = "WHITE";
            return SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE + " Now observing game: " + id + " " +
                    RESET_BG_COLOR+ RESET_TEXT_COLOR+ "\n\n";
        } catch (ResponseException e) {
            return SET_TEXT_COLOR_RED + "Failed to launch game" + id + ": " + e.getMessage();
        }
    }

    public String logout() {
        state = State.SIGNEDOUT;
        return SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE + "Successfully logged out"
                +RESET_BG_COLOR+ RESET_TEXT_COLOR;
    }

    public String quit() {
        state = State.EXIT;
        return SET_TEXT_COLOR_BLUE + "Goodbye! :)";
    }

}
