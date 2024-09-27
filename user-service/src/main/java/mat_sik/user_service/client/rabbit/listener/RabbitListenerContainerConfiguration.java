package mat_sik.user_service.client.rabbit.listener;

import mat_sik.user_service.user.listener.CreateUserMessageListener;
import mat_sik.user_service.user.listener.DeleteUserTaskListener;
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
    public SimpleMessageListenerContainer CreateUserMessageListenerContainer(
            ConnectionFactory factory,
            ExecutorService executorService,
            @Qualifier("userCreationQueue") Queue queue,
            CreateUserMessageListener messageListener
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
    public SimpleMessageListenerContainer DeleteUserTaskListenerContainer(
            ConnectionFactory factory,
            ExecutorService executorService,
            @Qualifier("userDeletionQueue") Queue queue,
            DeleteUserTaskListener messageListener
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
