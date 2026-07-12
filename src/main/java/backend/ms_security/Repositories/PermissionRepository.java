package backend.ms_security.Repositories;

import backend.ms_security.Models.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface PermissionRepository extends MongoRepository<Permission, String> {

    @Query("{'url':?0,'method':?1}")
    Permission getPermission(String url, String method);

    Page<Permission> findByUrlContainingIgnoreCase(String url, Pageable pageable);

    Page<Permission> findByModelContainingIgnoreCase(String model, Pageable pageable);
}
