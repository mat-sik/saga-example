package mat_sik.saga_orchestrator.user.controller.create.compensate;

import org.bson.types.ObjectId;

public record DeleteUserMessage(ObjectId id) {
}
