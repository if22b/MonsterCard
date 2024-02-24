import server.Unwrapper;
import server.context.RequestContext;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

public class UnwrapperTest {

    @Test
    void unwrap() {
        RequestContext request;
        BufferedReader reader = new BufferedReader(new StringReader(
                "GET /cards HTTP/1.1\r\n" +
                "Host: localhost\r\n" +
                "Key: value\r\n" +
                "Content-Type: application/json\r\n" +
                "Content-Length: 8\r\n" +
                "\r\n" +
                "{id:123}"));

        Map<String, String> result = new HashMap<>();
        result.put("host:","localhost");
        result.put("key:","value");
        result.put("content-type:","application/json");
        result.put("content-length:","8");

        Unwrapper wrapper = new Unwrapper(reader);
        request = wrapper.unwrap();

        assertEquals("GET", request.getHttp_verb());
        assertEquals("/cards", request.getRequested());
        assertEquals("HTTP/1.1", request.getHttp_version());
        assertEquals(result, request.getHeader_values());
        assertEquals("{id:123}", request.getPayload());
    }

    @Test
    void unwrapWithIncompleteHeader() {
        BufferedReader reader = new BufferedReader(new StringReader(
                "GET /cards HTTP/1.1\r\n" +
                        "Host: localhost\r\n" + // Missing other headers
                        "\r\n"));

        Unwrapper wrapper = new Unwrapper(reader);
        RequestContext request = wrapper.unwrap();

        assertNotNull(request, "Request should not be null even with incomplete headers");
        assertEquals("GET", request.getHttp_verb(), "HTTP verb should be GET");
        assertEquals("/cards", request.getRequested(), "Resource should be /cards");
        assertEquals("HTTP/1.1", request.getHttp_version(), "HTTP version should be HTTP/1.1");
    }

    @Test
    void unwrapWithEmptyBody() {
        BufferedReader reader = new BufferedReader(new StringReader(
                "GET /cards HTTP/1.1\r\n" +
                        "Host: localhost\r\n" +
                        "Content-Length: 0\r\n" +
                        "\r\n"));

        Unwrapper wrapper = new Unwrapper(reader);
        RequestContext request = wrapper.unwrap();

        assertNotNull(request, "Request should not be null");
        assertEquals("", request.getPayload(), "Payload should be empty for a GET request with no body");
    }
}