package clientSide;

public class GameplayClient {


    private final ServerFacade server;
    private static State state = State.GAMEMODE;

    public GameplayClient(ServerFacade server) {
        this.server = server;
    }

    public State run() {
        //CHANGE BEFORE WRITING ANYTHING
        return State.EXIT;
    }
}
