package mat_sik.auth_service.client.rabbit;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class RabbitClientConfiguration {

    @Bean
    public ExecutorService virtualExecutorService() {
        return Executors.newVirtualThreadPerTaskExecutor();
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

        return factory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory factory) {
        return new RabbitAdmin(factory);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory factory) {
        return new RabbitTemplate(factory);
    }

}
