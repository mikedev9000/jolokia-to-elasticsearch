package mikedev9000.jolokiatoelasticsearch;

import org.jolokia.client.J4pClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.elasticsearch.jest.JestProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(JestProperties.class)
@Configuration
public class JolokiaConfiguration {

	@ConfigurationProperties(prefix = "jolokia")
	public static class JolokiaProperties {
		String url;

		String username;

		String password;
	}

	@Autowired
	JolokiaProperties properties;

	@Bean
	public J4pClient jolokiaClient() {
		return J4pClient.singleConnection().target(properties.url).user(properties.username)
				.password(properties.password).build();
	}

}
