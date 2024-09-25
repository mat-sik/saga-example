package mat_sik.saga_orchestrator.client.rabbit.topology;

import lombok.Getter;
import mat_sik.saga_orchestrator.client.rabbit.topology.property.ExchangeConfig;
import mat_sik.saga_orchestrator.client.rabbit.topology.property.QueueConfig;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;

import java.util.HashMap;
import java.util.Map;

public class TopologyBuilder {

    @Getter
    private final DirectExchange directExchange;

    private final Map<String, Queue> queues;
    private final Map<String, Binding> bindings;

    public TopologyBuilder(ExchangeConfig exchangeConfig) {
        String exchangeName = exchangeConfig.name();
        this.directExchange = buildDirectExchange(exchangeName);

        this.queues = new HashMap<>();
        this.bindings = new HashMap<>();

        Map<String, QueueConfig> queueConfigs = exchangeConfig.queues();
        queueConfigs.forEach((name, config) -> {
            String queueName = config.name();
            Queue queue = buildQuorumQueue(queueName);
            queues.put(name, queue);

            String routingKey = config.routingKey();
            Binding binding = buildBinding(queue, directExchange, routingKey);
            bindings.put(name, binding);
        });
    }

    public Queue getQueue(String name) {
        return queues.get(name);
    }

    public Binding getBinding(String name) {
        return bindings.get(name);
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
