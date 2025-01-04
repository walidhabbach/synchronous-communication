package org.group.client;

import org.group.client.entities.Client;
import org.group.client.repositories.ClientRepositroy;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@EnableDiscoveryClient
@SpringBootApplication
public class ClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class, args);
	}
	@Bean
	CommandLineRunner initializeH2Database(ClientRepositroy clientRepository) {
		Client client = Client.builder().id(1L).nom("Test").age(25F).build();
		Client client2 = Client.builder().id(2L).nom("Test").age(25F).build();
		Client client3 = Client.builder().id(3L).nom("Test").age(25F).build();
		System.out.println(client);

		return args -> {
			clientRepository.save(client);
			clientRepository.save(client2);
			clientRepository.save(client3);
		};
	}

}
