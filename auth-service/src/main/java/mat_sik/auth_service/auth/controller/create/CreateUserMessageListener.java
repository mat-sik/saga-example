package mat_sik.auth_service.auth.controller.create;

import com.rabbitmq.client.Channel;
import lombok.extern.java.Log;
import mat_sik.auth_service.auth.model.User;
import mat_sik.auth_service.auth.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DuplicateKeyException;
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
    private final Binding compensateTransactionBinding;


    public CreateUserMessageListener(
            UserService service,
            Jackson2JsonMessageConverter converter,
            RabbitTemplate template,
            @Qualifier("compensateCreateUserBinding") Binding compensateTransactionBinding
    ) {
        this.service = service;
        this.converter = converter;
        this.template = template;
        this.compensateTransactionBinding = compensateTransactionBinding;
    }

    @Override
    public void onMessage(Message message, Channel channel) throws IOException {
        MessageProperties messageProperties = message.getMessageProperties();
        long deliveryTag = messageProperties.getDeliveryTag();

        var createUserMessage = (CreateUserMessage) converter.fromMessage(message, CreateUserMessage.class);

        try {
            performLocalTransaction(createUserMessage);
            channel.basicAck(deliveryTag, MULTIPLE_ACK);
        } catch (DuplicateKeyException ex) {
            compensateDistributedTransaction(createUserMessage);
            channel.basicAck(deliveryTag, MULTIPLE_ACK);
        } catch (Exception ex) {
            channel.basicNack(deliveryTag, MULTIPLE_ACK, REQUEUE);
            log.severe(ex.getMessage());
        }
    }

    public void performLocalTransaction(CreateUserMessage createUserMessage) {
        ObjectId id = createUserMessage.id();
        String username = createUserMessage.username();
        String email = createUserMessage.email();
        String password = createUserMessage.password();

        service.save(new User(id, username, email, password));
    }

    public void compensateDistributedTransaction(CreateUserMessage createUserMessage) {
        ObjectId id = createUserMessage.id();

        var compensateCreateUserMessage = new CompensateCreateUserMessage(id);

        template.setMessageConverter(converter);
        template.setExchange(compensateTransactionBinding.getExchange());
        template.setRoutingKey(compensateTransactionBinding.getRoutingKey());

        // by default delivery mode is persistent, I don't want to handle returns so I leave mandatory as false.
        template.convertAndSend(compensateCreateUserMessage);
    }

}
