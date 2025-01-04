package org.group.voiture.services;

import lombok.extern.slf4j.Slf4j;
import org.group.voiture.entities.Car;
import org.group.voiture.entities.Client;
import org.group.voiture.models.CarResponse;
import org.group.voiture.repositories.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CarRestTemplateService {
    @Autowired
    private   CarRepository carRepository;
    @Autowired
    private   RestTemplate restTemplate;
    private final String CLIENT_SERVICE_URL = "http://service-client";

    public List<CarResponse> findAll() {
        log.debug("Fetching all cars with RestTemplate");
        List<Car> cars = carRepository.findAll();
        ResponseEntity<List<Client>> response = restTemplate.exchange(
                CLIENT_SERVICE_URL + "/api/client/all",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Client>>() {}
        );

        List<Client> clients = Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new RuntimeException("No clients data received"));

        return cars.stream()
                .map(car -> mapToCarResponse(car, clients))
                .toList();
    }

    public CarResponse findById(Long id) {
        log.debug("Fetching car by id with RestTemplate: {}", id);
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found with id: " + id));

        Client client = restTemplate.getForObject(
                CLIENT_SERVICE_URL + "/api/client/" + car.getClient_id (),
                Client.class
        );

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
