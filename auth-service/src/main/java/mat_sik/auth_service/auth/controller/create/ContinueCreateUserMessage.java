package mat_sik.auth_service.auth.controller.create;

import org.bson.types.ObjectId;

public record ContinueCreateUserMessage(ObjectId id, String username, String email, String password) {
}
