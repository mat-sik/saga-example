package mat_sik.user_service.client.rabbit.topology;

import mat_sik.user_service.client.rabbit.topology.property.ExchangeConfig;
import mat_sik.user_service.client.rabbit.topology.property.UserDirectExchangeTopologyConfigurationProperty;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitTopologyConfiguration {

    private static final String USER_CREATE_QUEUE_NAME = "create";
    private static final String USER_CREATE_CONTINUE_TRANSACTION_QUEUE_NAME = "create-continue-transaction";
    private static final String USER_DELETE_QUEUE_NAME = "delete";

    private final TopologyBuilder topologyBuilder;

    public RabbitTopologyConfiguration(UserDirectExchangeTopologyConfigurationProperty topologyConfigurationProperty) {
        ExchangeConfig userExchangeConfig = topologyConfigurationProperty.user();
        this.topologyBuilder = new TopologyBuilder(userExchangeConfig);
    }

    @Bean
    public DirectExchange userDirectExchange() {
        return topologyBuilder.getUserDirectExchange();
    }

    @Bean
    public Queue createUserQueue() {
        return topologyBuilder.getQueue(USER_CREATE_QUEUE_NAME);
    }

    @Bean
    public Binding createUserBinding() {
        return topologyBuilder.getBinding(USER_CREATE_QUEUE_NAME);
    }

    @Bean
    public Queue createUserContinueTransactionQueue() {
        return topologyBuilder.getQueue(USER_CREATE_CONTINUE_TRANSACTION_QUEUE_NAME);
    }

    @Bean
    public Binding createUserContinueTransactionBinding() {
        return topologyBuilder.getBinding(USER_CREATE_CONTINUE_TRANSACTION_QUEUE_NAME);
    }

    @Bean
    public Queue deleteUserQueue() {
        return topologyBuilder.getQueue(USER_DELETE_QUEUE_NAME);
    }

    @Bean
    public Binding userDeleteBinding() {
        return topologyBuilder.getBinding(USER_DELETE_QUEUE_NAME);
    }

}
