package org.example.backend.server;

import org.example.backend.app.App;
import org.example.backend.http.ContentType;
import org.example.backend.http.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.net.*;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class Server {
    private Request request;
    private Response response;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter outputStream;
    private BufferedReader inputStream;
    private App app;
    private int port;

    public Server(App app, int port) {
        setApp(app);
        setPort(port);
    }

    public void start() throws IOException {
        setServerSocket(new ServerSocket(getPort()));

        run();
    }

    private void run() {
        while (true) {
            try (Socket clientSocket = getServerSocket().accept();
                 PrintWriter outputStream = new PrintWriter(clientSocket.getOutputStream(), true);
                 BufferedReader inputStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                setInputStream(inputStream);
                setRequest(new Request(getInputStream()));
                setOutputStream(outputStream);

                if (getRequest().getPathname() == null) {
                    setResponse(new Response(
                            HttpStatus.BAD_REQUEST,
                            ContentType.TEXT,
                            "Bad Request"
                    ));
                } else {
                    setResponse(getApp().handleRequest(request));
                }

                getOutputStream().write(getResponse().build());
                getOutputStream().flush();
            } catch (IOException e) {
                e.printStackTrace();
                // Consider adding more detailed error handling or logging
            }
        }
    }
}
