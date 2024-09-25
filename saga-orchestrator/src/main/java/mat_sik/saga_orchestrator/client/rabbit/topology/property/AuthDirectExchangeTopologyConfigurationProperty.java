package mat_sik.saga_orchestrator.client.rabbit.topology.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rabbit.topology.exchanges")
public record AuthDirectExchangeTopologyConfigurationProperty(ExchangeConfig auth) {
}
