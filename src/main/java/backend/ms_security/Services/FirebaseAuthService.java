package backend.ms_security.Services;

import backend.ms_security.Models.Session;
import backend.ms_security.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class FirebaseAuthService {
    /**
     * Verifica el idToken que viene del frontend (Firebase)
     * y devuelve el token decodificado con la info del usuario.
     */
    public FirebaseToken verifyToken(String idToken) {
        try {
            return FirebaseAuth.getInstance().verifyIdToken(idToken);
        } catch (Exception e) {
            System.out.println("Error en loginOAuth: " + e.getMessage()); // 👈
            return null;
        }
    }
}
