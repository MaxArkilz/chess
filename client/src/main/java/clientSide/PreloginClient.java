package clientSide;

import exception.ResponseException;

public class PreloginClient {

    private String visitorName = null;
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;

    public PreloginClient(String serverUrl) throws ResponseException {
        server = new ServerFacade(serverUrl);
    }
}
