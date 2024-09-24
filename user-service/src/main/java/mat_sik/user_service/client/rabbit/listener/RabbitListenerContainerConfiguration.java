package mat_sik.user_service.client.rabbit.listener;

import mat_sik.user_service.user.controller.create.CreateUserMessageListener;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;

@Configuration
public class RabbitListenerContainerConfiguration {

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleMessageListenerContainer createUserListenerContainer(
            ConnectionFactory factory,
            ExecutorService executorService,
            @Qualifier("createUserQueue") Queue queue,
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

}
