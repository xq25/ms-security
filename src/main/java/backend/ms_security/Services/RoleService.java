package backend.ms_security.Services;

import backend.ms_security.Models.Permission;
import backend.ms_security.Models.Role;
import backend.ms_security.Models.RolePermission;
import backend.ms_security.Models.UserRole;
import backend.ms_security.Repositories.PermissionRepository;
import backend.ms_security.Repositories.RolePermissionRepository;
import backend.ms_security.Repositories.RoleRepository;
import backend.ms_security.Repositories.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    @Autowired
    private RoleRepository theRoleRepository;

    @Autowired
    private UserRoleRepository theUserRoleRepository;

    @Autowired
    private RolePermissionRepository theRolePermissionRepository;


    public List<Role> find(){
        return this.theRoleRepository.findAll();
    }

    public Role findById(String id){
        return this.theRoleRepository.findById(id).orElse(null);
    }

    public Role create(Role newRole){
        return this.theRoleRepository.save(newRole);
    }

    public Role update(String id, Role newRole){
        Role actualRole = this.theRoleRepository.findById(id).orElse(null);

        if(actualRole != null){
            actualRole.setName(newRole.getName());
            actualRole.setDescription(newRole.getDescription());
            this.theRoleRepository.save(actualRole);
            return actualRole;
        } else {
            return null;
        }
    }

    public boolean delete(String id){
        Role theRole = this.theRoleRepository.findById(id).orElse(null);
        if(theRole != null){

            List<UserRole> users = this.theUserRoleRepository.getUsersByRole(theRole.getId());
            List<RolePermission> permissions = this.theRolePermissionRepository.getPermissionsByRole(theRole.getId());

            if (users != null && !users.isEmpty()) {
                this.theUserRoleRepository.deleteAll(users);
            }
            if (permissions != null && !permissions.isEmpty()){
                this.theRolePermissionRepository.deleteAll(permissions);
            }

            this.theRoleRepository.delete(theRole);
            return true;
        }
        else{
            return false;
        }
    }

}

