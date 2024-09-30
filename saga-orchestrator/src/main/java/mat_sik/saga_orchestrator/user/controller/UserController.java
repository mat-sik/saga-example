package mat_sik.saga_orchestrator.user.controller;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import mat_sik.common.message.models.CreateInTransactionUserTask;
import mat_sik.common.message.models.CreateUserAuthTask;
import mat_sik.common.message.models.CreateUserTask;
import mat_sik.common.message.models.DeleteUserTask;
import mat_sik.common.message.models.UserAuthCreationFailedEvent;
import org.bson.types.ObjectId;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
@Log
public class UserController {

    private static final boolean MULTIPLE_ACK = false;
    private static final boolean REQUEUE = true;

    private final RabbitTemplate template;

    private final Binding userCreationBinding;
    private final Binding userDeletionBinding;
    private final Binding userAuthCreationBinding;

    @RabbitListener(
            queues = "#{userTransactionalCreationQueue.name}",
            ackMode = "MANUAL",
            messageConverter = "#{messageConverter}",
            executor = "virtualThreadTaskExecutor"
    )
    public void createUserInTransaction(
            CreateInTransactionUserTask task,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag
    ) throws IOException {
        ObjectId id = task.id();
        String firstname = task.firstname();
        String lastname = task.lastname();
        String username = task.username();
        String email = task.email();
        String password = task.password();

        var createUserTask = new CreateUserTask(id, firstname, lastname);
        var createUserAuthTask = new CreateUserAuthTask(id, username, email, password);

        try {
            sendCreateUserInTransactionMessage(createUserTask, createUserAuthTask);
            channel.basicAck(deliveryTag, MULTIPLE_ACK);
        } catch (Exception ex) {
            channel.basicNack(deliveryTag, MULTIPLE_ACK, REQUEUE);
            log.severe(ex.getMessage());
        }
    }

    private void sendCreateUserInTransactionMessage(
            CreateUserTask createUserTask,
            CreateUserAuthTask createUserAuthTask
    ) {
        String userCreationExchangeName = userCreationBinding.getExchange();
        String userCreationRoutingKey = userCreationBinding.getRoutingKey();

        String userAuthCreationExchangeName = userAuthCreationBinding.getExchange();
        String userAuthRoutingKey = userAuthCreationBinding.getRoutingKey();

        template.invoke(t -> {
            t.convertAndSend(userCreationExchangeName, userCreationRoutingKey, createUserTask);
            t.convertAndSend(userAuthCreationExchangeName, userAuthRoutingKey, createUserAuthTask);
            t.waitForConfirmsOrDie(Duration.ofSeconds(10).toMillis());
            return true;
        });
    }

    @RabbitListener(
            queues = "#{userCreationCompensationQueue.name}",
            ackMode = "MANUAL",
            messageConverter = "#{messageConverter}",
            executor = "virtualThreadTaskExecutor"
    )
    public void compensateUserCreation(
            UserAuthCreationFailedEvent event,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag
    ) throws IOException {
        ObjectId id = event.id();

        var deleteUserTask = new DeleteUserTask(id);

        try {
            sendDeleteUserMessage(deleteUserTask);
            channel.basicAck(deliveryTag, MULTIPLE_ACK);
        } catch (Exception ex) {
            channel.basicNack(deliveryTag, MULTIPLE_ACK, REQUEUE);
            log.severe(ex.getMessage());
        }
    }

    public void sendDeleteUserMessage(DeleteUserTask task) {
        String targetExchangeName = userDeletionBinding.getExchange();
        String targetRoutingKey = userDeletionBinding.getRoutingKey();

        template.invoke(t -> {
            t.convertAndSend(targetExchangeName, targetRoutingKey, task);
            t.waitForConfirmsOrDie(Duration.ofSeconds(10).toMillis());
            return true;
        });
    }
}
