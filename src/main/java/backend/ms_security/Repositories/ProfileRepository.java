package backend.ms_security.Repositories;

import backend.ms_security.Models.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ProfileRepository extends MongoRepository<Profile, String> {

    @Query("{ 'user.$id' : ObjectId(?0) }")
    public Profile findProfileByUserID(String user_id);
}
