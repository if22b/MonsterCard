import mtcg.User;
import mtcg.managers.CardManager;
import mtcg.managers.UserManager;
import static org.mockito.Mockito.times;
import server.ResponseHandler;
import server.context.RequestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import server.context.ResponseContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ResponseHandlerTest {

    @Mock
    UserManager userManager;
    @Mock
    CardManager cardManager;
    @Mock
    User user;
    @Mock
    BufferedWriter writer;

    RequestContext request;

    @BeforeEach
    void setUp() {
        request = new RequestContext();
        request.setHttp_version("HTTP/1.1");
        Map<String, String> map = new HashMap<>();
        map.put("user-agent:", "PostmanRuntime/7.26.8");
        map.put("content-length:", "0");
        map.put("content-type:", "application/json");
        map.put("accept:", "*/*");
        map.put("host:", "localhost:10001");
        map.put("authorization:", "test");
        request.setHeader_values(map);
        request.setPayload("{\"Username\":\"Test\", \"Password\":\"test\"}");
    }

    @Test
    public void registerTest() throws IOException {
        try (MockedStatic<UserManager> mb = Mockito.mockStatic(UserManager.class)) {
            mb.when(UserManager::getInstance)
                    .thenReturn(userManager);

            request.setHttp_verb("POST");
            request.setRequested("/users");
            ResponseHandler handler = new ResponseHandler(writer);
            handler.response(request);
            verify(userManager).registerUser(anyString(), anyString());
            verify(writer).flush();
        }
    }

    @Test
    public void loginTest() throws IOException {
        try (MockedStatic<UserManager> mb = Mockito.mockStatic(UserManager.class)) {
            mb.when(UserManager::getInstance)
                    .thenReturn(userManager);

            when(userManager.authorizeUser(anyString())).thenReturn(user);
            when(user.getUsername()).thenReturn("kienboec");
            request.setHttp_verb("PUT");
            request.setRequested("/users/kienboec");
            request.setPayload("{\"Name\": \"Kienboeck\",  \"Bio\": \"me playin...\", \"Image\": \":-)\"}");
            ResponseHandler handler = new ResponseHandler(writer);
            handler.response(request);
            verify(user).setUserInfo(anyString(), anyString(), anyString());
            verify(writer).flush();
        }
    }

    @Test
    public void loginTestFailWrongUser() throws IOException {
        // Instantiate a MockedStatic in a try-with-resources block
        try (MockedStatic<UserManager> mb = Mockito.mockStatic(UserManager.class)) {
            mb.when(UserManager::getInstance)
                    .thenReturn(userManager);

            when(userManager.authorizeUser(anyString())).thenReturn(user);
            when(user.getUsername()).thenReturn("altenhof");
            request.setHttp_verb("PUT");
            request.setRequested("/users/kienboec");
            request.setPayload("{\"Name\": \"Kienboeck\",  \"Bio\": \"me playin...\", \"Image\": \":-)\"}");
            ResponseHandler handler = new ResponseHandler(writer);
            handler.response(request);
            verify(user, times(0)).setUserInfo(anyString(), anyString(), anyString());
            verify(writer).flush();
        }
    }

    @Test
    public void createPackageTest() throws IOException {
        try (MockedStatic<UserManager> mb0 = Mockito.mockStatic(UserManager.class)) {
            mb0.when(UserManager::getInstance)
                    .thenReturn(userManager);

            try (MockedStatic<CardManager> mb = Mockito.mockStatic(CardManager.class)) {
                mb.when(CardManager::getInstance)
                        .thenReturn(cardManager);

                when(userManager.isAdmin(anyString())).thenReturn(true);
                when(cardManager.registerCard(anyString(), anyString(), anyFloat())).thenReturn(true);
                when(cardManager.createPackage(anyList())).thenReturn(true);
                request.setHttp_verb("POST");
                request.setRequested("/packages");
                request.setPayload("[{\"Id\":\"b017ee50-1c14-44e2-bfd6-2c0c5653a37c\", \"Name\":\"WaterGoblin\", \"Damage\": 11.0}, {\"Id\":\"d04b736a-e874-4137-b191-638e0ff3b4e7\", \"Name\":\"Dragon\", \"Damage\": 70.0}, {\"Id\":\"88221cfe-1f84-41b9-8152-8e36c6a354de\", \"Name\":\"WaterSpell\", \"Damage\": 22.0}, {\"Id\":\"1d3f175b-c067-4359-989d-96562bfa382c\", \"Name\":\"Ork\", \"Damage\": 40.0}, {\"Id\":\"171f6076-4eb5-4a7d-b3f2-2d650cc3d237\", \"Name\":\"RegularSpell\", \"Damage\": 28.0}]");
                ResponseHandler handler = new ResponseHandler(writer);
                handler.response(request);
                verify(cardManager, times(5)).registerCard(anyString(), anyString(), anyFloat());
                verify(cardManager).createPackage(anyList());
                verify(writer).flush();
            }
        }
    }

    @Test
    public void testInvalidJsonPayload() throws IOException {
        request.setHttp_verb("POST");
        request.setRequested("/json-endpoint");
        request.setPayload("{invalid json}");

        ResponseHandler handler = new ResponseHandler(writer);
        handler.response(request);

        verify(writer).write(contains("400 Bad Request"));
        verify(writer).flush();
    }
}