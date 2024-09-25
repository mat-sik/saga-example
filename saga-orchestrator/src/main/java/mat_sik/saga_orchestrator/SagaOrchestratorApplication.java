package mat_sik.saga_orchestrator;

import mat_sik.common.message.models.CreateUserMessage;
import org.bson.types.ObjectId;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SagaOrchestratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(SagaOrchestratorApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(
            RabbitTemplate template,
            Jackson2JsonMessageConverter converter,
            @Qualifier("orchestratorCreateUserBinding") Binding binding
    ) {
        return _ -> {
            template.setMessageConverter(converter);
            template.setExchange(binding.getExchange());
            template.setRoutingKey(binding.getRoutingKey());

            ObjectId id = ObjectId.get();
            var message = new CreateUserMessage(
                    id,
                    "firstname",
                    "lastname",
                    "username",
                    "email",
                    "password"
            );

            template.convertAndSend(message);
        };
    }

}
