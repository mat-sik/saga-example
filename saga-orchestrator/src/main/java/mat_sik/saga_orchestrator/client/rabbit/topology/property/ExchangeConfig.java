package mat_sik.saga_orchestrator.client.rabbit.topology.property;

import java.util.Map;

public record ExchangeConfig(String name, Map<String, QueueConfig> queues) {
}
