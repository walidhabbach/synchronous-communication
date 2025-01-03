package org.group.client.controllers;

import org.group.client.entities.Client;
import org.group.client.repositories.ClientRepositroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/client")
public class ClientController {
    @Autowired
    ClientRepositroy clientRepositroy;

    @GetMapping("/all")
    public List<Client> findAll() {
        return clientRepositroy.findAll();
    }

    @GetMapping("/{id}")
    public Client findById(@PathVariable Long id) throws Exception {
        return clientRepositroy.findById(id)
                .orElseThrow(() -> new Exception("Client non trouver"));
    }
}
