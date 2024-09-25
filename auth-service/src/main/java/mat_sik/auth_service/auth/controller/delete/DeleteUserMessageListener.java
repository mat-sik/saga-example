package mat_sik.auth_service.auth.controller.delete;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import mat_sik.auth_service.auth.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteUserMessageListener implements ChannelAwareMessageListener {

    private static final boolean MULTIPLE_ACK = false;

    private final UserService service;
    private final Jackson2JsonMessageConverter converter;

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        MessageProperties messageProperties = message.getMessageProperties();
        long deliveryTag = messageProperties.getDeliveryTag();

        var deleteUserMessage = (DeleteUserMessage) converter.fromMessage(message, DeleteUserMessage.class);

        ObjectId id = deleteUserMessage.id();

        service.deleteById(id);

        channel.basicAck(deliveryTag, MULTIPLE_ACK);
    }
}
