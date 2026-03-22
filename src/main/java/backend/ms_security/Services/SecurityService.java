package backend.ms_security.Services;

import backend.ms_security.Models.Profile;
import backend.ms_security.Models.Session;
import backend.ms_security.Models.User;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
public class SecurityService {

    @Autowired
    private UserService theUserService;

    @Autowired
    private EncryptionService theEncryptionService;

    @Autowired
    private JwtService theJwtService;

    @Autowired
    private SessionService theSessionService;

    @Autowired
    private FirebaseAuthService theFirebaseAuthService;

    @Autowired
    private CaptchaService theCaptchaService;

    @Autowired
    private ProfileService theProfileService;

    // Manual Register
    public Session register(User newUser){
        User theActualUser = this.theUserService.findByEmail(newUser.getEmail());

        // Validamos que no exista un usuario con el mismo correo
        if (theActualUser == null){
            // Generamos el usuario con los credenciales generados.
            theActualUser = this.theUserService.create(newUser);
            // Generamos un perfil vacio (por defecto)
            Profile newProfile = this.theProfileService.create(new Profile());

            this.theUserService.addProfile(theActualUser.getId(), newProfile.getId());


            Date expiryDate = theJwtService.getExpirationDate();
            String token    = theJwtService.generateToken(theActualUser);

            Session emptySession = theSessionService.create(new Session(token, expiryDate));
            theUserService.addSession(theActualUser.getId(), emptySession.getId());

            return theSessionService.findById(emptySession.getId());

        }
        return null;
    }

    // LOGIN MANUAL
    public Session login(User theNewUser, String captchaToken) {
        String token = null;
        Date expiryDate = null;
        Session actualSession = null;

        // 1. Validar captcha antes de cualquier cosa
        if (!theCaptchaService.validate(captchaToken)) {
            System.out.println("❌ Captcha inválido o score bajo");
            return null;
        }

        User theActualUser = this.theUserService.findByEmail(theNewUser.getEmail());
        if (theActualUser != null &&
                theActualUser.getPassword().equals(theEncryptionService.convertSHA256(theNewUser.getPassword()))) {
            expiryDate = theJwtService.getExpirationDate();
            token = theJwtService.generateToken(theActualUser);

            Session emptySession = theSessionService.create(new Session(token, expiryDate));
            theUserService.addSession(theActualUser.getId(), emptySession.getId());
            actualSession = theSessionService.findById(emptySession.getId());

            return actualSession;
        } else {
            return actualSession;
        }
    }

    private Session loginOAuth(String idToken) {
        // 1. Verificar el token con Firebase Admin SDK
        FirebaseToken firebaseToken = theFirebaseAuthService.verifyToken(idToken);
        if (firebaseToken == null) return null;

        // 2. Extraer datos del usuario desde el token
        String email = firebaseToken.getEmail();
        String name  = firebaseToken.getName();

        // 3. Buscar si el usuario ya existe, si no, crearlo
        User theActualUser = theUserService.findByEmail(email);

        if (theActualUser == null) {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name != null ? name : email); // GitHub a veces no devuelve nombre
            newUser.setPassword("OAUTH_USER");
            theActualUser = theUserService.create(newUser);

            Profile newProfile = this.theProfileService.create(new Profile());

            this.theUserService.addProfile(theActualUser.getId(), newProfile.getId());
        }
        // 4. Generar sesión
        Date expiryDate = theJwtService.getExpirationDate();
        String token    = theJwtService.generateToken(theActualUser);

        Session emptySession = theSessionService.create(new Session(token, expiryDate));
        theUserService.addSession(theActualUser.getId(), emptySession.getId());

        return theSessionService.findById(emptySession.getId());
    }

    // LOGIN CON GOOGLE
    public Session loginOAuthGoogle(String idToken) {
        return this.loginOAuth(idToken);
    }

    // LOGIN CON GITHUB
    public Session loginOAuthGithub(String idToken) {
        return this.loginOAuth(idToken);
    }

    // LOGIN CON MICROSOFT
    public Session loginOAuthMicrosoft(String idToken) {
        return this.loginOAuth(idToken);
    }

    public boolean existUserByEmail(String email){
        User theUserValidation = this.theUserService.findByEmail(email);
        boolean validaiton = false;

        if (theUserValidation == null){
            validaiton = true;
        }
        return validaiton;
    }


}