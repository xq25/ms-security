package backend.ms_security.Controllers;

import backend.ms_security.Models.ApiResponse;
import backend.ms_security.Models.RolePermission;
import backend.ms_security.Services.RolePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/role-permission")
public class RolePermissionController {

    @Autowired
    private RolePermissionService theService;

    @GetMapping("role/{role_id}")
    public ResponseEntity<ApiResponse<List<RolePermission>>> getPermissionsByRole(@PathVariable String role_id) {
        return ResponseEntity.ok(this.theService.getPermissionsByRole(role_id));
    }

    @GetMapping("permission/{permission_id}")
    public ResponseEntity<ApiResponse<List<RolePermission>>> getRolesByPermission(@PathVariable String permission_id) {
        return ResponseEntity.ok(this.theService.getRolesByPermission(permission_id));
    }

    @PostMapping("role/{role_id}/permission/{permission_id}")
    public ResponseEntity<ApiResponse<RolePermission>> addRolePermission(
            @PathVariable String role_id, @PathVariable String permission_id) {
        ApiResponse<RolePermission> response = this.theService.addRolePermission(role_id, permission_id);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @DeleteMapping("{role_permission_id}")
    public ResponseEntity<ApiResponse<Void>> removeRolePermission(@PathVariable String role_permission_id) {
        ApiResponse<Void> response = this.theService.removeRolePermission(role_permission_id);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}