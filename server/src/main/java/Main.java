import dataaccess.DataAccess;
import dataaccess.DataAccessMemory;
import dataaccess.MySqlDataAccess;
import server.Server;
import service.GameService;

public class Main {
    public static void main(String[] args) {
        try {
            var port = 8080;

//            DataAccess dataAccess = new DataAccessMemory();
            DataAccess dataAccess = new MySqlDataAccess();

            System.out.printf("♕ 240 Chess Server using %s%n", dataAccess.getClass().getSimpleName());

            new Server(dataAccess).run(port);

        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }


    }
}