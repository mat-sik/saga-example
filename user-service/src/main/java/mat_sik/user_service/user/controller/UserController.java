package mat_sik.user_service.user.controller;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import mat_sik.common.message.models.CreateUserTask;
import mat_sik.common.message.models.DeleteUserTask;
import mat_sik.user_service.user.model.User;
import mat_sik.user_service.user.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Log
public class UserController {

    private static final boolean MULTIPLE_ACK = false;
    private static final boolean REQUEUE = true;

    private final UserService service;

    @RabbitListener(
            queues = "#{userCreationQueue.name}",
            ackMode = "MANUAL",
            messageConverter = "#{messageConverter}",
            executor = "virtualThreadTaskExecutor"
    )
    public void createUser(
            CreateUserTask task,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag
    ) throws IOException {
        try {
            performLocalTransaction(task);
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

    @RabbitListener(
            queues = "#{userDeletionQueue.name}",
            ackMode = "MANUAL",
            messageConverter = "#{messageConverter}",
            executor = "virtualThreadTaskExecutor"
    )
    public void deleteUser(
            DeleteUserTask task,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag
    ) throws IOException {
        ObjectId id = task.id();

        service.deleteById(id);

        channel.basicAck(deliveryTag, MULTIPLE_ACK);
    }

}
