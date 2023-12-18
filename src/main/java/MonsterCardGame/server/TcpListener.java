/*
package MonsterCard.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

import MonsterCard.server.context.RequestContext;

public class TcpListener {
    public static void main(String[] args) {
        System.out.println("start server...");
        try (ServerSocket listener = new ServerSocket(10001, 5)) {
            System.out.println("Waiting for clients...");
            System.out.println();
            while (true) {
                Socket socket = listener.accept();
                Thread thread = new Thread(() -> {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                         BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
                        // Request data
                        RequestContext request;
                        // Unwrap
                        Unwrapper wrapper = new Unwrapper(reader);
                        request = wrapper.unwarp();
                        // Print Request
                        if (request != null) {
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
                        // Handle response
                        ResponseHandler responder = new ResponseHandler(writer);
                        responder.response(request);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

*/

package MonsterCardGame.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.concurrent.*;

import MonsterCardGame.server.context.RequestContext;


public class TcpListener {

    private static final int TIMEOUT = 2500; // 2.5 seconds in milliseconds
    
    public static void main(String[] args) {
        System.out.println("Start server...");
        try (ServerSocket listener = new ServerSocket(10001, 5)) {
            System.out.println("Waiting for clients...");
            System.out.println();
            while (true) {
                Socket socket = listener.accept();
                Thread thread = new Thread(() -> handleClient(socket));
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket socket) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(() -> {

        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))
            ) {
                socket.setSoTimeout(TIMEOUT); // Set a timeout for read() operations
                RequestContext request;
            
            // Unwrap
            Unwrapper wrapper = new Unwrapper(reader);
            request = wrapper.unwrap();
            
            // Print Request
            if (request != null) {
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
            
            // Handle response
            ResponseHandler responder = new ResponseHandler(writer);
            responder.response(request);
        
        } catch (SocketTimeoutException e) {
            System.err.println("Request timed out.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    });

    executor.shutdown(); // Disallow new tasks
        try {
            future.get(TIMEOUT, TimeUnit.MILLISECONDS); // Wait for the task to complete or timeout
        } catch (TimeoutException e) {
            System.err.println("Terminating the request processing due to timeout.");
            future.cancel(true); // Interrupt the task thread if it's running
        } catch (ExecutionException | InterruptedException e) {
            // Handle other exceptions that may occur during execution
            e.printStackTrace();
        }
    }
}
