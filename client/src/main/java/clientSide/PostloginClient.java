package clientSide;

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

    public PostloginClient(ServerFacade server) {
        this.server = server;
    }

    public State run(String auth) {

        authToken = auth;

        System.out.print(help());
        Scanner scanner = new Scanner(System.in);

        printPrompt();
        String line = scanner.nextLine();
        String result = eval(line);
        System.out.print(SET_TEXT_COLOR_BLUE + result);
        return state;
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
        System.out.print("\n" + RESET_TEXT_COLOR
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
            return "Successfully created game: " + gameName + " with game ID: " + gameData.gameID();
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
            for (GameData game : gameList) {
                System.out.println(SET_TEXT_COLOR_BLACK + "Name: "+game.gameName() + " ID: "+game.gameID());
            }
            return "All games printed";
        } catch (ResponseException e) {
            return SET_TEXT_COLOR_RED + "Failed to list games: " + e.getMessage();
        }
    }

    public String join(String[] params) {
        return null;
    }

    public String observe(String[] params) {
        return null;
    }

    public String logout() {
        return null;
    }

    public String quit() {
        return null;
    }

}
