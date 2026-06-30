package backend.ms_security.Repositories;

import backend.ms_security.Models.Permission;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface PermissionRepository extends MongoRepository<Permission, String> {
    @Query("{'url':?0,'method':?1}")
    Permission getPermission(String url, String method);

    // Fallback: busca URLs que terminen con el path (soporta URLs con host:port prefix)
    @Query("{'url': {$regex: ?0}, 'method': ?1}")
    Permission getPermissionByRegex(String urlPattern, String method);
}
