package org.group.client.services;

import org.group.client.entities.Client;
import org.group.client.repositories.ClientRepositroy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {
    private final ClientRepositroy clientRepositroy;

    // Constructeur
    public ClientService(ClientRepositroy clientRepo) {
        this.clientRepositroy = clientRepo;
    }

    // Fetch all clients
    public List<Client> findAll() {
        return clientRepositroy.findAll();
    }

    // Save or update a client
    public Client save(Client client) {

            return clientRepositroy.save(client);
    }

    // Find client by ID
    public Optional<Client> findById(Long id) {
        return clientRepositroy.findById(id);
    }

    // Delete client by ID
    public void deleteById(Long id) {
        if (!clientRepositroy.existsById(id)) {
            throw new IllegalArgumentException("Client avec ID " + id + " n'existe pas.");
        }
        clientRepositroy.deleteById(id);
    }
}
