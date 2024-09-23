package mat_sik.user_service;

import com.mongodb.client.MongoClient;
import mat_sik.user_service.user.User;
import mat_sik.user_service.user.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Optional;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableMongoRepositories
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(MongoClient client, UserRepository repository) {
        return _ -> {
            client.listDatabaseNames().forEach(System.out::println);

            ObjectId id = ObjectId.get();
            User newUser = new User(id, "name", "surname");

            repository.save(newUser);

            Optional<User> opUser = repository.findById(id);
            opUser.ifPresentOrElse(
                    user -> System.out.println("User: " + user + " was found"),
                    () -> System.out.println("User was not found")
            );
        };
    }

}
