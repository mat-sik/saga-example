package mat_sik.user_service.client.rabbit.topology.properties;

import mat_sik.user_service.client.rabbit.topology.properties.types.ExchangeConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("rabbit.topology.exchanges.direct")
public record TaskDirectExchangeConfigurationProperties(ExchangeConfig task) {
}
