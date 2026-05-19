package backend.ms_security.Controllers;

import java.util.HashMap;

import backend.ms_security.Models.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import backend.ms_security.Services.SecurityService;

@RestController
@CrossOrigin
@RequestMapping("/api/public/security")
public class SecurityController {

    @Autowired
    private SecurityService theSecurityService;

    // REGISTER MANUAL
//    @PostMapping("register")
//    public ResponseEntity<ApiResponse<Session>> register(@RequestBody User newUser) {
//
//        Session session = this.theSecurityService.register(newUser);
//
//        if (session != null) {
//            return ResponseEntity.ok(
//                    ApiResponse.success(session, "Usuario registrado correctamente")
//            );
//        }
//
//        return ResponseEntity.status(HttpStatus.CONFLICT)
//                .body(ApiResponse.error("El correo ya está registrado"));
//    }

    // REGISTER CON ASIGNACION DE ROLE POR DEFECTO
    @PostMapping("register")
    public ResponseEntity<ApiResponse<Session>> registerWithDefaultRole(@RequestBody RegisterRequest registerRequest) {
        ApiResponse<Session> response = this.theSecurityService.register(registerRequest.getUser(), registerRequest.getDefaultRoleId() );
        if (response.isSuccess()){
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }

    }

    // LOGOUT
    @PutMapping("logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody HashMap<String, String> body) {

        boolean success = this.theSecurityService.logout(body.get("id"));

        if (success) {
            return ResponseEntity.ok(
                    ApiResponse.success("Sesión cerrada correctamente")
            );
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("No se encontró la sesión"));
    }

    // LOGIN MANUAL
    @PostMapping("login")
    public ResponseEntity<ApiResponse<Session>> login(@RequestBody HashMap<String, String> body) {

        String email = body.get("email");
        String password = body.get("password");
        String captchaToken = body.get("captchaToken");

        User theNewUser = new User(email, password);

        Session session = this.theSecurityService.login(theNewUser, captchaToken);

        if (session != null) {
            return ResponseEntity.ok(
                    ApiResponse.success(session, "Inicio de sesión exitoso")
            );
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Credenciales inválidas"));
    }

    // LOGIN GOOGLE
    @PostMapping("login/oauth/google")
    public ResponseEntity<ApiResponse<Session>> loginGoogle(
            @RequestBody HashMap<String, String> body) {

        Session session = this.theSecurityService.loginOAuthGoogle(body.get("idToken"));

        if (session != null) {
            return ResponseEntity.ok(
                    ApiResponse.success(session, "Inicio de sesión con Google exitoso")
            );
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("No fue posible autenticar con Google"));
    }

    // LOGIN GITHUB
    @PostMapping("login/oauth/github")
    public ResponseEntity<ApiResponse<Session>> loginGithub(
            @RequestBody HashMap<String, String> body) {

        Session session = this.theSecurityService.loginOAuthGithub(body.get("idToken"));

        if (session != null) {
            return ResponseEntity.ok(
                    ApiResponse.success(session, "Inicio de sesión con GitHub exitoso")
            );
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("No fue posible autenticar con GitHub"));
    }

    // LOGIN MICROSOFT
    @PostMapping("login/oauth/microsoft")
    public ResponseEntity<ApiResponse<Session>> loginMicrosoft(
            @RequestBody HashMap<String, String> body) {

        Session session = this.theSecurityService.loginOAuthMicrosoft(body.get("idToken"));

        if (session != null) {
            return ResponseEntity.ok(
                    ApiResponse.success(session, "Inicio de sesión con Microsoft exitoso")
            );
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("No fue posible autenticar con Microsoft"));
    }

    // RESET PASSWORD
    @PostMapping("reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @RequestBody HashMap<String, String> body) {

        String email = body.get("email");

        this.theSecurityService.resetPassword(email);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Si tu correo está registrado, recibirás las instrucciones en breve."
                )
        );
    }

    // TEMPORAL SESSION
    @PostMapping("get-temporal-session")
    public ResponseEntity<ApiResponse<Session>> getTemporalSession(
            @RequestBody HashMap<String, String> body) {

        String token = body.get("token");

        Session session = this.theSecurityService.getTemporalSession(token);

        if (session != null) {
            return ResponseEntity.ok(
                    ApiResponse.success(session, "Sesión temporal válida")
            );
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Token inválido o expirado"));
    }

    // USER EXIST BY EMAIL
    @GetMapping("user-exist/email/{email}")
    public ResponseEntity<ApiResponse<Boolean>> userExistByEmail(
            @PathVariable String email) {

        boolean exists = this.theSecurityService.existUserByEmail(email);

        return ResponseEntity.ok(
                ApiResponse.success(exists, "Validación completada")
        );
    }

    // VALIDATE 2FA
    @PutMapping("validateCode2FA")
    public ResponseEntity<ApiResponse<Session>> validateCode2FA(
            @RequestBody HashMap<String, String> body) {

        String code2FA = body.get("code2FA");
        String sessionId = body.get("sessionId");

        Session validSession = this.theSecurityService.validatecode2FA(
                code2FA,
                sessionId
        );

        if (validSession != null) {
            return ResponseEntity.ok(
                    ApiResponse.success(validSession, "Código 2FA válido")
            );
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Código 2FA inválido"));
    }

    // PERMISSIONS VALIDATION
    @PostMapping("permissions-validation")
    public ResponseEntity<Boolean> permissionsValidation(
            final HttpServletRequest request,
            @RequestBody Permission permissionData) {

        boolean hasPermission = this.theSecurityService.permissionsValidation(
                request,
                permissionData
        );

        return ResponseEntity.ok(
                hasPermission
        );
    }

    // EXIST USER BY ID
    @GetMapping("{user_id}/exist")
    public ResponseEntity<ApiResponse<Boolean>> existUser(
            @PathVariable String user_id) {

        boolean exists = this.theSecurityService.existUserById(user_id);

        return ResponseEntity.ok(
                ApiResponse.success(exists, "Validación completada")
        );
    }

    @GetMapping("user/email/{email}")
    public ResponseEntity<ApiResponse<User>> findByEmail(@PathVariable String email) {
        ApiResponse<User> response = this.theSecurityService.findUserByEmail(email);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

}