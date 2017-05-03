package mikedev9000.jolokiatoelasticsearch;

import java.io.IOException;
import java.time.Clock;
import java.util.List;

import org.jolokia.client.J4pClient;
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
	public void doWork() throws IOException {

		Builder action = new Bulk.Builder();

		poll().stream().map((document) -> new Index.Builder(document).build()).forEach(action::addAction);

		elasticsearch.execute(action.build());
	}

	private List<Object> poll() {
		// TODO Auto-generated method stub
		return null;
	}
}
