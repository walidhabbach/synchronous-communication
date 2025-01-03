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
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
    @Autowired
    private WebClient.Builder webClientBuilder;
    private final String URL = "http://localhost:8084";


    public List<CarResponse> findAllWebClient() {
        WebClient webClient = webClientBuilder.build();

        // Fetch cars from the local repository
        List<Car> cars = carRepository.findAll();

        // Fetch clients from the external service
        List<Client> clients = webClient.get()
                .uri("http://service-client/api/client/all")
                .retrieve()
                .bodyToFlux(Client.class)
                .collectList()
                .block();

        if (clients == null || clients.isEmpty()) {
            // Handle case when clients are not found (optional)
            throw new RuntimeException("Clients data not available");
        }

        // Map each car to a CarResponse
        return cars.stream()
                .map(car -> mapToCarResponse(car, clients))
                .toList();
    }

    public CarResponse findByIdWebClient(Long id) throws Exception {
        WebClient webClient = webClientBuilder.build();

        // Fetch car from local repository
        Car car = carRepository.findById(id).orElseThrow(() -> new Exception("Invalid Car Id"));

        // Fetch client details from the external service
        Client client = webClient.get()
                .uri("http://service-client/api/client/{id}", car.getClient_id())
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> Mono.error(new RuntimeException("External service error")))
                .bodyToMono(Client.class)
                .block();

        if (client == null) {
            // Handle case when client data is not available (optional)
            throw new Exception("Client data not available");
        }

        return CarResponse.builder()
                .id(car.getId())
                .brand(car.getBrand())
                .client(client)
                .matricue(car.getMatricule())
                .model(car.getModel())
                .build();
    }



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
