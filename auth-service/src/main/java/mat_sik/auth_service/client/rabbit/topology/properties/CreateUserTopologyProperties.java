package mat_sik.auth_service.client.rabbit.topology.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("rabbit.topology.create-user")
public class CreateUserTopologyProperties extends AbstractTopology {
    public CreateUserTopologyProperties(String exchangeName, String queueName, String routingKey) {
        super(exchangeName, queueName, routingKey);
    }
}
