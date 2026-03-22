package backend.ms_security.Controllers;

import backend.ms_security.Models.Session;
import backend.ms_security.Models.User;
import backend.ms_security.Services.SecurityService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;

@RestController
@CrossOrigin
@RequestMapping("/api/public/security")
public class SecurityController {

    @Autowired
    private SecurityService theSecurityService;

    // Register Manual
    @PostMapping("register")
    public HashMap<String, Object> register(@RequestBody User newUser,
                                            final HttpServletResponse response) throws IOException {
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

    // LOGIN MANUAL (con captcha)
    @PostMapping("login")
    public HashMap<String, Object> login(@RequestBody HashMap<String, String> body,
                                         final HttpServletResponse response) throws IOException {
        HashMap<String, Object> theResponse = new HashMap<>();

        // Extraemos los campos del body
        String email        = body.get("email");
        String password     = body.get("password");
        String captchaToken = body.get("captchaToken"); // 👈 nuevo campo

        User theNewUser = new User(email, password);
        Session session = this.theSecurityService.login(theNewUser, captchaToken);

        if (session != null) {
            theResponse.put("session", session);
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
        return theResponse;
    }

    // LOGIN CON GOOGLE
    @PostMapping("login/oauth/google")
    public HashMap<String, Object> loginGoogle(@RequestBody HashMap<String, String> body,
                                               final HttpServletResponse response) throws IOException {
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


    // LOGIN CON MICROSOFT
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

    @GetMapping("user-exist/email/{email}")
    public HashMap<String, Object> userExistByEmail(@PathVariable String email) {
        HashMap<String, Object> theResponse = new HashMap<>();
        boolean available = this.theSecurityService.existUserByEmail(email);
        theResponse.put("available", available); // true = correo disponible, false = ya existe
        return theResponse;
    }

}