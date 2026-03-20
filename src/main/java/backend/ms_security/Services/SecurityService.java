package backend.ms_security.Services;

import backend.ms_security.Models.Session;
import backend.ms_security.Models.User;
import backend.ms_security.Repositories.UserRepository;
import com.google.firebase.auth.FirebaseToken;
import org.apache.catalina.SessionIdGenerator;
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

    public Session login(User theNewUser) {
        String token = null;
        Date expiryDate = null;

        Session actualSession = null;

        //Validamos que el usuario que se desea loguear exista en nuestra base de datos.
        User theActualUser = this.theUserService.findByEmail(theNewUser.getEmail());
        if (theActualUser != null &&
                theActualUser.getPassword().equals(theEncryptionService.convertSHA256(theNewUser.getPassword()))) { // Nos traemos la contraseña de la base de datos ( Cifrada ) y la comparamos con la que viene del front pero que vamos a encryptar
            expiryDate = theJwtService.getExpirationDate();
            token = theJwtService.generateToken(theActualUser); // Generamos el token para el usuario logueado existosamente.

            // Generamos la nueva session (Sin usuario)
            Session emptySession = theSessionService.create(new Session(token, expiryDate));
            // Asignamos el usuario a la Session
            theUserService.addSession(theActualUser.getId() ,emptySession.getId());

            // Recargamos a la session con el usuario asociado
            actualSession = theSessionService.findById(emptySession.getId());

            return actualSession;
        } else {
            return actualSession;
        }
    }
    public Session loginOAuth(String idToken) {
        // 1. Verificar el token con Firebase
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
            newUser.setName(name);
            newUser.setPassword("OAUTH_USER"); // Sin contraseña real

            /** Nota !!!!!!!!!!!!!!!!!!!!!!!!!!!!!
             *  Preguntarle a felipe si esto esta bien, si debemos generar un usuario sin contrasena,
             * eso podria significar que a la hora de iniciar session, solo con saber el correo.
             * Ya podriamos entrar*/
            theActualUser = theUserService.create(newUser);
        }

        // 4. Generar sesión (misma lógica que tu login manual)
        Date expiryDate = theJwtService.getExpirationDate();
        String token    = theJwtService.generateToken(theActualUser);

        Session emptySession = theSessionService.create(new Session(token, expiryDate));
        theUserService.addSession(theActualUser.getId(), emptySession.getId());

        return theSessionService.findById(emptySession.getId());
    }


    /*
    public boolean permissionsValidation(final HttpServletRequest request,
                                         @RequestBody Permission thePermission) {
        boolean success=this.theValidatorsService.validationRolePermission(request,thePermission.getUrl(),thePermission.getMethod());
        return success;
    }
    */

}
