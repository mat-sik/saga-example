package mat_sik.saga_orchestrator.client.rabbit.topology;

import mat_sik.saga_orchestrator.client.rabbit.topology.properties.EventDirectExchangeConfigurationProperties;
import mat_sik.saga_orchestrator.client.rabbit.topology.properties.TaskDirectExchangeConfigurationProperties;
import mat_sik.saga_orchestrator.client.rabbit.topology.properties.UserAuthCreationQueueConfigurationProperties;
import mat_sik.saga_orchestrator.client.rabbit.topology.properties.UserCreationCompensationQueueConfigurationProperties;
import mat_sik.saga_orchestrator.client.rabbit.topology.properties.UserCreationQueueConfigurationProperties;
import mat_sik.saga_orchestrator.client.rabbit.topology.properties.UserDeletionQueueConfigurationProperties;
import mat_sik.saga_orchestrator.client.rabbit.topology.properties.UserTransactionalCreationQueueConfigurationProperties;
import mat_sik.saga_orchestrator.client.rabbit.topology.properties.types.ExchangeConfig;
import mat_sik.saga_orchestrator.client.rabbit.topology.properties.types.QueueConfig;
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

    @Bean
    public Queue userTransactionalCreationQueue(UserTransactionalCreationQueueConfigurationProperties configurationProperties) {
        QueueConfig config = configurationProperties.userTransactionalCreation();
        return TopologyBuilder.buildQuorumQueue(config);
    }

    @Bean
    public Binding userTransactionalCreationBinding(
            @Qualifier("taskDirectExchange") DirectExchange taskDirectExchange,
            @Qualifier("userTransactionalCreationQueue") Queue userTransactionalCreationQueue,
            UserTransactionalCreationQueueConfigurationProperties configurationProperties
    ) {
        QueueConfig config = configurationProperties.userTransactionalCreation();
        return TopologyBuilder.buildBinding(userTransactionalCreationQueue, taskDirectExchange, config);
    }

}