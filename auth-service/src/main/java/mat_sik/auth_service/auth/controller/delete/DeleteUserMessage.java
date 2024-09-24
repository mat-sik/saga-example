package mat_sik.auth_service.auth.controller.delete;

import org.bson.types.ObjectId;

public record DeleteUserMessage(ObjectId id) {
}
