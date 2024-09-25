package mat_sik.auth_service.client.rabbit.topology.properties;

import java.util.Map;

public record ExchangeConfig(String name, Map<String, QueueConfig> queues) {
}
