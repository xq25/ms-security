package backend.ms_security.Controllers;

import backend.ms_security.Models.ApiResponse;
import backend.ms_security.Models.User;
import backend.ms_security.Models.UserRole;
import backend.ms_security.Services.GetwaySecurity.SecurityGetwayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("getway/security/api")
public class SecurityGetwayController {

    @Autowired
    private SecurityGetwayService theSecurityGetwayService;

    @Value("${ms.secret.key}")
    private String SECRET_KEY_ACCESS;

    private boolean isInvalidSecret(String secretKey) {
        return secretKey == null || !secretKey.equals(SECRET_KEY_ACCESS);
    }

    @GetMapping("user/email/{email}")
    public ResponseEntity<ApiResponse<User>> findByEmail(@PathVariable String email, @RequestHeader(value = "secretKey", required = false) String secretKey) {

        if (isInvalidSecret(secretKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid secret key"));
        }

        ApiResponse<User> response = this.theSecurityGetwayService.findUserByEmail(email);

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // USER EXIST BY EMAIL
    @GetMapping("user-exist/email/{email}")
    public ResponseEntity<ApiResponse<Boolean>> userExistByEmail(@PathVariable String email, @RequestHeader(value = "secretKey", required = false) String secretKey) {

        if (isInvalidSecret(secretKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid secret key"));
        }

        boolean exists = this.theSecurityGetwayService.existUserByEmail(email);

        return ResponseEntity.ok(
                ApiResponse.success(exists, "Validación completada")
        );
    }

    // EXIST USER BY ID
    @GetMapping("{user_id}/exist")
    public ResponseEntity<ApiResponse<Boolean>> existUser(@PathVariable String user_id, @RequestHeader(value = "secretKey", required = false) String secretKey) {

        if (isInvalidSecret(secretKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid secret key"));
        }

        boolean exists = this.theSecurityGetwayService.existUserById(user_id);

        return ResponseEntity.ok(
                ApiResponse.success(exists, "Validación completada")
        );
    }

    // ASSING DEFAULT ROLE, SOLO LOS CONTEMPLADOS DENTRO DE ESTE SISTEMA
    @PostMapping("{userId}/assign-role/{defaultRoleKey}")
    public ResponseEntity<ApiResponse<UserRole>> assingDefaultRole(@PathVariable String userId, @PathVariable String defaultRoleKey){
        ApiResponse<UserRole> response = this.theSecurityGetwayService.assignDefaultRole(userId, defaultRoleKey);

        System.out.println(defaultRoleKey);
        System.out.println("Respuesta del controlador: " + response);
        if(response.isSuccess()){
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

}
