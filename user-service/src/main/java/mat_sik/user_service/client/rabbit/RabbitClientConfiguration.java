package mat_sik.user_service.client.rabbit;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
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
}
