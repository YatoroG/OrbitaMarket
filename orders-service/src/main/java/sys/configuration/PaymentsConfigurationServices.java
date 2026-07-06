package sys.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.payments-service")
public record PaymentsConfigurationServices(String url) {
}
