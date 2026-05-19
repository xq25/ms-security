package backend.ms_security.Services;

import backend.ms_security.Models.ApiResponse;
import backend.ms_security.Models.Permission;
import backend.ms_security.Models.RolePermission;
import backend.ms_security.Repositories.PermissionRepository;
import backend.ms_security.Repositories.RolePermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionService {

    @Autowired
    private PermissionRepository thePermissionRepository;
    @Autowired
    private RolePermissionRepository theRolePermissionRepository;

    public ApiResponse<List<Permission>> find() {
        return ApiResponse.success(this.thePermissionRepository.findAll(), "Permisos obtenidos correctamente");
    }

    public ApiResponse<Permission> findById(String id) {
        Permission p = this.thePermissionRepository.findById(id).orElse(null);
        if (p == null) return ApiResponse.error("Permiso no encontrado");
        return ApiResponse.success(p, "Permiso encontrado");
    }

    public ApiResponse<Permission> create(Permission newPermission) {
        Permission existing = this.thePermissionRepository.getPermission(newPermission.getUrl(), newPermission.getMethodValue());
        if (existing != null) return ApiResponse.error("El permiso con esas características ya existe");
        return ApiResponse.success(this.thePermissionRepository.save(newPermission), "Permiso creado correctamente");
    }

    public ApiResponse<Permission> update(String id, Permission newPermission) {
        Permission actual = this.thePermissionRepository.findById(id).orElse(null);
        if (actual == null) return ApiResponse.error("Permiso no encontrado");
        actual.setUrl(newPermission.getUrl());
        actual.setMethod(newPermission.getMethod());
        actual.setModel(newPermission.getModel());
        this.thePermissionRepository.save(actual);
        return ApiResponse.success(actual, "Permiso actualizado correctamente");
    }

    public ApiResponse<Void> delete(String id) {
        Permission thePermission = this.thePermissionRepository.findById(id).orElse(null);
        if (thePermission == null) return ApiResponse.error("Permiso no encontrado");
        List<RolePermission> roles = this.theRolePermissionRepository.getRolesByPermission(thePermission.getId());
        if (roles != null && !roles.isEmpty()) this.theRolePermissionRepository.deleteAll(roles);
        this.thePermissionRepository.delete(thePermission);
        return ApiResponse.success("Permiso eliminado correctamente");
    }
}