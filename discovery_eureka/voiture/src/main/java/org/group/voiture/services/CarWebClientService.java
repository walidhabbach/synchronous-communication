package org.group.voiture.services;

import lombok.extern.slf4j.Slf4j;
import org.group.voiture.entities.Car;
import org.group.voiture.entities.Client;
import org.group.voiture.models.CarResponse;
import org.group.voiture.repositories.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class CarWebClientService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    private final String CLIENT_SERVICE_URL = "http://service-client";

    public List<CarResponse> findAll() {
        log.debug("Fetching all cars with WebClient");
        List<Car> cars = carRepository.findAll();
        List<Client> clients;


            clients = webClientBuilder.build()
                    .get()
                    .uri(CLIENT_SERVICE_URL + "/api/client/all")
                    .retrieve()
                    .bodyToFlux(Client.class)
                    .collectList()
                    .block();


        if (clients == null) {
            throw new RuntimeException("No clients data received");
        }

        return cars.stream()
                .map(car -> mapToCarResponse(car, clients))
                .toList();
    }

    public CarResponse findById(Long id) {
        log.debug("Fetching car by id with WebClient: {}", id);
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found with id: " + id));

        Client client;

        try {
            client = webClientBuilder.build()
                    .get()
                    .uri(CLIENT_SERVICE_URL + "/api/client/" + car.getClient_id())
                    .retrieve()
                    .bodyToMono(Client.class)
                    .block();
        } catch (Exception e) {
            log.error("Failed to fetch client data: {}", e.getMessage());
            client = null; // Fallback to null
        }

        return mapToCarResponse(car, client);
    }

    private CarResponse mapToCarResponse(Car car, List<Client> clients) {
        Client foundClient = clients.stream()
                .filter(client -> client.getId().equals(car.getClient_id()))
                .findFirst()
                .orElse(null);
        return mapToCarResponse(car, foundClient);
    }

    private CarResponse mapToCarResponse(Car car, Client client) {
        return CarResponse.builder()
                .id(car.getId())
                .brand(car.getBrand())
                .client(client)
                .matricule(car.getMatricule())
                .model(car.getModel())
                .build();
    }
}