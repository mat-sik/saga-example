package mat_sik.saga_orchestrator.client.rabbit.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import mat_sik.saga_orchestrator.user.controller.create.compensate.DeleteUserMessage;
import mat_sik.saga_orchestrator.user.controller.create.compensate.InitiateCreateUserCompensationTransactionMessage;
import mat_sik.saga_orchestrator.user.controller.create.compensate.InitiateCreateUserCompensationTransactionMessageListener;
import mat_sik.saga_orchestrator.user.controller.create.next.ContinueCreateUserMessage;
import mat_sik.saga_orchestrator.user.controller.create.next.ContinueCreateUserMessageListener;
import mat_sik.saga_orchestrator.user.controller.create.start.CreateUserMessage;
import mat_sik.saga_orchestrator.user.controller.create.start.CreateUserMessageListener;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ExecutorService;

@Configuration
public class RabbitListenerContainerConfiguration {

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        var converter = new Jackson2JsonMessageConverter(new ObjectMapper());
        var mapper = new DefaultJackson2JavaTypeMapper();
        mapper.setIdClassMapping(
                Map.of(
                        "CreateUserMessage", CreateUserMessage.class,
                        "ContinueCreateUserMessage", ContinueCreateUserMessage.class,
                        "InitiateCreateUserCompensationTransactionMessage", InitiateCreateUserCompensationTransactionMessage.class,
                        "DeleteUserMessage", DeleteUserMessage.class
                )
        );
        mapper.setTrustedPackages("*");
        converter.setClassMapper(mapper);
        return converter;
    }

    @Bean
    public SimpleMessageListenerContainer createUserListenerContainer(
            ConnectionFactory factory,
            ExecutorService executorService,
            @Qualifier("orchestratorCreateUserQueue") Queue queue,
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
    public SimpleMessageListenerContainer continueCreateUserListenerContainer(
            ConnectionFactory factory,
            ExecutorService executorService,
            @Qualifier("createUserContinueTransactionQueue") Queue queue,
            ContinueCreateUserMessageListener messageListener
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
    public SimpleMessageListenerContainer compensateCreateUserListenerContainer(
            ConnectionFactory factory,
            ExecutorService executorService,
            @Qualifier("initiateCreateAuthCompensateTransactionQueue") Queue queue,
            InitiateCreateUserCompensationTransactionMessageListener messageListener
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
