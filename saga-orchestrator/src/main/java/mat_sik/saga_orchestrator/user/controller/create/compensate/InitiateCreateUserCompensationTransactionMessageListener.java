package mat_sik.saga_orchestrator.user.controller.create.compensate;

import com.rabbitmq.client.Channel;
import lombok.extern.java.Log;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Log
public class InitiateCreateUserCompensationTransactionMessageListener implements ChannelAwareMessageListener {

    private static final boolean MULTIPLE_ACK = false;
    private static final boolean REQUEUE = true;

    private final Jackson2JsonMessageConverter converter;
    private final RabbitTemplate template;
    private final Binding createUserBinding;

    public InitiateCreateUserCompensationTransactionMessageListener(
            Jackson2JsonMessageConverter converter,
            RabbitTemplate template,
            @Qualifier("userDeleteBinding") Binding createUserBinding
    ) {
        this.converter = converter;
        this.template = template;
        this.createUserBinding = createUserBinding;
    }

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        MessageProperties messageProperties = message.getMessageProperties();
        long deliveryTag = messageProperties.getDeliveryTag();

        var compensationTransactionMessage = (InitiateCreateUserCompensationTransactionMessage) converter.fromMessage(
                message,
                InitiateCreateUserCompensationTransactionMessage.class
        );

        try {
            sendMessage(compensationTransactionMessage);
            channel.basicAck(deliveryTag, MULTIPLE_ACK);
        } catch (Exception ex) {
            channel.basicNack(deliveryTag, MULTIPLE_ACK, REQUEUE);
            log.severe(ex.getMessage());
        }
    }

    private void sendMessage(InitiateCreateUserCompensationTransactionMessage compensationTransactionMessage) {
        String hex = compensationTransactionMessage.id();
        var message = new DeleteUserMessage(hex);

        template.setMessageConverter(converter);
        template.setExchange(createUserBinding.getExchange());
        template.setRoutingKey(createUserBinding.getRoutingKey());
        template.convertAndSend(message);
    }
}
