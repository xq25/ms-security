package backend.ms_security.interceptors;

import backend.ms_security.Models.ValidationResult;
import backend.ms_security.Services.ValidatorsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

// Porteria de la aplicacion
@Component
// Implementamos el interceptor propio de Spring Boot (Handler Interceptor). Este es el celador que va a validar quien entra y no. Mediante ValidationService
public class SecurityInterceptor implements HandlerInterceptor {

    @Autowired // Inyectamos las validaciones
    private ValidatorsService validatorService;

    /// Antes de entrar algo al backend, entra por aqui. Esto para poder validar el request.
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        // El preflight CORS no trae Authorization por diseño.
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        System.out.println("[SecurityInterceptor] " + request.getMethod() + " " + request.getRequestURI());

        ValidationResult result = this.validatorService.validationRolePermission(
                request, request.getRequestURI(), request.getMethod()
        );

        if (result == ValidationResult.SUCCESS) {
            return true;
        }

        // Construimos el mensaje según el resultado
        int status;
        String message;

        switch (result) {
            case INVALID_TOKEN -> {
                status  = HttpServletResponse.SC_UNAUTHORIZED; // 401
                message = "Token inválido o sesión expirada.";
            }
            case PERMISSION_DENIED -> {
                status  = HttpServletResponse.SC_FORBIDDEN; // 403
                message = "No tienes permiso para realizar esta acción.";
            }
            // Agregamos el estado por defecto por motivos de seguridad. Por defecto es mejor negar el acceso que concederlo.
            default -> {
                status  = HttpServletResponse.SC_FORBIDDEN;
                message = "Acceso denegado.";
            }
        }

        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> body = new HashMap<>();
        body.put("status", status);
        body.put("error", result.name());
        body.put("message", message);

        new ObjectMapper().writeValue(response.getWriter(), body);
        return false;
    }

    /// Todo lo que salga del backend va pasar por aqui. Esto para validar el Response.
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        // Lógica a ejecutar después de que se haya manejado la solicitud por el controlador
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) throws Exception {
        // Lógica a ejecutar después de completar la solicitud, incluso después de la renderización de la vista
    }
}
