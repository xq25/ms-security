package backend.ms_security.Services;

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

    public List<RolePermission> getPermissionsByRole(String role_id){
        return this.theRolePermissionRepository.getPermissionsByRole(role_id);
    }

    public boolean addRolePermission(String role_id, String permission_id){
        Role role = this.theRoleRepository.findById(role_id).orElse(null);
        Permission permission = this.thePermissionRepository.findById(permission_id).orElse(null);

        if (role != null && permission != null){
            RolePermission newRolePermission = new RolePermission(role, permission);
            this.theRolePermissionRepository.save(newRolePermission);
            return true;
        }
        else{
            return false;
        }
    }

    public boolean removeRolePermission(String role_permission_id){
        RolePermission removeRolePermission = this.theRolePermissionRepository.findById(role_permission_id).orElse(null);

        if (removeRolePermission != null){
            this.theRolePermissionRepository.delete(removeRolePermission);
            return true;
        }else{
            return false;
        }

    }
}
