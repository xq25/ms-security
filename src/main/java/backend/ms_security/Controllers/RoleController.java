package backend.ms_security.Controllers;

import backend.ms_security.Models.ApiResponse;
import backend.ms_security.Models.Role;
import backend.ms_security.Services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService theRoleService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<Role>>> find() {
        return ResponseEntity.ok(this.theRoleService.find());
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<Role>> findById(@PathVariable String id) {
        ApiResponse<Role> response = this.theRoleService.findById(id);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Role>> create(@RequestBody Role newRole) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.theRoleService.create(newRole));
    }

    @PutMapping("{id}")
    public ResponseEntity<ApiResponse<Role>> update(@PathVariable String id, @RequestBody Role newRole) {
        ApiResponse<Role> response = this.theRoleService.update(id, newRole);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        ApiResponse<Void> response = this.theRoleService.delete(id);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}