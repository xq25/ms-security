package backend.ms_security.Services;

import java.util.Date;

import backend.ms_security.Models.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.firebase.auth.FirebaseToken;


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

    @Autowired
    private NotificationService theNotificationService;

    @Autowired
    private ValidatorsService theValidatorService;

    @Autowired
    private UserRoleService theUserRoleService;

    @Autowired
    private RoleService theRoleService;

    @Value("${rest.expiration}")
    private Long RESET_TOKEN_EXPIRATION;

//    // Manual Register
//    public Session register(User newUser) {
//        User theActualUser = this.theUserService.findByEmail(newUser.getEmail()).getData();
//
//        if (theActualUser == null) {
//            theActualUser = this.theUserService.create(newUser).getData();
//
//            Profile newProfile = this.theProfileService.create(new Profile()).getData();
//            this.theUserService.addProfile(theActualUser.getId(), newProfile.getId());
//
//            String token    = theJwtService.generateToken(theActualUser);
//            Date expiryDate = theJwtService.getExpirationFromToken(token);
//
//            Session emptySession = theSessionService.create(new Session(token, expiryDate)).getData();
//            theUserService.addSession(theActualUser.getId(), emptySession.getId());
//
//            String body = "Hola " + newUser.getName() + ",\n\n"
//                    + "Gracias por registrarte en nuestra plataforma.\n"
//                    + "El equipo de MS Security";
//            theNotificationService.sendEmail(newUser.getEmail(), body);
//
//            return theSessionService.findById(emptySession.getId()).getData();
//        }
//        return null;
//    }

    // Register With DefaultRole
    public ApiResponse<Session> register(User newUser, String defaultRoleId) {
        User theActualUser = this.theUserService.findByEmail(newUser.getEmail()).getData();

        // Validamos que el rol por defecto sea válido
        Role role = this.theRoleService.findById(defaultRoleId).getData();
        if (role == null) {
            return ApiResponse.error("No se encontró el rol por defecto con ID: " + defaultRoleId);
        }

        // Usuario no existente en el sistema
        if (theActualUser == null) {
            theActualUser = this.theUserService.create(newUser).getData();

            // Creamos y asignamos un perfil por defecto al usuario
            Profile newProfile = this.theProfileService.create(new Profile()).getData();
            this.theUserService.addProfile(theActualUser.getId(), newProfile.getId());

            String body = "Hola " + newUser.getName() + ",\n\n"
                    + "Gracias por registrarte en nuestra plataforma.\n"
                    + "El equipo de MS Security";
            theNotificationService.sendEmail(newUser.getEmail(), body);
        }

        String token    = theJwtService.generateToken(theActualUser);
        Date expiryDate = theJwtService.getExpirationFromToken(token);

        Session emptySession = theSessionService.create(new Session(token, expiryDate)).getData();
        theUserService.addSession(theActualUser.getId(), emptySession.getId());
        Session theActualSession = theSessionService.findById(emptySession.getId()).getData();

        //Asignamos el rol por defecto con el que se esta haciendo el registro
        ApiResponse<UserRole> responseUserRole = this.theUserRoleService.addUserRole(theActualUser.getId(), defaultRoleId);

        // Si algo en el proceso falla -> Unicamente podria fallar que este usuario ya posea ese role. Devolvemos la session sin la asignacion del role
        if (!responseUserRole.isSuccess()){
            return ApiResponse.success(theActualSession, "Usuario registrado correctamente pero no se pudo asignar el rol por defecto: " + responseUserRole.getMessage());
        }

        return ApiResponse.success(theActualSession, "Usuario registrado correctamente con role por defecto");
    }

    // LOGOUT
    public boolean logout(String current_session_id) {
        return this.theSessionService.invalidateSession(current_session_id);
    }

    // LOGIN MANUAL
    public Session login(User theNewUser, String captchaToken) {
        String token = null;
        Date expiryDate = null;
        Session actualSession = null;
        String code2FA = null;
        Session session2FA = null;

        if (!theCaptchaService.validate(captchaToken)) {
            System.out.println("❌ Captcha inválido o score bajo");
            return null;
        }

        User theActualUser = this.theUserService.findByEmail(theNewUser.getEmail()).getData();
        if (theActualUser != null &&
                theActualUser.getPassword().equals(theEncryptionService.convertSHA256(theNewUser.getPassword()))) {

            token      = theJwtService.generateToken(theActualUser);
            expiryDate = theJwtService.getExpirationFromToken(token);
            code2FA    = theJwtService.generateCode2FA();

            Session emptySession = theSessionService.create(new Session(token, expiryDate, code2FA)).getData();

            theUserService.addSession(theActualUser.getId(), emptySession.getId());
            actualSession = theSessionService.findById(emptySession.getId()).getData();

            session2FA = new Session(actualSession.getId());

            String body = "Hola " + theActualUser.getName() + ",\n\n"
                    + "Tu código de autenticación de dos factores es: " + code2FA + "\n"
                    + "⚠️ Este código expira en 5 minutos.\n\n"
                    + "Si no solicitaste esto, ignora este mensaje.\n\n"
                    + "El equipo de MS Security";
            theNotificationService.sendEmail(theActualUser.getEmail(), body);

            return session2FA;
        } else {
            return session2FA;
        }
    }

    // FUNCION GENERICA DE AUTENTICACION CON TERCEROS
    private Session loginOAuth(String idToken) {
        FirebaseToken firebaseToken = theFirebaseAuthService.verifyToken(idToken);
        if (firebaseToken == null) return null;

        String email = firebaseToken.getEmail();
        String name  = firebaseToken.getName();

        User theActualUser = theUserService.findByEmail(email).getData();

        if (theActualUser == null) {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name != null ? name : email);
            newUser.setPassword("OAUTH_USER");
            theActualUser = theUserService.create(newUser).getData();

            Profile newProfile = this.theProfileService.create(new Profile()).getData();
            this.theUserService.addProfile(theActualUser.getId(), newProfile.getId());
        }

        String token    = theJwtService.generateToken(theActualUser);
        Date expiryDate = theJwtService.getExpirationFromToken(token);

        Session emptySession = theSessionService.create(new Session(token, expiryDate)).getData();
        theUserService.addSession(theActualUser.getId(), emptySession.getId());

        return theSessionService.findById(emptySession.getId()).getData();
    }

    public Session loginOAuthGoogle(String idToken) {
        return this.loginOAuth(idToken);
    }

    public Session loginOAuthGithub(String idToken) {
        return this.loginOAuth(idToken);
    }

    public Session loginOAuthMicrosoft(String idToken) {
        return this.loginOAuth(idToken);
    }

    public boolean resetPassword(String email) {
        User theUser = this.theUserService.findByEmail(email).getData();

        if (theUser != null) {
            String resetToken = theJwtService.generateTemporalToken(theUser, RESET_TOKEN_EXPIRATION);
            String resetLink  = "http://localhost:4200/reset-password?token=" + resetToken;

            String body = "Hola " + theUser.getName() + ",\n\n"
                    + "Recibimos una solicitud para restablecer tu contraseña.\n"
                    + "Haz clic en el siguiente enlace para continuar:\n\n"
                    + resetLink + "\n\n"
                    + "⚠️ Este enlace expira en 5 minutos.\n\n"
                    + "Si no solicitaste esto, ignora este mensaje.\n\n"
                    + "El equipo de MS Security";

            theNotificationService.sendEmail(email, body);
        }
        return true;
    }

    public Session getTemporalSession(String token) {
        User theTemporalUser = this.theJwtService.getUserFromToken(token);
        if (theTemporalUser == null) return null;

        Date expiryDate = this.theJwtService.getExpirationFromToken(token);

        if (this.theSessionService.isTokenAlreadyUsed(token)) {
            System.out.println("❌ Token ya utilizado, acceso denegado");
            return null;
        }

        Session emptySession = theSessionService.create(new Session(token, expiryDate)).getData();
        theUserService.addSession(theTemporalUser.getId(), emptySession.getId());

        return theSessionService.findById(emptySession.getId()).getData();
    }

    public boolean existUserByEmail(String email) {
        User theUserValidation = this.theUserService.findByEmail(email).getData();
        return theUserValidation != null;
    }

    public Session validatecode2FA(String code2FA, String sessionId) {
        Session theSession = this.theSessionService.findById(sessionId).getData();

        if (theSession != null && theSession.getCode2FA() != null && theSession.getCode2FA().equals(code2FA)) {
            theSession.setActive(true);
            return this.theSessionService.update(sessionId, theSession).getData();
        }
        return null;
    }

    public boolean permissionsValidation(final HttpServletRequest request, Permission permissionData) {
        ValidationResult result = this.theValidatorService.validationRolePermission(request, permissionData.getUrl(), permissionData.getMethodValue());
        return result == ValidationResult.SUCCESS;
    }

    public boolean existUserById(String user_id) {
        return this.theUserService.existUserById(user_id);
    }

    public ApiResponse<User> findUserByEmail(String email){
        return this.theUserService.findByEmail(email);
    }
}