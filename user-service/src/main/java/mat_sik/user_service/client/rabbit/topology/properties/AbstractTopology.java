package mat_sik.user_service.client.rabbit.topology.properties;

import lombok.Getter;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;

@Getter
public abstract class AbstractTopology {

    private final DirectExchange directExchange;
    private final Queue queue;
    private final Binding binding;

    public AbstractTopology(String exchangeName, String queueName, String routingKey) {
        this.directExchange = buildExchange(exchangeName);
        this.queue = buildQueue(queueName);
        this.binding = buildBinding(queue, directExchange, routingKey);
    }

    private static DirectExchange buildExchange(String exchangeName) {
        return ExchangeBuilder
                .directExchange(exchangeName)
                .durable(true)
                .build();
    }

    private static Queue buildQueue(String queueName) {
        return QueueBuilder
                .durable(queueName)
                .quorum()
                .build();
    }

    private static Binding buildBinding(Queue queue, DirectExchange exchange, String routingKey) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }
}
