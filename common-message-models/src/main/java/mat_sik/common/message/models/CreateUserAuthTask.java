package mat_sik.common.message.models;

import org.bson.types.ObjectId;

public record CreateUserAuthTask(
        ObjectId id,
        String username,
        String email,
        String password
) {
}
