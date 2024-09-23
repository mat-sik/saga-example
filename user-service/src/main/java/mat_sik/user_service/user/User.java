package mat_sik.user_service.user;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public record User(
        @Id ObjectId id,
        String firstname,
        String lastname
) {
}
