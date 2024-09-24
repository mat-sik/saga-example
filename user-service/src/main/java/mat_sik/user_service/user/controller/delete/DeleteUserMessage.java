package mat_sik.user_service.user.controller.delete;

import org.bson.types.ObjectId;

public record DeleteUserMessage(ObjectId id) {
}
