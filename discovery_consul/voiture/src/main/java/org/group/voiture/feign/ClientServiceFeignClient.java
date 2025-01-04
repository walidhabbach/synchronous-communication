package org.group.voiture.feign;


import org.group.voiture.entities.Client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "service-client")
public interface ClientServiceFeignClient {
    @GetMapping(path = "/api/client/{id}")
    public Client clientById(@PathVariable Long id);
        @GetMapping(path = "/api/client/all")
    public List<Client> clientAll();
}
