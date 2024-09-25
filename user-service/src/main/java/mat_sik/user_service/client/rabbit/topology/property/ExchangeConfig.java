package mat_sik.user_service.client.rabbit.topology.property;

import java.util.Map;

public record ExchangeConfig(String name, Map<String, QueueConfig> queues) {
}
