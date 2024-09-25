package mat_sik.common.message.models;

import org.bson.types.ObjectId;

public record ContinueCreateUserMessage(
        ObjectId id,
        String username,
        String email,
        String password
) {
}
