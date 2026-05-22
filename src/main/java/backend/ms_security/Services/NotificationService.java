package backend.ms_security.Services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationService {

    @Value("${notifications.url}")
    private String notificationsUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Llama al microservicio de notificaciones para enviar un correo.
     * @param email      Destinatario
     * @param body       Cuerpo del mensaje
     */
    public void sendEmail(String email, String body) {
        try {
            Map<String, String> request = new HashMap<>();
            request.put("email", email);
            request.put("body", body);

            restTemplate.postForObject(
                    notificationsUrl + "/send-email",
                    request,
                    Map.class
            );

            System.out.println("✅ Correo enviado a: " + email);

        } catch (Exception e) {
            System.out.println("❌ Error al enviar correo: " + e.getMessage());
        }
    }
}
