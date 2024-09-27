package mat_sik.saga_orchestrator.client.rabbit;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class RabbitClientConfiguration {

    @Bean
    public ExecutorService virtualExecutorService() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @Bean
    public TaskExecutor virtualThreadTaskExecutor(ExecutorService virtualExecutorService) {
        return new TaskExecutorAdapter(virtualExecutorService);
    }

    @Bean
    public CachingConnectionFactory cachingConnectionFactory(
            RabbitClientProperties properties,
            ExecutorService executorService
    ) {
        var factory = new CachingConnectionFactory();

        String user = properties.user();
        factory.setUsername(user);

        String password = properties.password();
        factory.setPassword(password);

        String virtualHost = properties.virtualHost();
        factory.setVirtualHost(virtualHost);

        String addresses = properties.nodeAddresses();
        factory.setAddresses(addresses);

        factory.setExecutor(executorService);

        factory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);

        return factory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory factory) {
        return new RabbitAdmin(factory);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory factory, Jackson2JsonMessageConverter converter) {
        var template = new RabbitTemplate(factory);
        template.setMessageConverter(converter);
        return template;
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

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
}
