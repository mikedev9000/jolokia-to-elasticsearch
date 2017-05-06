package mikedev9000.jolokiatoelasticsearch;

import org.jolokia.client.J4pClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JolokiaProperties.class)
public class JolokiaConfiguration {

	@Autowired
	JolokiaProperties props;

	@Bean
	public J4pClient jolokiaClient() {
		return J4pClient.singleConnection().url(props.getUrl()).user(props.getUsername()).password(props.getPassword())
				.build();
	}

}
