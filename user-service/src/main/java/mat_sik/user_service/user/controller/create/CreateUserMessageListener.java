package mat_sik.user_service.user.controller.create;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class CreateUserMessageListener implements ChannelAwareMessageListener {

    private static final boolean MULTIPLE_ACK = false;

    private final UserService service;
    private final Jackson2JsonMessageConverter converter;

    @Override
    public void onMessage(Message message, Channel channel) throws IOException {
        MessageProperties messageProperties = message.getMessageProperties();
        long deliveryTag = messageProperties.getDeliveryTag();

        var createUserMessage = (CreateUserMessage) converter.fromMessage(message, CreateUserMessage.class);

        ObjectId id = createUserMessage.id();
        String firstname = createUserMessage.firstname();
        String lastname = createUserMessage.lastname();

        service.createUser(new User(id, firstname, lastname));

        channel.basicAck(deliveryTag, MULTIPLE_ACK);
    }

}
