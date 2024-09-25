package mat_sik.user_service.user.controller.create;

import org.bson.types.ObjectId;

public record CreateUserMessage(
        ObjectId id,
        String firstname,
        String lastname,
        String username,
        String email,
        String password
) {
}
