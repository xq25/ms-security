package backend.ms_security.Controllers;

import backend.ms_security.Services.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/photos")
public class PhotoController {

    @Autowired
    private PhotoService photoService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadPhoto(@RequestParam("file") MultipartFile file) {
        String url = photoService.uploadPhoto(file);
        return ResponseEntity.ok(Map.of("url", url));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deletePhoto(@RequestParam("url") String url) {
        photoService.deletePhoto(url);
        return ResponseEntity.ok(Map.of("message", "Foto eliminada correctamente"));
    }
}