package backend.ms_security.Controllers;

import backend.ms_security.Models.ApiResponse;
import backend.ms_security.Models.UserRole;
import backend.ms_security.Services.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/user-role")
public class UserRoleController {

    @Autowired
    private UserRoleService theUserRoleService;

    @GetMapping("user/{user_id}")
    public ResponseEntity<ApiResponse<List<UserRole>>> getRolesByUser(@PathVariable String user_id) {
        ApiResponse<List<UserRole>> response = this.theUserRoleService.getRolesByUser(user_id);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @GetMapping("role/{role_id}")
    public ResponseEntity<ApiResponse<List<UserRole>>> getUsersByRole(@PathVariable String role_id) {
        ApiResponse<List<UserRole>> response = this.theUserRoleService.getUsersByRole(role_id);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @PostMapping("user/{userId}/role/{roleId}")
    public ResponseEntity<ApiResponse<Void>> addUserRole(
            @PathVariable String userId, @PathVariable String roleId) {
        ApiResponse<Void> response = this.theUserRoleService.addUserRole(userId, roleId);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @DeleteMapping("{userRoleId}")
    public ResponseEntity<ApiResponse<Void>> removeUserRole(@PathVariable String userRoleId) {
        ApiResponse<Void> response = this.theUserRoleService.removeUserRole(userRoleId);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}