package mat_sik.saga_orchestrator;

import mat_sik.common.message.models.CreateInTransactionUserTask;
import org.bson.types.ObjectId;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
            Binding userTransactionalCreationBinding
    ) {
        return _ -> {
            template.setExchange(userTransactionalCreationBinding.getExchange());
            template.setRoutingKey(userTransactionalCreationBinding.getRoutingKey());

            ObjectId id = ObjectId.get();
            var message = new CreateInTransactionUserTask(
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
