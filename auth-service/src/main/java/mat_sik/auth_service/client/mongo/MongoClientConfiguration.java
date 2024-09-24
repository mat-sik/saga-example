package mat_sik.auth_service.client.mongo;

import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import mat_sik.auth_service.auth.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;

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

    @Bean
    public MongoTemplate mongoTemplate(MongoDatabaseFactory factory) {
        MongoTemplate mongoTemplate = new MongoTemplate(factory);
        createIndexes(mongoTemplate);
        return mongoTemplate;
    }

    private static void createIndexes(MongoTemplate mongoTemplate) {
        MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext = mongoTemplate
                .getConverter().getMappingContext();

        IndexResolver resolver = new MongoPersistentEntityIndexResolver(mappingContext);

        IndexOperations indexOps = mongoTemplate.indexOps(User.class);
        resolver.resolveIndexFor(User.class).forEach(indexOps::ensureIndex);
    }

}
