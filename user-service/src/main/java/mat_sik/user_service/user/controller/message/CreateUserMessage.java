package mat_sik.user_service.user.controller.message;

import org.bson.types.ObjectId;

public record CreateUserMessage(ObjectId id, String firstname, String lastname) {
}
