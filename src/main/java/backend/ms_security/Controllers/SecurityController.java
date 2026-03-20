package backend.ms_security.Controllers;

import backend.ms_security.Models.Session;
import backend.ms_security.Models.User;
import backend.ms_security.Services.SecurityService;
import backend.ms_security.Services.SessionService;
import backend.ms_security.Services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;

@RestController
@CrossOrigin
@RequestMapping("/api/public/security")

public class SecurityController {
    @Autowired
    private SecurityService theSecurityService;

    @Autowired
    private SessionService theSessionService;

    @Autowired
    private UserService theUserService;

    // Aqui definimos que lo que nos va a devolver el login es el token del usuario logueado
    @PostMapping("login")
    public HashMap<String,Object> login(@RequestBody User theNewUser,
                                        final HttpServletResponse response)throws IOException {
        HashMap<String, Object> theResponse = new HashMap<>();
        Session session = this.theSecurityService.login(theNewUser);

        if (session != null) {

            theResponse.put("session", session);
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return theResponse;
        }
        return theResponse;
    }
    @PostMapping("login//google")
    public HashMap<String, Object> loginOAuth(@RequestBody HashMap<String, String> body,
                                              final HttpServletResponse response) throws IOException {
        HashMap<String, Object> theResponse = new HashMap<>();

        String idToken = body.get("idToken"); // El token que viene del frontend Firebase
        Session session = this.theSecurityService.loginOAuthGoogle(idToken);

        if (session != null) {
            theResponse.put("session", session);
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }

        return theResponse;
    }
    // =========================
    // LOGIN CON GITHUB
    // =========================
    @PostMapping("login/oauth/github")
    public HashMap<String, Object> loginGithub(@RequestBody HashMap<String, String> body,
                                               final HttpServletResponse response) throws IOException {
        HashMap<String, Object> theResponse = new HashMap<>();
        Session session = this.theSecurityService.loginOAuthGithub(body.get("idToken"));

        if (session != null) {
            theResponse.put("session", session);
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
        return theResponse;
    }

    // =========================
    // LOGIN CON MICROSOFT
    // =========================
    @PostMapping("login/oauth/microsoft")
    public HashMap<String, Object> loginMicrosoft(@RequestBody HashMap<String, String> body,
                                                  final HttpServletResponse response) throws IOException {
        HashMap<String, Object> theResponse = new HashMap<>();
        Session session = this.theSecurityService.loginOAuthMicrosoft(body.get("idToken"));

        if (session != null) {
            theResponse.put("session", session);
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
        return theResponse;
    }

}
