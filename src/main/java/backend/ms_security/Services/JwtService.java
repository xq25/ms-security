package backend.ms_security.Services;

import backend.ms_security.Models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@Service
public class JwtService {
    // Necesitamos dos variables de entorno para que JWT funcione.
    @Value("${jwt.secret}") // El secreto (Firma).
    private String secret; // Esta es la clave secreta que se utiliza para firmar el token. Debe mantenerse segura.

    @Value("${jwt.expiration}") // (Expiracion del token).
    private Long expiration; // Tiempo de expiración del token en milisegundos.

    // CADA VEZ QUE SE CORRE EL PROGRAMA SE GENERA UNA NUEVA SECRET KEY (POR TANTO, NO PUEDE HABER UNA SESSION VALIDA, ANTES DE CORRER NUEVAMENTE EL PROGRAMA)
    private Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    // GENERAR TOKEN (expiracion por defecto)
    public String generateToken(User theUser) {
        return buildToken(theUser, expiration);
    }

    // GENERAR TOKEN (expiracion personalizada en ms)
    public String generateTemporalToken(User theUser, long customExpiration) {
        return buildToken(theUser, customExpiration);
    }

    // Privatizamos el metodo de construccion del token, y lo reutilizamos para las dos versiones de token que hay en nuestro sistema.
    private String buildToken(User theUser, long expirationMs) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", theUser.getId());
        claims.put("name", theUser.getName());
        claims.put("email", theUser.getEmail());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(theUser.getName())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    public Date getExpirationFromToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            return claimsJws.getBody().getExpiration();
        } catch (Exception e) {
            return null;
        }
    }

    // METODO PARA OBTENER LA EXPIRACION DE UN TOKEN (Default)
    public Date getExpirationDate(){
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        return expiryDate;
    }

    // METODO PARA OBTENER LA EXPIRACION DE UN TOKEN (Temporal)
    public Date getCustomExpirationDate(long customExpiration) {
        Date now = new Date();
        return new Date(now.getTime() + customExpiration);
    }

    // METODO DE VALIDACION DE TOKENS
    public boolean validateToken(String token) {
        boolean validation = true;
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            // Verifica la expiración del token
            Date now = new Date();
            if (claimsJws.getBody().getExpiration().before(now)) {
                validation = false;
            }
        } catch (SignatureException ex) {
            // La firma del token es inválida
            System.out.println(ex.getCause());
            validation = false;
        } catch (Exception e) {
            // Otra excepción
            validation =  false;
        }
        return validation;
    }

    /**
     *Metodo encargaado de devolvernos el usuario al cual pertence el token( Re armar el usuario con el token )
     */
    public User getUserFromToken(String token) {
        try {
            // Validamoss que el token no este corrupto

            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            Claims claims = claimsJws.getBody();

            User user = new User();
            user.setId((String) claims.get("id"));
            user.setName((String) claims.get("name"));
            user.setEmail((String) claims.get("email"));
            return user;
        } catch (Exception e) {
            // En caso de que el token sea inválido o haya expirado
            return null;
        }
    }

    public String generateCode2FA(){
        SecureRandom random = new SecureRandom();
        int code = random.nextInt(10000);
        return String.format("%06d", code);
    }

}
