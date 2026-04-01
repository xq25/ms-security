package backend.ms_security.Services.Storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class LocalPhotoStorageService implements PhotoStorageService {

    // En tu application.properties defines: app.storage.location=uploads/photos
    @Value("${app.storage.location}")
    private String storageLocation;

    // En tu application.properties defines: app.base.url=http://localhost:8080
    @Value("${app.base.url}")
    private String baseUrl;

    @Override
    public String store(MultipartFile file) {
        try {
            // Generamos un nombre unico para evitar colisiones
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();

            // Creamos la carpeta si no existe
            Path storagePath = Paths.get(storageLocation);
            if (!Files.exists(storagePath)) {
                Files.createDirectories(storagePath);
            }

            // Guardamos el archivo fisicamente
            Path destination = storagePath.resolve(filename);
            file.transferTo(destination);

            // Retornamos la URL publica
            return baseUrl + "/photos/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar la foto: " + e.getMessage());
        }
    }

    @Override
    public void delete(String url) {
        try {
            // Extraemos el nombre del archivo de la URL
            String filename = url.substring(url.lastIndexOf("/") + 1);
            Path filePath = Paths.get(storageLocation).resolve(filename);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Error al eliminar la foto: " + e.getMessage());
        }
    }
}