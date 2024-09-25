package mat_sik.saga_orchestrator.user.controller.create.start;

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
