package mat_sik.auth_service.auth.repository;

import mat_sik.auth_service.auth.model.UserAuth;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<UserAuth, ObjectId> {
}
