package mikedev9000.jolokiatoelasticsearch;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.management.MalformedObjectNameException;

import org.jolokia.client.J4pClient;
import org.jolokia.client.exception.J4pException;
import org.jolokia.client.request.J4pReadRequest;
import org.jolokia.client.request.J4pResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.searchbox.client.JestClient;
import io.searchbox.core.Bulk;
import io.searchbox.core.Bulk.Builder;
import io.searchbox.core.Index;

@SpringBootApplication
@Component
@EnableScheduling
@EnableConfigurationProperties(JolokiaConfiguration.class)
public class App {
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

	@Bean
	static Clock clock() {
		return Clock.systemDefaultZone();
	}

	@Autowired
	JestClient elasticsearch;

	@Autowired
	J4pClient jolokia;

	@Autowired
	Clock clock;

	@Scheduled
	public void doWork() throws Exception {

		Builder action = new Bulk.Builder();

		poll().stream().map((document) -> new Index.Builder(document).build()).forEach(action::addAction);

		elasticsearch.execute(action.build());
	}

	private List<Object> poll() throws MalformedObjectNameException, J4pException {

		List<Object> documents = new ArrayList<>();

		final long now = clock.millis();
		J4pResponse<J4pReadRequest> response = jolokia
				.execute(new J4pReadRequest("metrics:type=timer,name=*", "OneMinuteRate", "Mean", "StdDev"));

		Map<String, Map<String, Object>> data = (Map<String, Map<String, Object>>) response.asJSONObject().get("value");

		data.forEach((key, value) -> {
			value.put("mbean", key);
			value.put("@timestamp", now);

			documents.add(value);
		});

		return documents;
	}
}
