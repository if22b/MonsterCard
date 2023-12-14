package bif3.server.context;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

// Store request context
public class RequestContext {
    @Getter
    @Setter
    private String http_verb;
    @Getter
    @Setter
    private String requested;
    @Getter
    @Setter
    private String http_version;
    @Getter
    @Setter
    private Map<String,String> header_values;
    @Getter
    @Setter
    private String payload;

    public RequestContext(){
        header_values = new HashMap<>();
    }

    public void addHeaderValues(String key, String value){
        header_values.put(key,value);
    }

    public int getContentLength(){
        if (header_values != null && header_values.containsKey("content-length:")){
            return Integer.parseInt(header_values.get("content-length:"));
        }
        return 0;
    }
}
