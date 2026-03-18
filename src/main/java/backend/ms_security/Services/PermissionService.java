package backend.ms_security.Services;

import backend.ms_security.Models.Permission;
import backend.ms_security.Repositories.PermissionRepository;

import backend.ms_security.Repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionService {

    @Autowired
    private PermissionRepository thePermissionRepository;

    @Autowired
    private RoleRepository theRoleRepository;

    // getAll() -> List<Permission>
    public List<Permission> find() {
        return this.thePermissionRepository.findAll();
    }

    // getById() -> Permission
    public Permission findById(String id) {
        return this.thePermissionRepository.findById(id).orElse(null);
    }

    // create() -> Permission
    public Permission create(Permission newPermission) {
        return this.thePermissionRepository.save(newPermission);
    }

    // update() -> Permission
    public Permission update(String id, Permission newPermission) {
        Permission actualPermission =
                this.thePermissionRepository.findById(id).orElse(null);

        if (actualPermission != null) {
            actualPermission.setUrl(newPermission.getUrl());
            actualPermission.setMethod(newPermission.getMethod());
            actualPermission.setModel(newPermission.getModel());

            this.thePermissionRepository.save(actualPermission);
            return actualPermission;
        } else {
            return null;
        }
    }

    public void delete(String id) {
        Permission thePermission =
                this.thePermissionRepository.findById(id).orElse(null);

        if (thePermission != null) {
            this.thePermissionRepository.delete(thePermission);
        }
    }

}