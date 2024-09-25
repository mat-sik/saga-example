package mat_sik.saga_orchestrator.client.rabbit;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("rabbit")
public record RabbitClientProperties(String user, String password, String virtualHost, String nodeAddresses) {
}
