package backend.ms_security.Services.Storage;

import org.springframework.web.multipart.MultipartFile;

public interface PhotoStorageService {
    // Este metodo es adaptable para manejar una separacion de el almacenamiento local, habria que agregar un parametro en el que especifique la carpeta del store a la que va.
    String store(MultipartFile file);   // Retorna la URL
    void delete(String url);            // Para cuando borres fotos
}