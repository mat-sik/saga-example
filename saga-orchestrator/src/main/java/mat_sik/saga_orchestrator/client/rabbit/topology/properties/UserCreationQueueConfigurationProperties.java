package mat_sik.saga_orchestrator.client.rabbit.topology.properties;

import mat_sik.saga_orchestrator.client.rabbit.topology.properties.types.QueueConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("rabbit.topology.queues")
public record UserCreationQueueConfigurationProperties(QueueConfig userCreation) {
}
