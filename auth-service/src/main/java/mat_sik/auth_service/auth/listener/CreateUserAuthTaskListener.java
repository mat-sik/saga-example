package mat_sik.auth_service.auth.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.java.Log;
import mat_sik.auth_service.auth.model.User;
import mat_sik.auth_service.auth.service.UserService;
import mat_sik.auth_service.client.rabbit.listener.MessageSender;
import mat_sik.common.message.models.CreateUserAuthTask;
import mat_sik.common.message.models.UserAuthCreationFailedEvent;
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
public class CreateUserAuthTaskListener implements ChannelAwareMessageListener {

    private static final boolean MULTIPLE_ACK = false;
    private static final boolean REQUEUE = true;

    private final UserService service;
    private final Jackson2JsonMessageConverter converter;
    private final Binding compensateTransactionBinding;
    private final MessageSender messageSender;

    public CreateUserAuthTaskListener(
            UserService service,
            Jackson2JsonMessageConverter converter,
            RabbitTemplate template,
            @Qualifier("userCreationCompensationBinding") Binding compensateTransactionBinding
    ) {
        this.service = service;
        this.converter = converter;
        this.compensateTransactionBinding = compensateTransactionBinding;
        this.messageSender = new MessageSender(template, converter);
    }

    @Override
    public void onMessage(Message message, Channel channel) throws IOException {
        MessageProperties messageProperties = message.getMessageProperties();
        long deliveryTag = messageProperties.getDeliveryTag();

        var createUserAuthTask = (CreateUserAuthTask) converter.fromMessage(message, CreateUserAuthTask.class);

        try {
            performLocalTransaction(createUserAuthTask);
            channel.basicAck(deliveryTag, MULTIPLE_ACK);
        } catch (DuplicateKeyException ex) {
            compensateDistributedTransaction(createUserAuthTask);
            channel.basicAck(deliveryTag, MULTIPLE_ACK);
        } catch (Exception ex) {
            channel.basicNack(deliveryTag, MULTIPLE_ACK, REQUEUE);
            log.severe(ex.getMessage());
        }
    }

    public void performLocalTransaction(CreateUserAuthTask createUserAuthTask) {
        ObjectId id = createUserAuthTask.id();
        String username = createUserAuthTask.username();
        String email = createUserAuthTask.email();
        String password = createUserAuthTask.password();

        service.save(new User(id, username, email, password));
    }

    public void compensateDistributedTransaction(CreateUserAuthTask createUserAuthTask) {
        ObjectId id = createUserAuthTask.id();
        var userAuthCreationFailedEvent = new UserAuthCreationFailedEvent(id);
        messageSender.send(compensateTransactionBinding, userAuthCreationFailedEvent);
    }

}
