package backend.ms_security.Services;

import backend.ms_security.Models.ApiResponse;
import backend.ms_security.Models.Permission;
import backend.ms_security.Models.Role;
import backend.ms_security.Models.RolePermission;
import backend.ms_security.Repositories.PermissionRepository;
import backend.ms_security.Repositories.RolePermissionRepository;
import backend.ms_security.Repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolePermissionService {

    @Autowired
    private RolePermissionRepository theRolePermissionRepository;
    @Autowired
    private RoleRepository theRoleRepository;
    @Autowired
    private PermissionRepository thePermissionRepository;

    public ApiResponse<List<RolePermission>> getPermissionsByRole(String role_id) {
        return ApiResponse.success(this.theRolePermissionRepository.getPermissionsByRole(role_id), "Permisos del rol obtenidos correctamente");
    }

    public ApiResponse<List<RolePermission>> getRolesByPermission(String permission_id) {
        return ApiResponse.success(this.theRolePermissionRepository.getRolesByPermission(permission_id), "Roles del permiso obtenidos correctamente");
    }

    public ApiResponse<Void> addRolePermission(String role_id, String permission_id) {
        Role role = this.theRoleRepository.findById(role_id).orElse(null);
        Permission permission = this.thePermissionRepository.findById(permission_id).orElse(null);
        if (role == null || permission == null) return ApiResponse.error("Rol o permiso no encontrado");
        this.theRolePermissionRepository.save(new RolePermission(role, permission));
        return ApiResponse.success("Permiso asignado al rol correctamente");
    }

    public ApiResponse<Void> removeRolePermission(String role_permission_id) {
        RolePermission rp = this.theRolePermissionRepository.findById(role_permission_id).orElse(null);
        if (rp == null) return ApiResponse.error("Relación rol-permiso no encontrada");
        this.theRolePermissionRepository.delete(rp);
        return ApiResponse.success("Permiso removido del rol correctamente");
    }
}