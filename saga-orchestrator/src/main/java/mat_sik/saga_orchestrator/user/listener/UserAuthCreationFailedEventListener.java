package mat_sik.saga_orchestrator.user.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.java.Log;
import mat_sik.common.message.models.DeleteUserTask;
import mat_sik.common.message.models.UserAuthCreationFailedEvent;
import org.bson.types.ObjectId;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Log
public class UserAuthCreationFailedEventListener implements ChannelAwareMessageListener {

    private static final boolean MULTIPLE_ACK = false;
    private static final boolean REQUEUE = true;

    private final Jackson2JsonMessageConverter converter;
    private final RabbitTemplate template;

    private final Binding userDeletionBinding;

    public UserAuthCreationFailedEventListener(
            Jackson2JsonMessageConverter converter,
            RabbitTemplate template,
            @Qualifier("userDeletionBinding") Binding userDeletionBinding
    ) {
        this.converter = converter;
        this.template = template;
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
            sendMessage(deleteUserTask);
            channel.basicAck(deliveryTag, MULTIPLE_ACK);
        } catch (Exception ex) {
            channel.basicNack(deliveryTag, MULTIPLE_ACK, REQUEUE);
            log.severe(ex.getMessage());
        }
    }

    public void sendMessage(DeleteUserTask task) {
        String targetExchangeName = userDeletionBinding.getExchange();
        String targetRoutingKey = userDeletionBinding.getRoutingKey();

        template.invoke(t -> {
            t.convertAndSend(targetExchangeName, targetRoutingKey, task);
            t.waitForConfirmsOrDie(Duration.ofSeconds(10).toMillis());
            return true;
        });
    }

}
