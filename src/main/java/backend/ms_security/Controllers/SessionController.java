package backend.ms_security.Controllers;

import backend.ms_security.Models.ApiResponse;
import backend.ms_security.Models.Session;
import backend.ms_security.Services.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    @Autowired
    private SessionService theSessionService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<Session>>> find() {
        return ResponseEntity.ok(this.theSessionService.find());
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<Session>> findById(@PathVariable String id) {
        ApiResponse<Session> response = this.theSessionService.findById(id);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Session>> create(@RequestBody Session newSession) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.theSessionService.create(newSession));
    }

    @PutMapping("{id}")
    public ResponseEntity<ApiResponse<Session>> update(@PathVariable String id, @RequestBody Session newSession) {
        ApiResponse<Session> response = this.theSessionService.update(id, newSession);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        ApiResponse<Void> response = this.theSessionService.delete(id);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}