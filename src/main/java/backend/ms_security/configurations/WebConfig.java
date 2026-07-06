package backend.ms_security.configurations;

import backend.ms_security.interceptors.SecurityInterceptor;
import org.springframework.web.filter.CorsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

// Estamos configurando el framework para validar cada que llegue algo desde afuera
@Configuration
public class WebConfig implements WebMvcConfigurer {
    // Inyectamos el interceptor
    @Autowired
    private SecurityInterceptor securityInterceptor; // Valida el token, los permisos, etc.

    @Value("${app.storage.location}")
    private String storageLocation;

    // Orígenes permitidos para CORS. En local: "http://localhost:4200".
    // En Docker se inyecta via env var APP_CORS_ALLOWED_ORIGINS="http://localhost"
    // (Spring Boot convierte APP_CORS_ALLOWED_ORIGINS → app.cors.allowed-origins)
    @Value("${app.cors.allowed-origins:http://localhost:4200}")
    private String corsAllowedOrigins;

    // Este Bean corre ANTES del interceptor, resuelve el OPTIONS limpiamente (Verificar)
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        Arrays.stream(corsAllowedOrigins.split(","))
              .map(String::trim)
              .forEach(config::addAllowedOrigin);
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(); // Sin argumentos
        source.registerCorsConfiguration("/api/**", config);

        return new CorsFilter(source);
    }

    // --- Nota:  Preguntarle a felipe sobre esto !!!!!!!!!!!!!!!!!!!!!!!
    @Override
    //Esto permite realizar peticiones directamente desde el front
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(corsAllowedOrigins.split(","))  // Desde app.cors.allowed-origins
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    // Guardado de informacion de forma local
    // Expone la carpeta uploads/photos para acceder a las fotos por URL (Expone el recurso como estatico) Para ser almacenado
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/photos/**")
                .addResourceLocations("file:" + storageLocation + "/");
    }
    // Sobre escribimos el metodo addInterceptor
    @Override
    public void addInterceptors(InterceptorRegistry /*Interceptor propio del framework*/ registry) {
        // Se activa el interceptor cada que se venga algo dentro del request con el sufijo /api/xxxxxxxxxxxx
        registry.addInterceptor(securityInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/public/**", "/getway/security/api/**"); // Excluimos del interceptor las requeste que vengan con el sufijo /api/public

    }

}