package org.group.voiture.controllers;

import org.group.voiture.entities.Client;
import org.group.voiture.feign.ClientServiceFeignClient;
import  org.group.voiture.models.CarResponse;
import org.group.voiture.services.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/voiture")
public class CarController {

    @Autowired
    private CarService carService;

    @GetMapping("/restTemplate/all")
    public List<CarResponse> findAll() {
        return carService.findAllRestTemplate ();
    }

    @GetMapping("/restTemplate/{id}")
    public CarResponse findById(@PathVariable Long id) throws Exception {
        return carService.findByIdRestTemplate (id);
    }

    @GetMapping("/feignClient/all")
    public List<CarResponse> findAllFeignClient() {
        return carService.findAllFeignClient ();
    }

    @GetMapping("/feignClient/{id}")
    public CarResponse findByIdFeignClient(@PathVariable Long id) throws Exception{
        return carService.findByIdFeignClient (id);
    }
}