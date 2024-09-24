package mat_sik.auth_service.client.rabbit.topology.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("rabbit.topology.compensate-create-user")
public class CompensateCreateUserTopologyProperties extends AbstractTopology {
    public CompensateCreateUserTopologyProperties(String exchangeName, String queueName, String routingKey) {
        super(exchangeName, queueName, routingKey);
    }
}
