import server.ChessServer;
import service.UserService;
import dataaccess.DataAccess;
import dataaccess.DataAccessMemory;

public class Main {
    public static void main(String[] args) {

        ChessServer server = new ChessServer();
        server.run(8080);

        System.out.println("♕ 240 Chess Server");
    }
}