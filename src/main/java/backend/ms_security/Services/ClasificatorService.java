package backend.ms_security.Services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ClasificatorService {

    @Value("${clasificator.url}")
    private String clasificatorUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Valida si un usuario tiene relaciones
     * dentro del MS Clasificator.
     *
     * @param userId ID del usuario
     * @return true si tiene relaciones
     */
    public boolean existRelation(String userId) {

        try {

            Boolean response = restTemplate.getForObject(
                    clasificatorUrl + "/exist-relation/" + userId,
                    Boolean.class
            );

            return Boolean.TRUE.equals(response);

        } catch (Exception e) {

            System.out.println("❌ Error consultando MS Clasificator: " + e.getMessage());
            return false;
        }
    }
}