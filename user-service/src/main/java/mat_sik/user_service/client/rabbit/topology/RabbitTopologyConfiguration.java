package mat_sik.user_service.client.rabbit.topology;

import mat_sik.user_service.client.rabbit.topology.properties.EventDirectExchangeConfigurationProperties;
import mat_sik.user_service.client.rabbit.topology.properties.TaskDirectExchangeConfigurationProperties;
import mat_sik.user_service.client.rabbit.topology.properties.UserCreationQueueConfigurationProperties;
import mat_sik.user_service.client.rabbit.topology.properties.UserDeletionQueueConfigurationProperties;
import mat_sik.user_service.client.rabbit.topology.properties.types.ExchangeConfig;
import mat_sik.user_service.client.rabbit.topology.properties.types.QueueConfig;
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
    public Queue userCreationQueue(UserCreationQueueConfigurationProperties configurationProperties) {
        QueueConfig config = configurationProperties.userCreation();
        return TopologyBuilder.buildQuorumQueue(config);
    }

    @Bean
    public Binding userCreationBinding(
            @Qualifier("taskDirectExchange") DirectExchange taskDirectExchange,
            @Qualifier("userCreationQueue") Queue userCreationQueue,
            UserCreationQueueConfigurationProperties configurationProperties
    ) {
        QueueConfig config = configurationProperties.userCreation();
        return TopologyBuilder.buildBinding(userCreationQueue, taskDirectExchange, config);
    }

    @Bean
    public Queue userDeletionQueue(UserDeletionQueueConfigurationProperties configurationProperties) {
        QueueConfig config = configurationProperties.userDeletion();
        return TopologyBuilder.buildQuorumQueue(config);
    }

    @Bean
    public Binding userDeletionBinding(
            @Qualifier("taskDirectExchange") DirectExchange taskDirectExchange,
            @Qualifier("userDeletionQueue") Queue userDeletionQueue,
            UserDeletionQueueConfigurationProperties configurationProperties
    ) {
        QueueConfig config = configurationProperties.userDeletion();
        return TopologyBuilder.buildBinding(userDeletionQueue, taskDirectExchange, config);
    }

}