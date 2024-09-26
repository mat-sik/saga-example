package mat_sik.common.message.models;

import org.bson.types.ObjectId;

public record CreateInTransactionUserTask(
        ObjectId id,
        String firstname,
        String lastname,
        String username,
        String email,
        String password
) {
}
