package mat_sik.saga_orchestrator.user.controller.create.next;

public record ContinueCreateUserMessage(String id, String username, String email, String password) {
}
