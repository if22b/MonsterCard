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

    public ResponseContext(String status, String contentType){
        http_version = "HTTP/1.1";
        this.status = status;
        server = "mtcg-server";
        this.contentType = contentType;
        contentLength = 0;
        payload = "";
    }

    public void setPayload(String payload){
        this.payload = payload;
        contentLength = payload.length();
    }
}
