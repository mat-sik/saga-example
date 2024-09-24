package mat_sik.user_service.user.controller.create;

import org.bson.types.ObjectId;

public record ContinueCreateUserMessage(ObjectId id, String username, String email, String password) {
}
