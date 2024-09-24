package mat_sik.user_service.user.service;

import lombok.RequiredArgsConstructor;
import mat_sik.user_service.user.model.User;
import mat_sik.user_service.user.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public User save(User user) {
        return repository.save(user);
    }

    public void deleteById(ObjectId id) {
        repository.deleteById(id);
    }
}
