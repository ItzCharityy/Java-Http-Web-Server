package acsse.csc2b;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * A basic HTTP server that listens on a specified port and handles client connections.
 * The server is multi-threaded, handling each client connection in a separate thread.
 * 
 * @author Moyo Rc 222022568
 * @version P03
 */
public class HttpServer {
    
    private ServerSocket server;
    boolean running = false;
    
    /**
     * Default Constructor
     */
    public HttpServer() {}
    
    /**
     * Constructs a new HttpServer that listens on the specified port.
     * 
     * @param port the port on which the server listens for connections.
     */
    public HttpServer(int port) {
        try {
            server = new ServerSocket(port);
            this.running = true;
            System.out.print("Server Running ");
        } catch (IOException e) {
            System.err.println("Could not connect to server");
            e.printStackTrace();
        }
    }
    
    /**
     * Starts the server, accepting client connections and handling them in separate threads.
     */
    public void startServer() {
        while (running) {
            try {
                Thread td = new Thread(new ClientHandler(server.accept()));
                td.start();
                System.out.println("Server listening on port: " + server.getLocalPort());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
