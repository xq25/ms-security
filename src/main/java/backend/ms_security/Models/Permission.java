package backend.ms_security.Models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Permission {

    @Id
    private String id;
    private String url;
    private HttpMethods method;
    private String model;

    public Permission() {}

    public Permission(String id, String url, HttpMethods method, String model) {
        this.id     = id;
        this.url    = url;
        this.method = method;
        this.model  = model;
    }
}

enum HttpMethods {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE");

    private final String value;

    HttpMethods(String value) {
        this.value = value;
    }

    //  Le dice a Jackson cómo serializar el enum → devuelve el string "GET", "POST"...
    @JsonValue
    public String getValue() {
        return value;
    }

    //  Le dice a Jackson cómo deserializar el string → convierte "GET" al enum GET
    @JsonCreator
    public static HttpMethods fromValue(String value) {
        for (HttpMethods m : HttpMethods.values()) {
            if (m.value.equalsIgnoreCase(value)) {
                return m;
            }
        }
        throw new RuntimeException("Método inválido: " + value);
    }
}
