package backend.ms_security.Controllers;

import java.io.IOException;
import java.util.HashMap;

import backend.ms_security.Models.Permission;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend.ms_security.Models.Session;
import backend.ms_security.Models.User;
import backend.ms_security.Services.SecurityService;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin
@RequestMapping("/api/public/security")
public class SecurityController {

    @Autowired
    private SecurityService theSecurityService;

    // REGISTER MANUAL
    @PostMapping("register")
    public HashMap<String, Object> register(@RequestBody User newUser, final HttpServletResponse response) throws IOException {
        HashMap<String, Object> theResponse = new HashMap<>();
        Session session = this.theSecurityService.register(newUser);

        if (session != null) {

            theResponse.put("session", session);
        } else {
            // 409 Conflict — el correo ya está registrado
            response.sendError(HttpServletResponse.SC_CONFLICT);
        }
        return theResponse;
    }

    @PutMapping("logout")
    public HashMap<String, Object> logout(@RequestBody HashMap<String, String> body, final HttpServletResponse response) throws IOException {
        HashMap<String, Object> theResponse = new HashMap<>();
        boolean success = this.theSecurityService.logout(body.get("id"));

        if (success) {
            theResponse.put("message", "Sesión cerrada correctamente");
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
        return theResponse;
    }

    // LOGIN MANUAL (con captcha)
    @PostMapping("login")
    public HashMap<String, Object> login(@RequestBody HashMap<String, String> body, final HttpServletResponse response) throws IOException {
        HashMap<String, Object> theResponse = new HashMap<>();

        // Extraemos los campos del body
        String email        = body.get("email");
        String password     = body.get("password");
        String captchaToken = body.get("captchaToken"); // 👈 nuevo campo

        User theNewUser = new User(email, password);
        Session session = this.theSecurityService.login(theNewUser, captchaToken);

        if (session != null) {
            theResponse.put("session",session);
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
        return theResponse;
    }

    // LOGIN CON GOOGLE
    @PostMapping("login/oauth/google")
    public HashMap<String, Object> loginGoogle(@RequestBody HashMap<String, String> body, final HttpServletResponse response) throws IOException {
        HashMap<String, Object> theResponse = new HashMap<>();
        Session session = this.theSecurityService.loginOAuthGoogle(body.get("idToken"));

        if (session != null) {
            theResponse.put("session", session);
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
        return theResponse;
    }

    // LOGIN CON GITHUB
    @PostMapping("login/oauth/github")
    public HashMap<String, Object> loginGithub(@RequestBody HashMap<String, String> body, final HttpServletResponse response) throws IOException {
        HashMap<String, Object> theResponse = new HashMap<>();
        Session session = this.theSecurityService.loginOAuthGithub(body.get("idToken"));

        if (session != null) {
            theResponse.put("session", session);
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
        return theResponse;
    }

    // LOGIN CON MICROSOFT
    @PostMapping("login/oauth/microsoft")
    public HashMap<String, Object> loginMicrosoft(@RequestBody HashMap<String, String> body, final HttpServletResponse response) throws IOException {
        HashMap<String, Object> theResponse = new HashMap<>();
        Session session = this.theSecurityService.loginOAuthMicrosoft(body.get("idToken"));

        if (session != null) {
            theResponse.put("session", session);
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
        return theResponse;
    }

    // HABILITAR PROCESO DE RESTAURACION DE PASSWORD
    @PostMapping("reset-password")
    public HashMap<String, Object> resetPassword(@RequestBody HashMap<String, String> body) {
        HashMap<String, Object> theResponse = new HashMap<>();
        String email = body.get("email");

        this.theSecurityService.resetPassword(email);

        // Siempre mostramos el mismo mensaje — no revelamos si el correo existe o no
        theResponse.put("message", "Si tu correo está registrado, recibirás las instrucciones en breve.");
        return theResponse;
    }

    // HABILITACION TEMPORAL DE SESSSION PARA USUARIO (Restablecer Password)
    @PostMapping("get-temporal-session")
    public HashMap<String, Object> getTemporalSession(@RequestBody HashMap<String, String> body, final HttpServletResponse response) throws IOException {
        HashMap<String, Object> theResponse = new HashMap<>();
        String token = body.get("token");

        Session session = this.theSecurityService.getTemporalSession(token);

        if (session != null) {
            theResponse.put("session", session);
        } else {
            // 401 — token inválido o expirado
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
        return theResponse;
    }

    // VALIDACION DE EXISTENCIA DE USUARIO EN NUESTRA BASE DE DATOS (Mediante email).
    @GetMapping("user-exist/email/{email}")
    public HashMap<String, Object> userExistByEmail(@PathVariable String email) {
        HashMap<String, Object> theResponse = new HashMap<>();
        boolean exists = this.theSecurityService.existUserByEmail(email);
        theResponse.put("exists", exists); // true = Ya existe, false = Correo Disponible
        return theResponse;
    }

    @PutMapping("validateCode2FA")
    public HashMap<String, Object> validateCode2FA(@RequestBody HashMap<String, String> body) {
        HashMap<String, Object> theResponse = new HashMap<>();
        String code2FA = body.get("code2FA");
        String sessionId = body.get("sessionId");
        boolean isValid = false;

         Session validSession = this.theSecurityService.validatecode2FA(code2FA, sessionId);

         if (validSession != null){
             isValid = true;
             theResponse.put("session", validSession);

         }
         theResponse.put("isValid", isValid);


        return theResponse;
    }

    @PostMapping("permissions-validation")
    public boolean permissionsValidation(final HttpServletRequest request, @RequestBody Permission permissionData){
        return this.theSecurityService.permissionsValidation(request, permissionData);
    }

}