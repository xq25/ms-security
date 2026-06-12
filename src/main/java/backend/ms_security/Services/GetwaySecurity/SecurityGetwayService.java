package backend.ms_security.Services.GetwaySecurity;

import backend.ms_security.Models.ApiResponse;
import backend.ms_security.Models.User;
import backend.ms_security.Models.UserRole;
import backend.ms_security.Services.UserRoleService;
import backend.ms_security.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SecurityGetwayService {
    @Value("${default.role.patient}")
    private String DEFAULT_PATIENT_ID;

    @Value("${default.role.doctor}")
    private String DEFAULT_DOCTOR_ID;

    @Autowired
    private UserService theUserService;

    @Autowired
    private UserRoleService theUserRoleService;

    public boolean existUserById(String user_id) {
        return this.theUserService.existUserById(user_id);
    }

    public boolean existUserByEmail(String email) {
        User theUserValidation = this.theUserService.findByEmail(email).getData();
        return theUserValidation != null;
    }

    public ApiResponse<User> findUserByEmail(String email){
        return this.theUserService.findByEmail(email);
    }

    // Funcion de comunicacion entre ms que permite asignar roles (Siempre y cuando sean por defecto), dentro del sistema
    public ApiResponse<UserRole> assignDefaultRole(String userId, String roleKey) {

        User theUser = this.theUserService.findById(userId).getData();

        if (theUser == null) {
            return ApiResponse.error("Usuario no encontrado");
        }

        String roleId = resolveRoleId(roleKey);

        if (roleId == null) {
            return ApiResponse.error("Rol no permitido");
        }
        System.out.println("Validaciones exitosas");
        // Devolvemos la misma respuesta, sea un fallo o un acierto
        return this.theUserRoleService.addUserRole(userId, roleId);

    }

    // Si se van a agregar mas roles por defecto hay que cargarloss desde el propierties y agregarlos aqui
    private String resolveRoleId(String roleKey) {

        Map<String, String> allowedRoles = Map.of(
                "doctor", DEFAULT_DOCTOR_ID,
                "patient", DEFAULT_PATIENT_ID
        );

        return allowedRoles.get(roleKey.toLowerCase());
    }

    public String getUserNameById(String user_id){
         ApiResponse<User> user = theUserService.findById(user_id);
         if (user.isSuccess()){
             return user.getData().getName();
         }
         return null;
    }

    public String getUserEmailById(String user_id){
         ApiResponse<User> user = theUserService.findById(user_id);
         if (user.isSuccess()){
             return user.getData().getEmail();
         }
         return null;
     }



}
