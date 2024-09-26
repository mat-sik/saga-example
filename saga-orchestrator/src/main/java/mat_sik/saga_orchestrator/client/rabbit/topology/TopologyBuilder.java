package mat_sik.saga_orchestrator.client.rabbit.topology;

import mat_sik.saga_orchestrator.client.rabbit.topology.properties.types.ExchangeConfig;
import mat_sik.saga_orchestrator.client.rabbit.topology.properties.types.QueueConfig;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;

public class TopologyBuilder {

    public static DirectExchange buildDirectExchange(ExchangeConfig config) {
        String exchangeName = config.name();
        return buildDirectExchange(exchangeName);
    }

    public static Queue buildQuorumQueue(QueueConfig config) {
        String queueName = config.name();
        return buildQuorumQueue(queueName);
    }

    public static Binding buildBinding(Queue queue, DirectExchange exchange, QueueConfig config) {
        String routingKey = config.routingKey();
        return buildBinding(queue, exchange, routingKey);
    }

    private static DirectExchange buildDirectExchange(String exchangeName) {
        return ExchangeBuilder
                .directExchange(exchangeName)
                .durable(true)
                .build();
    }

    private static Queue buildQuorumQueue(String queueName) {
        return QueueBuilder
                .durable(queueName)
                .quorum()
                .build();
    }

    private static Binding buildBinding(Queue queue, DirectExchange exchange, String routingKey) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }

}
