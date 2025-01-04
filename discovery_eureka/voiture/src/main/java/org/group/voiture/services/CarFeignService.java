package org.group.voiture.services;

import lombok.extern.slf4j.Slf4j;
import org.group.voiture.entities.Car;
import org.group.voiture.entities.Client;
import org.group.voiture.feign.ClientServiceFeignClient;
import org.group.voiture.models.CarResponse;
import org.group.voiture.repositories.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
public class CarFeignService {
    @Autowired
    private   CarRepository carRepository;
    @Autowired
    private   ClientServiceFeignClient clientServiceFeignClient;

    public List<CarResponse> findAll() {
        log.debug("Fetching all cars with Feign");
        List<Car> cars = carRepository.findAll();
        List<Client> clients = clientServiceFeignClient.clientAll();

        return cars.stream()
                .map(car -> mapToCarResponse(car, clients))
                .toList();
    }

    public CarResponse findById(Long id) {
        log.debug("Fetching car by id with Feign: {}", id);
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found with id: " + id));

        Client client = clientServiceFeignClient.clientById(car.getClient_id ());
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
    // Same mapping methods as other services...
}
