package mat_sik.auth_service.client.rabbit.listener;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import mat_sik.auth_service.auth.controller.create.ContinueCreateUserMessage;
import mat_sik.auth_service.auth.controller.create.CreateUserMessageListener;
import mat_sik.auth_service.auth.controller.create.InitiateCreateUserCompensationTransactionMessage;
import mat_sik.auth_service.auth.controller.delete.DeleteUserMessage;
import mat_sik.auth_service.auth.controller.delete.DeleteUserMessageListener;
import org.bson.types.ObjectId;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@Configuration
public class RabbitListenerContainerConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        var objectMapper = new ObjectMapper();
        var simpleModule = new SimpleModule();
        simpleModule.addSerializer(ObjectId.class, new ToStringSerializer());
        simpleModule.addDeserializer(ObjectId.class, new ObjectIdDeserializer());

        objectMapper.registerModule(simpleModule);
        return objectMapper;
    }

    private static class ObjectIdDeserializer extends JsonDeserializer<ObjectId> {
        @Override
        public ObjectId deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            String hex = jsonParser.getText();
            return new ObjectId(hex);
        }
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        var converter = new Jackson2JsonMessageConverter(objectMapper());
        var mapper = new DefaultJackson2JavaTypeMapper();
        mapper.setIdClassMapping(
                Map.of(
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

    @Bean
    public SimpleMessageListenerContainer deleteUserListenerContainer(
            ConnectionFactory factory,
            ExecutorService executorService,
            @Qualifier("deleteUserQueue") Queue queue,
            DeleteUserMessageListener messageListener
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
