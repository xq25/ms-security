package backend.ms_security.Controllers;

import backend.ms_security.Models.ApiResponse;
import backend.ms_security.Models.Profile;
import backend.ms_security.Services.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    @Autowired
    private ProfileService theProfileService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<Profile>>> find() {
        return ResponseEntity.ok(this.theProfileService.find());
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<Profile>> findById(@PathVariable String id) {
        ApiResponse<Profile> response = this.theProfileService.findById(id);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @GetMapping("user/{user_id}")
    public ResponseEntity<ApiResponse<Profile>> findByUserID(@PathVariable String user_id) {
        ApiResponse<Profile> response = this.theProfileService.findProfileByUser(user_id);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Profile>> create(@RequestBody Profile newProfile) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.theProfileService.create(newProfile));
    }

    @PutMapping("{id}")
    public ResponseEntity<ApiResponse<Profile>> update(@PathVariable String id, @RequestBody Profile newProfile) {
        ApiResponse<Profile> response = this.theProfileService.update(id, newProfile);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        ApiResponse<Void> response = this.theProfileService.delete(id);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}