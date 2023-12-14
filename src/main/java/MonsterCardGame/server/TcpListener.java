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
import java.util.Map;

import MonsterCardGame.server.context.RequestContext;

public class TcpListener {
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
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))
        ) {
            // Request data
            RequestContext request;
            
            // Unwrap
            Unwrapper wrapper = new Unwrapper(reader);
            request = wrapper.unwrap(); // Corrected method name
            
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
    }
}
