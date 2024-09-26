package mat_sik.user_service.client.rabbit.topology.properties;

import mat_sik.user_service.client.rabbit.topology.properties.types.QueueConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("rabbit.topology.queues")
public record UserDeletionQueueConfigurationProperties(QueueConfig userDeletion) {
}
