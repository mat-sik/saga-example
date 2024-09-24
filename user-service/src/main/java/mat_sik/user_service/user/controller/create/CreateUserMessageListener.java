package mat_sik.user_service.user.controller.create;

import com.rabbitmq.client.Channel;
import lombok.extern.java.Log;
import mat_sik.user_service.user.model.User;
import mat_sik.user_service.user.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Log
public class CreateUserMessageListener implements ChannelAwareMessageListener {

    private static final boolean MULTIPLE_ACK = false;
    private static final boolean REQUEUE = true;

    private final UserService service;
    private final Jackson2JsonMessageConverter converter;

    private final RabbitTemplate template;
    private final Binding continueTransactionBinding;

    public CreateUserMessageListener(
            UserService service,
            Jackson2JsonMessageConverter converter,
            RabbitTemplate template,
            @Qualifier("continueTransactionCreateBinding") Binding continueTransactionBinding) {
        this.service = service;
        this.converter = converter;
        this.template = template;
        this.continueTransactionBinding = continueTransactionBinding;
    }

    @Override
    public void onMessage(Message message, Channel channel) throws IOException {
        MessageProperties messageProperties = message.getMessageProperties();
        long deliveryTag = messageProperties.getDeliveryTag();

        var createUserMessage = (CreateUserMessage) converter.fromMessage(message, CreateUserMessage.class);

        performLocalTransaction(createUserMessage);

        try {
            continueDistributedTransaction(createUserMessage);
            channel.basicAck(deliveryTag, MULTIPLE_ACK);
        } catch (Exception ex) {
            channel.basicNack(deliveryTag, MULTIPLE_ACK, REQUEUE);
            log.severe(ex.getMessage());
        }
    }

    public void performLocalTransaction(CreateUserMessage createUserMessage) {
        ObjectId id = createUserMessage.id();
        String firstname = createUserMessage.firstname();
        String lastname = createUserMessage.lastname();

        service.save(new User(id, firstname, lastname));
    }

    public void continueDistributedTransaction(CreateUserMessage createUserMessage) {
        ObjectId id = createUserMessage.id();
        String username = createUserMessage.username();
        String email = createUserMessage.email();
        String password = createUserMessage.password();
        var continueCreateUserMessage = new ContinueCreateUserMessage(id, username, email, password);

        template.setMessageConverter(converter);
        template.setExchange(continueTransactionBinding.getExchange());
        template.setRoutingKey(continueTransactionBinding.getRoutingKey());

        // by default delivery mode is persistent, I don't want to handle returns so I leave mandatory as false.
        template.convertAndSend(continueCreateUserMessage);
    }

}
