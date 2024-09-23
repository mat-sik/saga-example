package mat_sik.user_service.client.mongo;

import com.mongodb.MongoCredential;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;

@Configuration
public class MongoClientConfiguration {

    @Bean
    public MongoClientFactoryBean mongoClientFactoryBean(MongoClientProperties mongoClientProperties) {
        var factory = new MongoClientFactoryBean();

        String host = mongoClientProperties.host();
        factory.setHost(host);
        int port = mongoClientProperties.port();
        factory.setPort(port);

        MongoCredential[] credentials = getMongoCredentials(mongoClientProperties);
        factory.setCredential(credentials);

        return factory;
    }

    private static MongoCredential[] getMongoCredentials(MongoClientProperties mongoClientProperties) {
        String login = mongoClientProperties.login();
        String database = mongoClientProperties.database();
        char[] password = mongoClientProperties.password();

        MongoCredential credential = MongoCredential.createCredential(login, database, password);

        return new MongoCredential[]{credential};
    }

}
