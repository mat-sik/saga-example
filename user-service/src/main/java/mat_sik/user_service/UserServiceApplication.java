package mat_sik.user_service;

import mat_sik.user_service.user.controller.message.CreateUserMessage;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableMongoRepositories
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(RabbitTemplate template, Jackson2JsonMessageConverter converter) {
        return _ -> {
            CreateUserMessage message = new CreateUserMessage(
                    new ObjectId("64a30e756779b3416c4eaf5b"),
                    "John",
                    "Doe"
            );

            template.setExchange("exchange-create-user");
            template.setMessageConverter(converter);

            template.convertAndSend("user.new", message);
        };
    }

}
