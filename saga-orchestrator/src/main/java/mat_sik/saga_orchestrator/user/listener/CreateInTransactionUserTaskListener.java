package mat_sik.saga_orchestrator.user.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.java.Log;
import mat_sik.common.message.models.CreateInTransactionUserTask;
import mat_sik.common.message.models.CreateUserAuthTask;
import mat_sik.common.message.models.CreateUserTask;
import mat_sik.saga_orchestrator.client.rabbit.listener.MessageSender;
import org.bson.types.ObjectId;
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
public class CreateInTransactionUserTaskListener implements ChannelAwareMessageListener {

    private static final boolean MULTIPLE_ACK = false;
    private static final boolean REQUEUE = true;

    private final Jackson2JsonMessageConverter converter;
    private final MessageSender messageSender;

    private final Binding userCreationBinding;
    private final Binding userAuthCreationBinding;

    public CreateInTransactionUserTaskListener(
            Jackson2JsonMessageConverter converter,
            RabbitTemplate template,
            @Qualifier("userCreationBinding") Binding userCreationBinding,
            @Qualifier("userAuthCreationBinding") Binding userAuthCreationBinding
    ) {
        this.converter = converter;
        this.userCreationBinding = userCreationBinding;
        this.userAuthCreationBinding = userAuthCreationBinding;
        this.messageSender = new MessageSender(template, converter);
    }

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        MessageProperties messageProperties = message.getMessageProperties();
        long deliveryTag = messageProperties.getDeliveryTag();

        var createInTransactionUserTask = (CreateInTransactionUserTask) converter.fromMessage(message);

        ObjectId id = createInTransactionUserTask.id();
        String firstname = createInTransactionUserTask.firstname();
        String lastname = createInTransactionUserTask.lastname();
        String username = createInTransactionUserTask.username();
        String email = createInTransactionUserTask.email();
        String password = createInTransactionUserTask.password();

        var createUserTask = new CreateUserTask(id, firstname, lastname);
        var createUserAuthTask = new CreateUserAuthTask(id, username, email, password);

        try {
            messageSender.send(userCreationBinding, createUserTask);
            messageSender.send(userAuthCreationBinding, createUserAuthTask);
            channel.basicAck(deliveryTag, MULTIPLE_ACK);
        } catch (Exception ex) {
            channel.basicNack(deliveryTag, MULTIPLE_ACK, REQUEUE);
            log.severe(ex.getMessage());
        }
    }

}
