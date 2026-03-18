package backend.ms_security.Models;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Permission {
    private String id;
    private String url;
    private String method;
    private String model;

    public Permission() {
    }

    public Permission(String id, String url, String method, String model) {
        this.id = id;
        this.url = url;
        this.method = method;
        this.model = model;
    }
}
