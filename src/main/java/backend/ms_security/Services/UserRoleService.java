package backend.ms_security.Services;

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

    public List<UserRole> getRolesByUser(String user_id){
        List<UserRole> userRoles = null;
        User theUser = this.theUserRepository.findById(user_id).orElse(null);

        if (theUser != null ) {
            userRoles = this.theUserRoleRepository.getRolesByUser(user_id);

        }
        return userRoles;

    }

    public List<UserRole> getUsersByRole(String role_id){
        List<UserRole> userRoles = null;
        Role theRole = this.theRoleRepository.findById(role_id).orElse(null);

        if (theRole != null ) {
            userRoles = this.theUserRoleRepository.getUsersByRole(role_id);

        }
        return userRoles;

    }

    public boolean addUserRole(String userId, String roleId){
        User user=this.theUserRepository.findById(userId).orElse(null);
        Role role=this.theRoleRepository.findById(roleId).orElse(null);
        if (user!=null && role!=null){
            UserRole theUserRole= new UserRole(user,role);
            this.theUserRoleRepository.save(theUserRole);
            return true;
        }else{
            return false;
        }
    }

    public boolean removeUserRole(String userRoleId){
        UserRole userRole=this.theUserRoleRepository.findById(userRoleId).orElse(null);
        if (userRole!=null){
            this.theUserRoleRepository.delete(userRole);
            return true;
        }else{
            return false;
        }
    }



}
