package backend.ms_security.Services;

import backend.ms_security.Models.ApiResponse;
import backend.ms_security.Models.Role;
import backend.ms_security.Models.User;
import backend.ms_security.Models.UserRole;
import backend.ms_security.Repositories.RoleRepository;
import backend.ms_security.Repositories.UserRepository;
import backend.ms_security.Repositories.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRoleService {

    @Autowired
    private UserRoleRepository theUserRoleRepository;
    @Autowired
    private RoleRepository theRoleRepository;
    @Autowired
    private UserRepository theUserRepository;

    public ApiResponse<List<UserRole>> getRolesByUser(String user_id) {
        User theUser = this.theUserRepository.findById(user_id).orElse(null);
        if (theUser == null) return ApiResponse.error("Usuario no encontrado");
        return ApiResponse.success(this.theUserRoleRepository.getRolesByUser(user_id), "Roles del usuario obtenidos correctamente");
    }

    public ApiResponse<List<UserRole>> getUsersByRole(String role_id) {
        Role theRole = this.theRoleRepository.findById(role_id).orElse(null);
        if (theRole == null) return ApiResponse.error("Rol no encontrado");
        return ApiResponse.success(this.theUserRoleRepository.getUsersByRole(role_id), "Usuarios del rol obtenidos correctamente");
    }

    public ApiResponse<Void> addUserRole(String userId, String roleId) {
        User user = this.theUserRepository.findById(userId).orElse(null);
        Role role = this.theRoleRepository.findById(roleId).orElse(null);
        if (user == null || role == null) return ApiResponse.error("Usuario o rol no encontrado");
        this.theUserRoleRepository.save(new UserRole(user, role));
        return ApiResponse.success("Rol asignado al usuario correctamente");
    }

    public ApiResponse<Void> removeUserRole(String userRoleId) {
        UserRole userRole = this.theUserRoleRepository.findById(userRoleId).orElse(null);
        if (userRole == null) return ApiResponse.error("Relación usuario-rol no encontrada");
        this.theUserRoleRepository.delete(userRole);
        return ApiResponse.success("Rol removido del usuario correctamente");
    }
}