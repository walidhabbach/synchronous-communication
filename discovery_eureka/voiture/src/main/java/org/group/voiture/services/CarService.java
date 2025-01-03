package org.group.voiture.services;

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

import java.util.Arrays;
import java.util.List;

@Service
public class CarService {
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ClientServiceFeignClient clientServiceFeignClient;
    private final String URL = "http://localhost:8084";

    public List<CarResponse> findAllRestTemplate() {
        List<Car> cars = carRepository.findAll();
        ResponseEntity<List<Client>> response = restTemplate.exchange(
                this.URL + "/api/client/all",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Client>> () {}
        );

        List<Client> clients = response.getBody();

        return cars.stream().map((Car car) -> mapToCarResponse(car, clients)).toList();
    }

    // this function allow the change the Car Entity to A Model that we will send ot the client side using the @Builder Annotation
    private CarResponse mapToCarResponse(Car car, List<Client> clients) {
        Client foundClient = clients.stream()
                .filter(client -> client.getId().equals(car.getClient_id()))
                .findFirst()
                .orElse(null);

        return CarResponse.builder()
                .id(car.getId())
                .brand(car.getBrand())
                .client(foundClient)
                .matricue(car.getMatricule())
                .model(car.getModel())
                .build();
    }


    public CarResponse findByIdRestTemplate(Long id) throws Exception {
        Car car = carRepository.findById(id).orElseThrow(() -> new Exception("Invalid Car Id"));
        Client client = restTemplate.getForObject(this.URL + "/api/client/" + car.getClient_id(), Client.class);
        return CarResponse.builder()
                .id(car.getId())
                .brand(car.getBrand())
                .client(client)
                .matricue(car.getMatricule())
                .model(car.getModel())
                .build();
    }

    public List<CarResponse> findAllFeignClient() {
        List<Car> cars = carRepository.findAll();
         List<Client> clients = clientServiceFeignClient.clientAll ();
        return cars.stream().map((Car car) -> mapToCarResponse(car, clients)).toList();
    }

    // this function allow the change the Car Entity to A Model that we will send ot the client side using the @Builder Annotation


    public CarResponse findByIdFeignClient(Long id) throws Exception {
        Car car = carRepository.findById(id).orElseThrow(() -> new Exception("Invalid Car Id"));
        Client client = clientServiceFeignClient.clientById (car.getClient_id ());
        return CarResponse.builder()
                .id(car.getId())
                .brand(car.getBrand())
                .client(client)
                .matricue(car.getMatricule())
                .model(car.getModel())
                .build();
    }
}
