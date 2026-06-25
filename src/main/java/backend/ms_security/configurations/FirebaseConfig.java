package backend.ms_security.configurations;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initFirebase() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {

                // En Docker: FIREBASE_CREDENTIALS_PATH apunta al archivo montado como volumen.
                // En local sin Docker: cae al classpath (src/main/resources/serviceAccountKey.json).
                String credentialsPath = System.getenv("FIREBASE_CREDENTIALS_PATH");
                InputStream serviceAccount = (credentialsPath != null && !credentialsPath.isBlank())
                        ? new FileInputStream(credentialsPath)
                        : new ClassPathResource("serviceAccountKey.json").getInputStream();

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
                System.out.println("✅ Firebase inicializado correctamente");

            } else {
                System.out.println("✅ Firebase ya estaba inicializado");
            }
        } catch (Exception e) {
            System.out.println("❌ Error al inicializar Firebase: " + e.getMessage());
            e.printStackTrace();
        }
    }
}