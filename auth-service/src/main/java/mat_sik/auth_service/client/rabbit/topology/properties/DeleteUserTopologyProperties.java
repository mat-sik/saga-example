package mat_sik.auth_service.client.rabbit.topology.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("rabbit.topology.delete-user")
public class DeleteUserTopologyProperties extends AbstractTopology {
    public DeleteUserTopologyProperties(String exchangeName, String queueName, String routingKey) {
        super(exchangeName, queueName, routingKey);
    }
}
