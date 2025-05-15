package com.BackNight.backendNIght.ws.dao;

import com.BackNight.backendNIght.ws.entity.Clientes;
import com.BackNight.backendNIght.ws.repository.ClientesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientesDao {

    @Autowired
    private ClientesRepository clientesRepository;

    public Clientes registrarCliente(Clientes cliente) {
        return clientesRepository.save(cliente);
    }

    public Clientes loginCliente(String usuarioCliente, String contrasenaCliente) {
        return clientesRepository.findByUsuarioClienteAndContrasenaCliente(usuarioCliente, contrasenaCliente);
    }
}
