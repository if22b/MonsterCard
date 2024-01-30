package server;

import server.context.RequestContext;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class Server {
    private static final int PORT = 10001;
    private static final int BACKLOG = 5;
    private static final int THREAD_POOL_SIZE = 20;
    private static final Logger logger = Logger.getLogger(Server.class.getName());

    public static void main(String[] args) {
        logger.info("Starting server");
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        try (ServerSocket listener = new ServerSocket(PORT, BACKLOG)) {
            logger.info("Waiting for clients on port " + PORT);

            while (true) {
                Socket socket = listener.accept();
                logger.info("Client connected: " + socket);
                executor.submit(() -> handleClient(socket));
            }
        } catch (IOException e) {
            logger.severe("Server exception: " + e.getMessage());
        } finally {
            executor.shutdown();
            logger.info("Server shutting down");
        }
    }

    private static void handleClient(Socket socket) {
        logger.info("Handling client: " + socket);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            Unwrapper wrapper = new Unwrapper(reader);
            RequestContext request = wrapper.unwrap();
            if (request != null) {
                logRequest(request);
                ResponseHandler responder = new ResponseHandler(writer);
                responder.response(request);
            } else {
                logger.warning("Request context is null after unwrapping.");
            }

        } catch (IOException e) {
            logger.severe("Error handling client connection: " + e.getMessage());
        } finally {
            try {
                socket.close();
                logger.info("Client socket closed: " + socket);
            } catch (IOException e) {
                logger.severe("Error closing client socket: " + e.getMessage());
            }
        }
    }

    private static void logRequest(RequestContext request) {
        System.out.println("** Client - Start **");
        System.out.println("** Header: **");
        System.out.println("    " + request.getHttp_verb() + " " + request.getRequested() + " " + request.getHttp_version());
        for (Map.Entry<String, String> entry : request.getHeader_values().entrySet()) {
            System.out.println("    " + entry.getKey() + ": " + entry.getValue());
        }
        System.out.println("** Body: **");
        System.out.println(request.getPayload());
        System.out.println("-------------------------------------------");
    }
}
