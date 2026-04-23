package backend.ms_security.Services;

import backend.ms_security.Models.*;
import backend.ms_security.Repositories.PermissionRepository;
import backend.ms_security.Repositories.RolePermissionRepository;
import backend.ms_security.Repositories.UserRepository;
import backend.ms_security.Repositories.UserRoleRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ValidatorsService {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private PermissionRepository thePermissionRepository;

    @Autowired
    private UserRepository theUserRepository;

    @Autowired
    private RolePermissionRepository theRolePermissionRepository;

    @Autowired
    private UserRoleRepository theUserRoleRepository;

    private static final String BEARER_PREFIX = "Bearer ";

    /** @Param request : Aqui vienen almacenada la ruta, el metodo, el token y el body.
     *
     */
    public ValidationResult validationRolePermission(HttpServletRequest request, String url, String method) {
        User theUser = this.getUser(request);

        if (theUser == null) {
            System.out.println("❌ Token inválido o usuario no encontrado");
            return ValidationResult.INVALID_TOKEN;
        }

        url = url.replaceAll("[0-9a-fA-F]{24}|\\d+", "?");
        System.out.println("URL " + url + " metodo " + method);

        Permission thePermission = this.thePermissionRepository.getPermission(url, method);
        List<UserRole> roles = this.theUserRoleRepository.getRolesByUser(theUser.getId());

        int i = 0;
        while (i < roles.size()) {
            UserRole actual = roles.get(i);
            Role theRole = actual.getRole();

            if (theRole != null && thePermission != null) {
                RolePermission theRolePermission = this.theRolePermissionRepository
                        .getRolePermission(theRole.getId(), thePermission.getId());
                if (theRolePermission != null) {
                    System.out.println("✅ Permiso concedido");
                    return ValidationResult.SUCCESS;
                }
            }
            i++;
        }

        System.out.println("❌ El usuario no posee el permiso requerido");
        return ValidationResult.PERMISSION_DENIED;
    }

    /**Obtiene o analiza el token y decifra dichos datos para poder re armar al usuario
     * @request : Contiene el token en el header.
     *
     */
    public User getUser(final HttpServletRequest request) {
        User theUser=null;
        // En la request viene dentro del header un campo especifico Authorization.
        String authorizationHeader = request.getHeader("Authorization");
        System.out.println("Header "+authorizationHeader);

        // Si en el header autorization viene algo continuamos la validacion
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {

            String token = authorizationHeader.substring(BEARER_PREFIX.length());// Recortamos todo el autoriztion para quedarnoiss solo con el contenido del token
            System.out.println("Bearer Token: " + token);

            // Validamos que el token no haya expirado
            boolean validateToken = this.jwtService.validateToken(token);
            if (validateToken){
                // Desactivamos la seccion del usuario.
                System.out.println("Token Valido");
            }

            // Del token devuelve al usuario
            User theUserFromToken=jwtService.getUserFromToken(token); // instancia de usuario.
            if(theUserFromToken!=null && validateToken) { // Solo precargamos al usuario si el token es valido y existe el usuario para ese token
                //Pre carga del usuario. Tenemos que traer el usuario desde la base de datos ya que este ya contiene las relaciones.
                theUser= this.theUserRepository.findById(theUserFromToken.getId())
                        .orElse(null);

            }
        }

        return theUser;
    }
}

