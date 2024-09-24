package mat_sik.user_service.client.rabbit.topology.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("rabbit.topology.continue-transaction-create-user")
public class ContinueTransactionCreateUserTopologyProperties extends AbstractTopology {
    public ContinueTransactionCreateUserTopologyProperties(String exchangeName, String queueName, String routingKey) {
        super(exchangeName, queueName, routingKey);
    }
}
