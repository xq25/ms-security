package backend.ms_security.Repositories;

import backend.ms_security.Models.RolePermission;
import backend.ms_security.Models.UserRole;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface UserRoleRepository extends MongoRepository<UserRole, String> {
    @Query("{ 'user.$id' : ObjectId(?0) }")
    List<UserRole> getRolesByUser(String user_id);

    @Query("{ 'role.$id' : ObjectId(?0) }")
    List<UserRole> getUsersByRole(String role_id);

    @Query("{'user.$id': ObjectId(userId), 'role.$id': ObjectId(roleId)}")
    UserRole getByUserIdAndRoleId(String userId, String roleId );
}
