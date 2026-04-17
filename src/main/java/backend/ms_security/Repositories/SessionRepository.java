package backend.ms_security.Repositories;

import backend.ms_security.Models.Session;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface SessionRepository extends MongoRepository<Session, String> {
    @Query("{ 'token': ?0, 'active': false }")
    Session findInvalidatedSessionByToken(String token);

    @Query("{ 'user.$id': { $oid: ?0 }, 'active': true }")
    Session findActiveSessionByUserId(String userId);
}
