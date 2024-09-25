package mat_sik.saga_orchestrator.user.controller.create.start;

public record CreateUserMessage(
        String id,
        String firstname,
        String lastname,
        String username,
        String email,
        String password
) {
}
