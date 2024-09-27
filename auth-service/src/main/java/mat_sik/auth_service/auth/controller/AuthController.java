package mat_sik.auth_service.auth.controller;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import mat_sik.auth_service.auth.model.UserAuth;
import mat_sik.auth_service.auth.service.UserService;
import mat_sik.common.message.models.CreateUserAuthTask;
import mat_sik.common.message.models.UserAuthCreationFailedEvent;
import org.bson.types.ObjectId;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
@Log
public class AuthController {

    private static final boolean MULTIPLE_ACK = false;
    private static final boolean REQUEUE = true;

    private final UserService service;
    private final RabbitTemplate template;
    private final Binding userCreationCompensationBinding;

    @RabbitListener(
            queues = "#{userAuthCreationQueue.name}",
            ackMode = "MANUAL",
            messageConverter = "#{messageConverter}",
            executor = "virtualThreadTaskExecutor"
    )
    public void createUserAuth(
            CreateUserAuthTask task,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag
    ) throws IOException {
        try {
            performLocalTransaction(task);
            channel.basicAck(deliveryTag, MULTIPLE_ACK);
        } catch (DuplicateKeyException ex) {
            compensateDistributedTransaction(task);
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

        service.save(new UserAuth(id, username, email, password));
    }

    public void compensateDistributedTransaction(CreateUserAuthTask createUserAuthTask) {
        ObjectId id = createUserAuthTask.id();
        var userAuthCreationFailedEvent = new UserAuthCreationFailedEvent(id);

        String targetExchangeName = userCreationCompensationBinding.getExchange();
        String targetRoutingKey = userCreationCompensationBinding.getRoutingKey();
        template.invoke(t -> {
            t.convertAndSend(targetExchangeName, targetRoutingKey, userAuthCreationFailedEvent);
            t.waitForConfirmsOrDie(Duration.ofSeconds(10).toMillis());
            return true;
        });
    }

}
