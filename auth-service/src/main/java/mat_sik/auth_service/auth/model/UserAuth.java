package mat_sik.auth_service.auth.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("users")
public record UserAuth(
        @Id ObjectId id,
        @Indexed(unique = true) String email,
        @Indexed(unique = true) String username,
        String password
) {
}
