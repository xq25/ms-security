package backend.ms_security.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import backend.ms_security.Models.Permission;
import backend.ms_security.Services.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/permissions")
public class PermissionController {

    @Autowired
    private PermissionService thePermissionService;

    @GetMapping("")
    public List<Permission> find() {
        return this.thePermissionService.find();
    }

    @GetMapping("{id}")
    public Permission findById(@PathVariable String id) {
        return this.thePermissionService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Permission newPermission) {
        Permission thePermission = this.thePermissionService.create(newPermission);

        if (thePermission == null) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "El permiso con esas características ya existe"));
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Permiso generado correctamente",
                        "permission", thePermission
                ));
    }

    @PutMapping("{id}")
    public Permission update(@PathVariable String id, @RequestBody Permission newPermission) {
        return this.thePermissionService.update(id, newPermission);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable String id) {
        this.thePermissionService.delete(id);
    }
}

