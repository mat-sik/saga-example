package mat_sik.saga_orchestrator.client.rabbit.listener;

import mat_sik.saga_orchestrator.user.listener.CreateInTransactionUserTaskListener;
import mat_sik.saga_orchestrator.user.listener.UserAuthCreationFailedEventListener;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;

@Configuration
public class RabbitListenerContainerConfiguration {

    @Bean
    public SimpleMessageListenerContainer CreateInTransactionUserTaskListenerContainer(
            ConnectionFactory factory,
            ExecutorService executorService,
            @Qualifier("userTransactionalCreationQueue") Queue queue,
            CreateInTransactionUserTaskListener messageListener
    ) {
        var container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(factory);
        container.setTaskExecutor(executorService);
        container.setQueues(queue);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setMessageListener(messageListener);
        return container;
    }

    @Bean
    public SimpleMessageListenerContainer UserAuthCreationFailedEventListenerContainer(
            ConnectionFactory factory,
            ExecutorService executorService,
            @Qualifier("userCreationCompensationQueue") Queue queue,
            UserAuthCreationFailedEventListener messageListener
    ) {
        var container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(factory);
        container.setTaskExecutor(executorService);
        container.setQueues(queue);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setMessageListener(messageListener);
        return container;
    }
}
