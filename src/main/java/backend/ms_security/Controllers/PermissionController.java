package backend.ms_security.Controllers;

import backend.ms_security.Models.ApiResponse;
import backend.ms_security.Models.Permission;
import backend.ms_security.Services.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    @Autowired
    private PermissionService thePermissionService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<Permission>>> find() {
        return ResponseEntity.ok(this.thePermissionService.find());
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<Permission>> findById(@PathVariable String id) {
        ApiResponse<Permission> response = this.thePermissionService.findById(id);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Permission>> create(@RequestBody Permission newPermission) {
        ApiResponse<Permission> response = this.thePermissionService.create(newPermission);
        return response.isSuccess()
                ? ResponseEntity.status(HttpStatus.CREATED).body(response)
                : ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @PutMapping("{id}")
    public ResponseEntity<ApiResponse<Permission>> update(@PathVariable String id, @RequestBody Permission newPermission) {
        ApiResponse<Permission> response = this.thePermissionService.update(id, newPermission);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        ApiResponse<Void> response = this.thePermissionService.delete(id);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}