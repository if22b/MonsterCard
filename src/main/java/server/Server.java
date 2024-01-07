package server;

import server.context.RequestContext;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int PORT = 10001;
    private static final int BACKLOG = 5;
    private static final int THREAD_POOL_SIZE = 30;

    public static void main(String[] args) {
        System.out.println("Starting server");
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        try (ServerSocket listener = new ServerSocket(PORT, BACKLOG)) {
            System.out.println("Waiting for clients on port " + PORT);
            System.out.println();

            while (true) {
                Socket socket = listener.accept();
                executor.submit(() -> handleClient(socket));
            }
        } catch (IOException e) {
            System.err.println("Server exception: " + e);
        } finally {
            executor.shutdown();
        }
    }

    private static void handleClient(Socket socket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            RequestContext request;
            Unwrapper wrapper = new Unwrapper(reader);
            request = wrapper.unwrap();

            if (request != null) {
                logRequest(request);
            }

            ResponseHandler responder = new ResponseHandler(writer);
            responder.response(request);

        } catch (IOException e) {
            System.err.println("Error handling client connection: " + e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e);
            }
        }
    }

    private static void logRequest(RequestContext request) {
        System.out.println("** Client - Start **");
        System.out.println("** Header: **");
        System.out.println("    " + request.getHttp_verb() + " " + request.getRequested() + " " + request.getHttp_version());
        for (Map.Entry<String, String> entry : request.getHeader_values().entrySet()) {
            System.out.println("    " + entry.getKey() + " " + entry.getValue());
        }
        System.out.println("** Body: **");
        System.out.println(request.getPayload());
        System.out.println("-------------------------------------------");
    }
}
