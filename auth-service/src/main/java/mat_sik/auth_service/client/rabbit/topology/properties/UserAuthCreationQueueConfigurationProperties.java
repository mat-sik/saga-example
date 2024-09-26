package mat_sik.auth_service.client.rabbit.topology.properties;

import mat_sik.auth_service.client.rabbit.topology.properties.types.QueueConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("rabbit.topology.queues")
public record UserAuthCreationQueueConfigurationProperties(QueueConfig userAuthCreation) {
}
