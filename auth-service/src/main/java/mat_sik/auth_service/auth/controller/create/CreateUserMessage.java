package mat_sik.auth_service.auth.controller.create;

import org.bson.types.ObjectId;

public record CreateUserMessage(ObjectId id, String username, String email, String password) {
}
