package backend.ms_security.Services;
import backend.ms_security.Services.Storage.PhotoStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PhotoService {

    @Autowired
    private PhotoStorageService photoStorageService; // Inyecta la interfaz, no la implementacion

    public String uploadPhoto(MultipartFile file) {
        validateFile(file);
        return photoStorageService.store(file);
    }

    public void deletePhoto(String url) {
        if (url != null && !url.isEmpty()) {
            photoStorageService.delete(url);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("El archivo no puede estar vacio");
        }
        // Validamos que sea una imagen
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Solo se permiten archivos de imagen");
        }
    }
}