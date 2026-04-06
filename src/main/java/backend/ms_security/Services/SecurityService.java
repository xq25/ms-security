package backend.ms_security.Services;

import backend.ms_security.Models.Profile;
import backend.ms_security.Models.Session;
import backend.ms_security.Models.User;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    private NotificationService theNotificationService;

    @Value("${rest.expiration}")
    private Long RESET_TOKEN_EXPIRATION;

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


            String token    = theJwtService.generateToken(theActualUser);
            Date expiryDate = theJwtService.getExpirationFromToken(token);

            Session emptySession = theSessionService.create(new Session(token, expiryDate));
            theUserService.addSession(theActualUser.getId(), emptySession.getId());

            // Proceso de enviar el mensaje de confirmacion de registro al correo.
            String body = "Hola " + newUser.getName() + ",\n\n"
                    + "Gracias por registrarte en nuestra plataforma.\n"
                    + "El equipo de MS Security";
            theNotificationService.sendEmail(newUser.getEmail(), body);

            return theSessionService.findById(emptySession.getId());

        }
        return null;
    }

    // LOGOUT
    public boolean logout(String current_session_id){
        return this.theSessionService.invalidateSession(current_session_id);
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

            token = theJwtService.generateToken(theActualUser);
            expiryDate = theJwtService.getExpirationFromToken(token);

            Session emptySession = theSessionService.create(new Session(token, expiryDate));
            theUserService.addSession(theActualUser.getId(), emptySession.getId());
            actualSession = theSessionService.findById(emptySession.getId());

            return actualSession;
        } else {
            return actualSession;
        }
    }

    // FUNCION GENERICA DE AUTENTICACION CON TERCEROS
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
        String token    = theJwtService.generateToken(theActualUser);
        Date expiryDate = theJwtService.getExpirationFromToken(token);


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

    /** RESET PASSWORD
     * @Param email -> Email registrado por el usuario para la recuperacion de su contrasena.
     * 1. Validamos que el emial exista en nuestra base de datos.
     *  1.2 Si existe, nos comunicamos con el microservicio de notificaciones,
     *  en el cual vamos a asignar un redireccionamiento a la pagina de reset-password,
     *  generando tambien una session (TEMPORAL) Para que se pueda modificar este atributo (password), del User
     *  1.3 Si no existe, Retornamos Null
     */
    public boolean resetPassword(String email) {
        // Buscamos el usuario por el email que se ingreso desde el Front.
        User theUser = this.theUserService.findByEmail(email);

        // Validamos si el usuario existe en nuestra base de datos.
        if (theUser != null) {
            // --- PROCESO DE ENVIAR EL MENSAJE AL USUARIO. ---

            // 2. Generar token temporal.
            String resetToken = theJwtService.generateTemporalToken(theUser, RESET_TOKEN_EXPIRATION);

            // 3. Construir el link con el token.
            String resetLink = "http://localhost:4200/reset-password?token=" + resetToken;

            // 4. Construir el cuerpo del correo
            String body = "Hola " + theUser.getName() + ",\n\n"
                    + "Recibimos una solicitud para restablecer tu contraseña.\n"
                    + "Haz clic en el siguiente enlace para continuar:\n\n"
                    + resetLink + "\n\n"
                    + "⚠️ Este enlace expira en 30 minutos.\n\n"
                    + "Si no solicitaste esto, ignora este mensaje.\n\n"
                    + "El equipo de MS Security";

            // 5. Enviar correo via microservicio de notificaciones
            theNotificationService.sendEmail(email, body);
        }

        // Siempre true — el frontend muestra el mismo mensaje
        return true;
    }

    // OBTENER LA SESSION TEMPORAL(PARA PROCESO DE RESET- PASSWORD)
    public Session getTemporalSession(String token) {
        // 1. Reconstruir el usuario desde el token
        User theTemporalUser = this.theJwtService.getUserFromToken(token);
        if (theTemporalUser == null) return null;

        // 2. Extraer la expiración directamente del token
        Date expiryDate = this.theJwtService.getExpirationFromToken(token);

        // 2.1 Validamos que no exista ya una session activa con ese token
        if (this.theSessionService.isTokenAlreadyUsed(token)) {
            System.out.println("❌ Token ya utilizado, acceso denegado");
            return null; //  El controller devolverá 401
        }

        // 3. Crear la sesión temporal con la misma expiración del token
        Session emptySession = theSessionService.create(new Session(token, expiryDate));
        theUserService.addSession(theTemporalUser.getId(), emptySession.getId());

        // 4. Retornar la sesión con el usuario asociado
        return theSessionService.findById(emptySession.getId());
    }

    // VALIDACION DE EXISTENCIA MEDIANTE (EMAIL)
    public boolean existUserByEmail(String email){
        User theUserValidation = this.theUserService.findByEmail(email);
        boolean validaiton = false;

        if (theUserValidation != null){
            validaiton = true;
        }
        return validaiton;
    }

}