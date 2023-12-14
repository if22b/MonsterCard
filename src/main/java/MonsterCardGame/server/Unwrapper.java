/*
package MonsterCard.server;

import java.io.BufferedReader;
import java.io.IOException;

import MonsterCard.server.context.RequestContext;

// unwrap request and saves in RequestContext
public class Unwrapper {

    BufferedReader reader;

    public Unwrapper(BufferedReader reader){
        this.reader = reader;
    }

    public RequestContext unwarp() {
        RequestContext request;
        try {
            // header
            request = readHttpHeader(reader);
            // body
            if (request != null){
                int contentLength = request.getContentLength();
                request.setPayload(readHttpBody(reader,contentLength));
                return request;
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private RequestContext readHttpHeader(BufferedReader reader) throws IOException {
        // init
        RequestContext request = new RequestContext();
        String message;
        // HTTP-Verb, resource requested, the http-version
        message = reader.readLine();
        if (message == null){
            return null;
        }
        String[] parts = message.split(" ");
        if (parts.length == 3){
            request.setHttp_verb(parts[0]);
            request.setRequested(parts[1]);
            request.setHttp_version(parts[2]);
        } else {
            return null;
        }
        // Further header values
        while ( (message = reader.readLine()) != null ) {
            if (message.isBlank() )
                break;
            parts = message.split(" ",2);
            if (parts.length == 2) {
                request.addHeaderValues(parts[0].toLowerCase(),parts[1]);
            }
        }
        return request;
    }

    private String readHttpBody(BufferedReader reader, int contentLength) throws IOException {
        StringBuilder sb = new StringBuilder(10000);
        int count = 0;
        int input;
        while (reader.ready() && (input = reader.read()) != -1) {
            sb.append((char)input);
            count ++;
            if( count >= contentLength )
                break;
        }
        return sb.toString();
    }
}

*/

package MonsterCardGame.server;

import java.io.BufferedReader;
import java.io.IOException;

import MonsterCardGame.server.context.RequestContext;

// Unwrap request and save it in RequestContext
public class Unwrapper {

    private final BufferedReader reader;

    public Unwrapper(BufferedReader reader) {
        this.reader = reader;
    }

    public RequestContext unwrap() {
        try {
            // Read HTTP header
            RequestContext request = readHttpHeader();
            
            // Read HTTP body
            if (request != null) {
                int contentLength = request.getContentLength();
                request.setPayload(readHttpBody(contentLength));
                return request;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private RequestContext readHttpHeader() throws IOException {
        RequestContext request = new RequestContext();
        String message = reader.readLine();
        
        if (message == null) {
            return null;
        }

        String[] parts = message.split(" ");
        
        if (parts.length != 3) {
            return null;
        }

        request.setHttp_verb(parts[0]);
        request.setRequested(parts[1]);
        request.setHttp_version(parts[2]);
        
        // Read further header values
        while ((message = reader.readLine()) != null) {
            if (message.isBlank()) {
                break;
            }
            parts = message.split(" ", 2);
            if (parts.length == 2) {
                request.addHeaderValues(parts[0].toLowerCase(), parts[1]);
            }
        }
        
        return request;
    }

    private String readHttpBody(int contentLength) throws IOException {
        StringBuilder sb = new StringBuilder(10000);
        int count = 0;
        int input;
        while (reader.ready() && (input = reader.read()) != -1) {
            sb.append((char) input);
            count++;
            if (count >= contentLength) {
                break;
            }
        }
        return sb.toString();
    }
}
