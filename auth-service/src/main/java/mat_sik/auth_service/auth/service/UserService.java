package mat_sik.auth_service.auth.service;

import lombok.RequiredArgsConstructor;
import mat_sik.auth_service.auth.model.UserAuth;
import mat_sik.auth_service.auth.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    // throws DuplicateKeyException if unique constraint gets violated[.
    public UserAuth save(UserAuth userAuth) {
        return repository.save(userAuth);
    }

}
