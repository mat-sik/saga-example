package mat_sik.common.message.models;

import org.bson.types.ObjectId;

public record CreateUserTask(
        ObjectId id,
        String firstname,
        String lastname
) {
}
