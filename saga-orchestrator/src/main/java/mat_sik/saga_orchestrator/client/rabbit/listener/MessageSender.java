package mat_sik.saga_orchestrator.client.rabbit.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;

@RequiredArgsConstructor
public class MessageSender {

    private final RabbitTemplate template;
    private final MessageConverter converter;

    public <T> void send(Binding binding, T message) {
        template.setMessageConverter(converter);
        template.setExchange(binding.getExchange());
        template.setRoutingKey(binding.getRoutingKey());
        template.convertAndSend(message);
    }

}
