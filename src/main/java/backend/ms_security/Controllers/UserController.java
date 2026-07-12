package backend.ms_security.Controllers;

import backend.ms_security.DTOs.Pagination.PageRequestDTO;
import backend.ms_security.DTOs.Response.PagedResponse;
import backend.ms_security.Models.ApiResponse;
import backend.ms_security.Models.User;
import backend.ms_security.Services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService theUserService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<PagedResponse<User>>> find(@Valid @ModelAttribute PageRequestDTO pageRequest) {
        return ResponseEntity.ok(this.theUserService.find(pageRequest));
    }

    @GetMapping("search/by-name")
    public ResponseEntity<ApiResponse<PagedResponse<User>>> searchByName(
            @RequestParam String query,
            @Valid @ModelAttribute PageRequestDTO pageRequest) {
        return ResponseEntity.ok(this.theUserService.searchByName(query, pageRequest));
    }

    @GetMapping("search/by-email")
    public ResponseEntity<ApiResponse<PagedResponse<User>>> searchByEmail(
            @RequestParam String query,
            @Valid @ModelAttribute PageRequestDTO pageRequest) {
        return ResponseEntity.ok(this.theUserService.searchByEmail(query, pageRequest));
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<User>> findById(@PathVariable String id) {
        ApiResponse<User> response = this.theUserService.findById(id);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @GetMapping("email/{email}")
    public ResponseEntity<ApiResponse<User>> findByEmail(@PathVariable String email) {
        ApiResponse<User> response = this.theUserService.findByEmail(email);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<User>> create(@RequestBody User newUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.theUserService.create(newUser));
    }

    @PutMapping("{id}")
    public ResponseEntity<ApiResponse<User>> update(@PathVariable String id, @RequestBody User newUser) {
        ApiResponse<User> response = this.theUserService.update(id, newUser);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        ApiResponse<Void> response = this.theUserService.delete(id);
        if (!response.isSuccess()) {
            HttpStatus status = response.getMessage().contains("Doctor o Paciente")
                    ? HttpStatus.CONFLICT
                    : HttpStatus.NOT_FOUND;
            return ResponseEntity.status(status).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("{user_id}/profile/{profile_id}")
    public ResponseEntity<ApiResponse<Void>> addUserProfile(
            @PathVariable String user_id, @PathVariable String profile_id) {
        ApiResponse<Void> response = this.theUserService.addProfile(user_id, profile_id);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @DeleteMapping("{user_id}/profile/{profile_id}")
    public ResponseEntity<ApiResponse<Void>> deleteUserProfile(
            @PathVariable String user_id, @PathVariable String profile_id) {
        ApiResponse<Void> response = this.theUserService.removeProfile(user_id, profile_id);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @PostMapping("{userId}/session/{sessionId}")
    public ResponseEntity<ApiResponse<Void>> addUserSession(
            @PathVariable String userId, @PathVariable String sessionId) {
        ApiResponse<Void> response = this.theUserService.addSession(userId, sessionId);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @DeleteMapping("{userId}/session/{sessionId}")
    public ResponseEntity<ApiResponse<Void>> deleteUserSession(
            @PathVariable String userId, @PathVariable String sessionId) {
        ApiResponse<Void> response = this.theUserService.removeSession(userId, sessionId);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}