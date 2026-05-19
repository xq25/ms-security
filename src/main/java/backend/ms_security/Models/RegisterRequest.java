package backend.ms_security.Models;

import lombok.Data;

@Data
public class RegisterRequest {
    private User user;
    private String defaultRoleId;
}
