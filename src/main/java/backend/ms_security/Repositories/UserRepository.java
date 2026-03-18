package backend.ms_security.Repositories;

import backend.ms_security.Models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface UserRepository extends MongoRepository<User, String>{

    @Query("{'email': ?0}") // Aqui esta la consulta que vamos a ejecutaar sobre Mongo, el 0 identifica el numero del parametro dentro de la consulta
    public User getUserByEmail(String email);
}
