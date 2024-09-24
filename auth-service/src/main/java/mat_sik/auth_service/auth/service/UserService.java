package mat_sik.auth_service.auth.service;

import lombok.RequiredArgsConstructor;
import mat_sik.auth_service.auth.model.User;
import mat_sik.auth_service.auth.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    // throws DuplicateKeyException if unique constraint gets violated[.
    public User save(User user) {
        return repository.save(user);
    }

    public void deleteById(ObjectId id) {
        repository.deleteById(id);
    }

}
