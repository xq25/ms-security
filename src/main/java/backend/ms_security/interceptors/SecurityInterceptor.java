package backend.ms_security.interceptors;

import backend.ms_security.Services.ValidatorsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

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
                             Object handler)
            throws Exception {
        // Aqui se hacen todas las respectivas validaciones.
        boolean validation = this.validatorService.validationRolePermission(request,request.getRequestURI(),request.getMethod());
        return validation;
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
