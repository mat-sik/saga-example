package mat_sik.user_service.user.controller.create;

public record CreateUserMessage(
        String id,
        String firstname,
        String lastname,
        String username,
        String email,
        String password
) {
}
