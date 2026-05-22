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

// Estamos configurando el framework para validar cada que llegue algo desde afuera
@Configuration
public class WebConfig implements WebMvcConfigurer {
    // Inyectamos el interceptor
    @Autowired
    private SecurityInterceptor securityInterceptor; // Valida el token, los permisos, etc.

    @Value("${app.storage.location}")
    private String storageLocation;

    // Este Bean corre ANTES del interceptor, resuelve el OPTIONS limpiamente (Verificar)
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:4200");
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
                .allowedOrigins("http://localhost:4200")  // Puerto de Angular
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