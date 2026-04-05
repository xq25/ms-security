package backend.ms_security.Controllers;

import backend.ms_security.Models.RolePermission;
import backend.ms_security.Models.UserRole;
import backend.ms_security.Services.RolePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/role-permission")
public class RolePermissionController {
    @Autowired
    private RolePermissionService theService ;

    @GetMapping("role/{role_id}")
    public List<RolePermission> getPermissionsByRole(@PathVariable String role_id){
        return this.theService.getPermissionsByRole(role_id);
    }

    @GetMapping("permission/{permission_id}")
    public List<RolePermission> getRolesByPermission(@PathVariable String permission_id){
        return this.theService.getRolesByPermission(permission_id);
    }

    // Agregamos la relacion entre roles y permisos
    @PostMapping("role/{role_id}/permission/{permission_id}")
    public ResponseEntity<Map<String, String>> addRolePermission(@PathVariable String role_id, @PathVariable String permission_id) {

        boolean response = this.theService.addRolePermission(role_id, permission_id);
        if (response) {
            return ResponseEntity.ok(Map.of("message", "Success"));
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Role or Permission not found"));
        }
    }

    //Eliminamos la relacion entre roles y permisos
    @DeleteMapping("{role_permission_id}")
    public ResponseEntity<Map<String, String>> removeRolePermission(@PathVariable String role_permission_id) {

        boolean response = this.theService.removeRolePermission(role_permission_id);
        if (response) {
            return ResponseEntity.ok(Map.of("message", "Success"));
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Role-Permission not found"));
        }
    }

}
