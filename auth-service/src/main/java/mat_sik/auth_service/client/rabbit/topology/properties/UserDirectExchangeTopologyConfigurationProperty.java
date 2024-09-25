package mat_sik.auth_service.client.rabbit.topology.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rabbit.topology.exchanges")
public record UserDirectExchangeTopologyConfigurationProperty(ExchangeConfig user) {
}
