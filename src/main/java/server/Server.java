
package server;

import server.context.RequestContext;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

public class Server {
    public static void main(String[] args) {
        System.out.println("start server...");
        try {
            ServerSocket listener = new ServerSocket(10001, 5);
            System.out.println("Waiting for clients...");
            System.out.println();
            while (true) {
                Socket socket = listener.accept();
                //System.out.println("** New client arrived: **");
                Thread thread = new Thread(() -> {
                    // Try reader and writer
                    try ( BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream())); BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
                        // Request data
                        RequestContext request;
                        // Unwrap
                        Unwrapper wrapper = new Unwrapper(reader);
                        request = wrapper.unwarp();
                        // Print Request
                        if (request != null){

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
                    //System.out.println("** Client gone. **");
                    //System.out.println("");
                });
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/*


package bif3.swe1.server;

import context.server.RequestContext;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

public class Server {
    public static void main(String[] args) {
        System.out.println("Starting server...");

        try (ServerSocket listener = new ServerSocket(10001)) {
            System.out.println("Waiting for clients...");

            while (true) {
                try (Socket socket = listener.accept();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

                    RequestContext request = processRequest(reader);
                    if (request != null) {
                        printRequest(request);
                    }

                    ResponseHandler responder = new ResponseHandler(writer);
                    responder.response(request);

                } catch (IOException e) {
                    System.err.println("Error handling client connection: " + e);
                }
            }
        } catch (IOException e) {
            System.err.println("Server exception: " + e);
        }
    }

    private static RequestContext processRequest(BufferedReader reader) throws IOException {
        Unwrapper wrapper = new Unwrapper(reader);
        return wrapper.unwarp();
    }

    private static void printRequest(RequestContext request) {
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
*/