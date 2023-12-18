import org.junit.jupiter.api.Test;
import MonsterCardGame.server.Unwrapper;
import MonsterCardGame.server.context.RequestContext;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnwrapperTest {

    @Test
    void unwrap() {
        RequestContext request;
        BufferedReader reader = new BufferedReader(new StringReader(
                "GET /messages/cards HTTP/1.1\r\n" +
                "Host: localhost\r\n" +
                "Key: value\r\n" +
                "Content-Type: application/json\r\n" +
                "Content-Length: 8\r\n" +
                "\r\n" +
                "{id:123}"));

        Map<String, String> expectedHeaders = new HashMap<>();
        expectedHeaders.put("host", "localhost");
        expectedHeaders.put("key", "value");
        expectedHeaders.put("content-type", "application/json");
        expectedHeaders.put("content-length", "8");

        Unwrapper wrapper = new Unwrapper(reader);
        request = wrapper.unwrap();

        assertEquals("GET", request.getHttp_verb());
        assertEquals("/messages/cards", request.getRequested());
        assertEquals("HTTP/1.1", request.getHttp_version());
        assertEquals(expectedHeaders, request.getHeader_values());
        assertEquals("{id:123}", request.getPayload());
    }
}
