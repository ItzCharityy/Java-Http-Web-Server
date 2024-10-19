package acsse.csc2b;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 * Handles individual client connections to the HTTP server.
 * Processes HTTP requests and serves the appropriate files or error messages.
 * 
 * @author Moyo Rc 222022568
 * @version P03
 */
public class ClientHandler implements Runnable {

    private Socket connection;

    /**
     * Constructs a ClientHandler to manage a client connection.
     * 
     * @param clientSocket the socket representing the client connection.
     */
    public ClientHandler(Socket clientSocket) {
        this.connection = clientSocket;
    }

    /**
     * Determines the MIME type of the requested file based on its extension.
     * 
     * @param filename the name of the requested file.
     * @return the MIME type as a string.
     */
    private String determineContentType(String filename) {
        if (filename.endsWith(".html")) {
            return "text/html";
        } else if (filename.endsWith(".jpg")) {
            return "image/jpeg";
        }
        return "application/octet-stream";
    }

    /**
     * Processes the client request, serves the requested file, or sends an appropriate
     * HTTP error response if the file is not found or if an error occurs.
     */
    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             DataOutputStream out = new DataOutputStream(new BufferedOutputStream(connection.getOutputStream()))) {

            String request = in.readLine();
            System.out.println(request);

            StringTokenizer tokenizer = new StringTokenizer(request);
            String requestType = tokenizer.nextToken();
            String filename = tokenizer.nextToken().substring(1);
            String filepath = "data/" + filename;

            File theFile = new File(filepath);

            if (theFile.exists()) {
                System.err.println("File was found");
                if (requestType.equals("GET")) {
                    String contentType = determineContentType(filename);

                    out.writeBytes("HTTP/1.1 200 OK\r\n");
                    out.writeBytes("Connection: close\r\n");
                    out.writeBytes("Content-Type: " + contentType + "\r\n");
                    out.writeBytes("Content-Length: " + theFile.length() + "\r\n");
                    out.writeBytes("\r\n");

                    BufferedInputStream bin = new BufferedInputStream(new FileInputStream(theFile));
                    byte[] buffer = new byte[1024];
                    int n;
                    while ((n = bin.read(buffer)) > 0) {
                        out.write(buffer, 0, n);
                    }
                    bin.close();
                    out.writeBytes("\r\n");
                    out.flush();
                }
            } else {
                out.writeBytes("HTTP/1.1 404 Not Found\r\n");
                out.writeBytes("Content-Type: text/html\r\n");
                out.writeBytes("\r\n");
                out.writeBytes("<html><body><h1>404 Not Found</h1></body></html>");
                out.flush();
            }
        } catch (IOException e) {
            try {
                DataOutputStream out = new DataOutputStream(new BufferedOutputStream(connection.getOutputStream()));
                out.writeBytes("HTTP/1.1 500 Internal Server Error\r\n");
                out.writeBytes("Content-Type: text/html\r\n");
                out.writeBytes("\r\n");
                out.writeBytes("<html><body><h1>500 Internal Server Error</h1></body></html>");
                out.flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }
}
