package mat_sik.saga_orchestrator.client.rabbit.topology;

import mat_sik.saga_orchestrator.client.rabbit.topology.property.ExchangeConfig;
import mat_sik.saga_orchestrator.client.rabbit.topology.property.OrchestratorDirectExchangeTopologyConfigurationProperty;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitTopologyConfiguration {

    private static final String USER_CREATE_QUEUE_NAME = "create-user";

    private final TopologyBuilder topologyBuilder;

    public RabbitTopologyConfiguration(OrchestratorDirectExchangeTopologyConfigurationProperty topologyConfigurationProperty) {
        ExchangeConfig userExchangeConfig = topologyConfigurationProperty.orchestrator();
        this.topologyBuilder = new TopologyBuilder(userExchangeConfig);
    }

    @Bean
    public DirectExchange orchestratorDirectExchange() {
        return topologyBuilder.getDirectExchange();
    }

    @Bean
    public Queue orchestratorCreateUserQueue() {
        return topologyBuilder.getQueue(USER_CREATE_QUEUE_NAME);
    }

    @Bean
    public Binding orchestratorCreateUserBinding() {
        return topologyBuilder.getBinding(USER_CREATE_QUEUE_NAME);

    }

}