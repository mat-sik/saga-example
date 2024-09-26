package mat_sik.auth_service.client.rabbit.topology;

import mat_sik.auth_service.client.rabbit.topology.properties.EventDirectExchangeConfigurationProperties;
import mat_sik.auth_service.client.rabbit.topology.properties.TaskDirectExchangeConfigurationProperties;
import mat_sik.auth_service.client.rabbit.topology.properties.UserAuthCreationQueueConfigurationProperties;
import mat_sik.auth_service.client.rabbit.topology.properties.UserCreationCompensationQueueConfigurationProperties;
import mat_sik.auth_service.client.rabbit.topology.properties.types.ExchangeConfig;
import mat_sik.auth_service.client.rabbit.topology.properties.types.QueueConfig;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitTopologyConfiguration {

    @Bean
    public DirectExchange taskDirectExchange(TaskDirectExchangeConfigurationProperties configurationProperties) {
        ExchangeConfig config = configurationProperties.task();
        return TopologyBuilder.buildDirectExchange(config);
    }

    @Bean
    public DirectExchange eventDirectExchange(EventDirectExchangeConfigurationProperties configurationProperties) {
        ExchangeConfig config = configurationProperties.event();
        return TopologyBuilder.buildDirectExchange(config);
    }

    @Bean
    public Queue userAuthCreationQueue(UserAuthCreationQueueConfigurationProperties configurationProperties) {
        QueueConfig config = configurationProperties.userAuthCreation();
        return TopologyBuilder.buildQuorumQueue(config);
    }

    @Bean
    public Binding userAuthCreationBinding(
            @Qualifier("taskDirectExchange") DirectExchange taskDirectExchange,
            @Qualifier("userAuthCreationQueue") Queue userAuthCreationQueue,
            UserAuthCreationQueueConfigurationProperties configurationProperties
    ) {
        QueueConfig config = configurationProperties.userAuthCreation();
        return TopologyBuilder.buildBinding(userAuthCreationQueue, taskDirectExchange, config);
    }

    @Bean
    public Queue userCreationCompensationQueue(UserCreationCompensationQueueConfigurationProperties configurationProperties) {
        QueueConfig config = configurationProperties.userCreationCompensation();
        return TopologyBuilder.buildQuorumQueue(config);
    }

    @Bean
    public Binding userCreationCompensationBinding(
            @Qualifier("eventDirectExchange") DirectExchange eventDirectExchange,
            @Qualifier("userCreationCompensationQueue") Queue userCreationCompensationQueue,
            UserCreationCompensationQueueConfigurationProperties configurationProperties
    ) {
        QueueConfig config = configurationProperties.userCreationCompensation();
        return TopologyBuilder.buildBinding(userCreationCompensationQueue, eventDirectExchange, config);
    }

}