package mat_sik.user_service.user.listener;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import mat_sik.common.message.models.CreateUserTask;
import mat_sik.user_service.user.model.User;
import mat_sik.user_service.user.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Log
@RequiredArgsConstructor
public class CreateUserMessageListener implements ChannelAwareMessageListener {

    private static final boolean MULTIPLE_ACK = false;
    private static final boolean REQUEUE = true;

    private final UserService service;
    private final Jackson2JsonMessageConverter converter;

    @Override
    public void onMessage(Message message, Channel channel) throws IOException {
        MessageProperties messageProperties = message.getMessageProperties();
        long deliveryTag = messageProperties.getDeliveryTag();

        var createUserMessage = (CreateUserTask) converter.fromMessage(message, CreateUserTask.class);

        try {
            performLocalTransaction(createUserMessage);
            channel.basicAck(deliveryTag, MULTIPLE_ACK);
        } catch (Exception ex) {
            channel.basicNack(deliveryTag, MULTIPLE_ACK, REQUEUE);
            log.severe(ex.getMessage());
        }
    }

    public void performLocalTransaction(CreateUserTask createUserMessage) {
        ObjectId id = createUserMessage.id();
        String firstname = createUserMessage.firstname();
        String lastname = createUserMessage.lastname();

        service.save(new User(id, firstname, lastname));
    }

}
