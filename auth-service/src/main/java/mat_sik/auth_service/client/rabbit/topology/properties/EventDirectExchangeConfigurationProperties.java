package mat_sik.auth_service.client.rabbit.topology.properties;

import mat_sik.auth_service.client.rabbit.topology.properties.types.ExchangeConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("rabbit.topology.exchanges.direct")
public record EventDirectExchangeConfigurationProperties(ExchangeConfig event) {
}
