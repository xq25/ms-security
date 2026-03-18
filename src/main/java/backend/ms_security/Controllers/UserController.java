package backend.ms_security.Controllers;

import backend.ms_security.Models.User;
import backend.ms_security.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin // Evita el problema de host al ejecutar
@RestController
@RequestMapping("/api/users") // Definimos aqui la ruta del servidor para que se activen ciertos endpoints.
// Clase de invocaciones
public class UserController {

    @Autowired // Auto Instanciacion de service
    private UserService theUserService;

    @GetMapping("") // Solo accede a la ruta (/users/ )
    public List<User> find() {
        return this.theUserService.find();
    }

    @GetMapping("{id}") // Solo accede a la ruta (/users/:id)
    public User findById(@PathVariable String id) { // Sacamos la variables dentro de la ruta (id)
        return this.theUserService.findById(id);
    }

    @PostMapping // Asociado directamente a los metodos de creacion(No asignamos un metodo especifico)
    public User create(@RequestBody User newUser) { // Pedimos extraer el body para almaacenarlo como una variable
        return this.theUserService.create(newUser);
    }

    @PutMapping("{id}") // Solicitamos laa variable dentro de la ruta y tambien extraemos la informacion del body.
    public User update(@PathVariable String id, @RequestBody User newUser) {
        return this.theUserService.update(id, newUser);
    }

    @DeleteMapping("{id}") // Solo solicitamos la variable en la ruta.
    public void delete(@PathVariable String id) {
        this.theUserService.delete(id);
    }

    @PostMapping("{user_id}/profile/{profile_id}")
    public ResponseEntity<Map<String, String>> addUserProfile(
            @PathVariable String user_id,
            @PathVariable String profile_id) {

        boolean response = this.theUserService.addProfile(user_id, profile_id);
        if (response) {
            return ResponseEntity.ok(Map.of("message", "Success"));
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User or Profile not found"));
        }
    }
    @DeleteMapping("{user_id}/profile/{profile_id}")
    public ResponseEntity<Map<String, String>> deleteUserProfile(
            @PathVariable String user_id,
            @PathVariable String profile_id) {

        boolean response = this.theUserService.removeProfile(user_id, profile_id);
        if (response) {
            return ResponseEntity.ok(Map.of("message", "Success"));
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User or Profile not found"));
        }
    }
    @PostMapping("{userId}/session/{sessionId}")
    public ResponseEntity<Map<String, String>> addUserSession(
            @PathVariable String userId,
            @PathVariable String sessionId) {

        boolean response = this.theUserService.addSession(userId, sessionId);
        if (response) {
            return ResponseEntity.ok(Map.of("message", "Success"));
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User or Session not found"));
        }
    }
    @DeleteMapping("{userId}/session/{sessionId}")
    public ResponseEntity<Map<String, String>> deleteUserSession(
            @PathVariable String userId,
            @PathVariable String sessionId) {

        boolean response = this.theUserService.removeSession(userId, sessionId);
        if (response) {
            return ResponseEntity.ok(Map.of("message", "Success"));
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User or Session not found"));
        }
    }

}
