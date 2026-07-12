package backend.ms_security.Repositories;

import backend.ms_security.Models.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoleRepository extends MongoRepository<Role, String> {

    Page<Role> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
