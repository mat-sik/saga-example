package mat_sik.auth_service.client.rabbit.topology;

import mat_sik.auth_service.client.rabbit.topology.properties.CompensateCreateUserTopologyProperties;
import mat_sik.auth_service.client.rabbit.topology.properties.CreateUserTopologyProperties;
import mat_sik.auth_service.client.rabbit.topology.properties.DeleteUserTopologyProperties;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitTopologyConfiguration {

    @Bean
    public Queue createUserQueue(CreateUserTopologyProperties topologyProperties) {
        return topologyProperties.getQueue();
    }

    @Bean
    public DirectExchange createUserDirectExchange(CreateUserTopologyProperties topologyProperties) {
        return topologyProperties.getDirectExchange();
    }

    @Bean
    public Binding createUserBinding(CreateUserTopologyProperties topologyProperties) {
        return topologyProperties.getBinding();
    }

    @Bean
    public Queue compensateCreateUserQueue(CompensateCreateUserTopologyProperties topologyProperties) {
        return topologyProperties.getQueue();
    }

    @Bean
    public DirectExchange compensateCreateUserDirectExchange(CompensateCreateUserTopologyProperties topologyProperties) {
        return topologyProperties.getDirectExchange();
    }

    @Bean
    public Binding compensateCreateUserBinding(CompensateCreateUserTopologyProperties topologyProperties) {
        return topologyProperties.getBinding();
    }

    @Bean
    public Queue deleteUserQueue(DeleteUserTopologyProperties topologyProperties) {
        return topologyProperties.getQueue();
    }

    @Bean
    public DirectExchange deleteUserDirectExchange(DeleteUserTopologyProperties topologyProperties) {
        return topologyProperties.getDirectExchange();
    }

    @Bean
    public Binding deleteUserBinding(DeleteUserTopologyProperties topologyProperties) {
        return topologyProperties.getBinding();
    }

}
