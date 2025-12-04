package websocket.commands;

public class ConnectCommand extends UserGameCommand{

    private final String playerRole;

    public ConnectCommand(String authToken, Integer gameID, String playerRole) {
        super(CommandType.CONNECT, authToken, gameID);
        this.playerRole = playerRole;
    }

    public String getPlayerRole() {
        return playerRole;
    }
}
