import dataaccess.DataAccess;
import dataaccess.DataAccessMemory;
import dataaccess.MySqlDataAccess;
import server.Server;
import service.GameService;

public class Main {
    public static void main(String[] args) {
        try {
            var port = 8080;
            if (args.length >= 1) {
                port = Integer.parseInt(args[0]);
            }

            DataAccess dataAccess = new DataAccessMemory();
            if (args.length >= 2 && args[1].equals("sql")) {
                dataAccess = new MySqlDataAccess();
            }

            new Server().run(port);

        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }


        System.out.println("♕ 240 Chess Server");
    }
}