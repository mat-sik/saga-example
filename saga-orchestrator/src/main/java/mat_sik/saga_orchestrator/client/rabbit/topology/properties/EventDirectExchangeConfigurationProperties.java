package mat_sik.saga_orchestrator.client.rabbit.topology.properties;

import mat_sik.saga_orchestrator.client.rabbit.topology.properties.types.ExchangeConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("rabbit.topology.exchanges.direct")
public record EventDirectExchangeConfigurationProperties(ExchangeConfig event) {
}
