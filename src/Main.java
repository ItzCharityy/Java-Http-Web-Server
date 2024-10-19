import acsse.csc2b.HttpServer;

/**
 * Entry point for the HTTP server application.
 * Initializes and starts the HTTP server on a specified port.
 * 
 * @author Moyo Rc 222022568
 * @version P03
 */
public class Main {

    public static void main(String[] args) {
        HttpServer server = new HttpServer(4321);
        server.startServer();
    }
}
