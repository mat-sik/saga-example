package mat_sik.saga_orchestrator.user.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.java.Log;
import mat_sik.common.message.models.DeleteUserTask;
import mat_sik.common.message.models.UserAuthCreationFailedEvent;
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
public class UserAuthCreationFailedEventListener implements ChannelAwareMessageListener {

    private static final boolean MULTIPLE_ACK = false;
    private static final boolean REQUEUE = true;

    private final Jackson2JsonMessageConverter converter;
    private final MessageSender messageSender;

    private final Binding userDeletionBinding;

    public UserAuthCreationFailedEventListener(
            Jackson2JsonMessageConverter converter,
            RabbitTemplate template,
            @Qualifier("userDeletionBinding") Binding userDeletionBinding
    ) {
        this.converter = converter;
        this.messageSender = new MessageSender(template, converter);
        this.userDeletionBinding = userDeletionBinding;
    }

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        MessageProperties messageProperties = message.getMessageProperties();
        long deliveryTag = messageProperties.getDeliveryTag();

        var userAuthCreationFailedEvent = (UserAuthCreationFailedEvent) converter.fromMessage(message);

        ObjectId id = userAuthCreationFailedEvent.id();

        var deleteUserTask = new DeleteUserTask(id);

        try {
            messageSender.send(userDeletionBinding, deleteUserTask);
            channel.basicAck(deliveryTag, MULTIPLE_ACK);
        } catch (Exception ex) {
            channel.basicNack(deliveryTag, MULTIPLE_ACK, REQUEUE);
            log.severe(ex.getMessage());
        }
    }

}
