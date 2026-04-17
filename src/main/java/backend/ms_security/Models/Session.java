package backend.ms_security.Models;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document
public class Session {
    @Id
    private String id;
    private String token;
    private Date expiration;
    private String code2FA;
    private boolean active;

    @DBRef
    private User user;

    public Session(){

    }

    public Session(String id){
        this.id = id;
        this.token = "NOT DEFINED";
        this.expiration = null;
        this.code2FA = "ON VALIDATION";
        this.active = false;
        this.user = null;
    }

    public Session(String id, String token, Date expiration, String code2FA, User user) {
        this.id = id;
        this.token = token;
        this.expiration = expiration;
        this.code2FA = code2FA;
        this.active = true;
        this.user = user;
    }

    public Session(String token, Date expiration) {
        this.token = token;
        this.expiration = expiration;
        this.code2FA = "NOT-FOUND";
        this.active = true;
    }

    public Session(String token, Date expiration, String code2FA) {
        this.token = token;
        this.expiration = expiration;
        this.code2FA = code2FA;
        this.active = false;
    }
}
