package clientSide;

import exception.ResponseException;
import model.AuthData;
import model.UserData;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PreloginClient {
    private final ServerFacade server;
    private static State state = State.SIGNEDOUT;
    private AuthData authData = null;

    public record PrelogResult(String authToken, State state){};

    public PreloginClient(ServerFacade server) throws ResponseException {
        this.server = server;
    }

    public PrelogResult run() {

        Scanner scanner = new Scanner(System.in);

        printPrompt();
        String line = scanner.nextLine();
        String result = eval(line);
        System.out.print(SET_TEXT_COLOR_BLUE + result);
        if (authData != null) {
            return new PrelogResult(authData.authToken(), state);
        } else {return new PrelogResult(null, state);}
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens,1,tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> quit();
                default -> help();
            };
        } catch (Exception e) {
            return SET_TEXT_COLOR_RED + e.getMessage();
        }
    }

    public String help() {
        return SET_TEXT_COLOR_BLUE + """
            \nregister <USERNAME> <PASSWORD> <EMAIL> - to create an account
            login <USERNAME> <PASSWORD>            - to play chess
            quit                                   - exit this program
            help                                   - show this menu
            """;
    }

    private void printPrompt() {
        System.out.print("\n\n" + RESET_TEXT_COLOR
                +"["+ state+"]" + ">>> " + SET_TEXT_COLOR_GREEN);
    }

    public String register(String[] params){
        if (params.length < 3) {
            return SET_TEXT_COLOR_RED + "Usage: register <USERNAME> <PASSWORD> <EMAIL>";
        }
        String username = params[0];
        String password = params[1];
        String email = params[2];
        try {
            AuthData auth = server.register(new UserData.RegisterRequest(username,password,email));
            authData = auth;
            state = State.SIGNEDIN;
            return SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE +
                    "  Registration successful. Logged in as " + username + ".  "
                    + RESET_TEXT_COLOR + RESET_BG_COLOR;
        } catch (ResponseException e) {
            return SET_TEXT_COLOR_RED + "Registration failed: "+ e.getMessage();
        }
    }

    public String login(String[] params) {
        if (params.length < 2) {
            return SET_TEXT_COLOR_RED + "Usage: login <USERNAME> <PASSWORD>";
        }
        String username = params[0];
        String password = params[1];
        try {
            authData = server.login(new UserData.LoginRequest(username,password));
            state = State.SIGNEDIN;
            return SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE + " Login successful. Welcome back "+ username + " "
                    + RESET_TEXT_COLOR + RESET_BG_COLOR;
        } catch (Exception e) {
            return SET_TEXT_COLOR_RED + "Login failed: " + e.getMessage();
        }
    }

    public String quit() {
        state = State.EXIT;
        return SET_TEXT_COLOR_BLUE + "Goodbye!\n" + RESET_TEXT_COLOR;
    }
}
