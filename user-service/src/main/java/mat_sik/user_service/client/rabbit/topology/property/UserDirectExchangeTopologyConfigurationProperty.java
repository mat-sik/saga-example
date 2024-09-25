package mat_sik.user_service.client.rabbit.topology.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rabbit.topology.exchanges")
public record UserDirectExchangeTopologyConfigurationProperty(ExchangeConfig user) {
}
