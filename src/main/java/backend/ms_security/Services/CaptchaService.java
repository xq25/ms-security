package backend.ms_security.Services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class CaptchaService {

    @Value("${captcha.key}")
    private String secretKey;

    private static final String VERIFY_URL =
            "https://www.google.com/recaptcha/api/siteverify";

    /**
     * Valida el captchaToken contra la API de Google reCAPTCHA v3.
     * Retorna true si el score es >= 0.5 (considerado humano).
     */
    public boolean validate(String captchaToken) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            String url = VERIFY_URL
                    + "?secret=" + secretKey
                    + "&response=" + captchaToken;

            // Google responde con un objeto JSON
            Map response = restTemplate.postForObject(url, null, Map.class);

            if (response == null) return false;

            boolean success = (Boolean) response.get("success");
            if (!success) return false;

            // v3 devuelve un score — 1.0 es humano, 0.0 es bot
            // Usamos 0.5 como umbral mínimo
            double score = (Double) response.get("score");
            // depuracion :
            // System.out.println("reCAPTCHA score: " + score);

            return score >= 0.5;

        } catch (Exception e) {
            System.out.println("Error validando captcha: " + e.getMessage());
            return false;
        }
    }
}