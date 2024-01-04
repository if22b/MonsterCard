package server.context;
import lombok.Getter;
import lombok.Setter;

// Store request context
public class ResponseContext {

    @Getter
    private String http_version;
    @Getter
    @Setter
    private String status;
    @Getter
    private String server;
    @Getter
    @Setter
    private String contentType;
    @Getter
    private int contentLength;
    @Getter
    private String payload;

    public ResponseContext(String status){
        http_version = "HTTP/1.1";
        this.status = status;
        server = "mtcg-server";
        contentType = "application/json";
        contentLength = 0;
        payload = "";
    }

    public void setPayload(String payload){
        this.payload = payload;
        contentLength = payload.length();
    }
}
