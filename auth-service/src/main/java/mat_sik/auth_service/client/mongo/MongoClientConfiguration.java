package mat_sik.auth_service.client.mongo;

import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

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
        String database = mongoClientProperties.authDatabase();
        char[] password = mongoClientProperties.password();

        MongoCredential credential = MongoCredential.createCredential(login, database, password);

        return new MongoCredential[]{credential};
    }

    @Bean
    public MongoDatabaseFactory mongoDatabaseFactory(
            MongoClient mongoClient,
            MongoClientProperties mongoClientProperties
    ) {
        String database = mongoClientProperties.database();
        return new SimpleMongoClientDatabaseFactory(mongoClient, database);
    }

}
