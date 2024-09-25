package mat_sik.saga_orchestrator.client.rabbit.topology;

import mat_sik.saga_orchestrator.client.rabbit.topology.property.AuthDirectExchangeTopologyConfigurationProperty;
import mat_sik.saga_orchestrator.client.rabbit.topology.property.ExchangeConfig;
import mat_sik.saga_orchestrator.client.rabbit.topology.property.OrchestratorDirectExchangeTopologyConfigurationProperty;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitAuthTopologyConfiguration {

    private static final String USER_CREATE_QUEUE_NAME = "create";
    private static final String USER_INITIATE_CREATE_COMPENSATE_TRANSACTION_QUEUE_NAME = "initiate-create-compensate-transaction";
    private static final String USER_DELETE_QUEUE_NAME = "delete";

    private final TopologyBuilder topologyBuilder;

    public RabbitAuthTopologyConfiguration(AuthDirectExchangeTopologyConfigurationProperty topologyConfigurationProperty) {
        ExchangeConfig userExchangeConfig = topologyConfigurationProperty.auth();
        this.topologyBuilder = new TopologyBuilder(userExchangeConfig);
    }

    @Bean
    public DirectExchange authDirectExchange() {
        return topologyBuilder.getDirectExchange();
    }

    @Bean
    public Queue createAuthQueue() {
        return topologyBuilder.getQueue(USER_CREATE_QUEUE_NAME);
    }

    @Bean
    public Binding createAuthBinding() {
        return topologyBuilder.getBinding(USER_CREATE_QUEUE_NAME);
    }

    @Bean
    public Queue initiateCreateAuthCompensateTransactionQueue() {
        return topologyBuilder.getQueue(USER_INITIATE_CREATE_COMPENSATE_TRANSACTION_QUEUE_NAME);
    }

    @Bean
    public Binding initiateCreateAuthCompensateTransactionBinding() {
        return topologyBuilder.getBinding(USER_INITIATE_CREATE_COMPENSATE_TRANSACTION_QUEUE_NAME);
    }

    @Bean
    public Queue deleteAuthQueue() {
        return topologyBuilder.getQueue(USER_DELETE_QUEUE_NAME);
    }

    @Bean
    public Binding deleteAuthBinding() {
        return topologyBuilder.getBinding(USER_DELETE_QUEUE_NAME);
    }

}
