package org.group.voiture;

import org.group.voiture.entities.Car;
import org.group.voiture.repositories.CarRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class VoitureApplication {

	public static void main(String[] args) {
		SpringApplication.run(VoitureApplication.class, args);
	}
	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		requestFactory.setConnectTimeout(5000);
		requestFactory.setReadTimeout(5000);
		restTemplate.setRequestFactory(requestFactory);

		return restTemplate;
	}
	@Bean
	CommandLineRunner initializeH2Database(CarRepository carRepository) {
		return args -> {
			carRepository.save(Car.builder ().id (1L).brand ("gg").model ("cs").client_id (1L).matricule ("qsdcqs").build ());
			carRepository.save(Car.builder ().id (2L).brand ("gg").model ("cs").client_id (2L).matricule ("qsdcqs").build ());
			carRepository.save(Car.builder ().id (3L).brand ("gg").model ("cs").client_id (3L).matricule ("qsdcqs").build ());
		};
	}

}
