package com.BackNight.backendNIght.ws.dao;

import com.BackNight.backendNIght.ws.entity.Clientes;
import com.BackNight.backendNIght.ws.repository.ClientesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientesDao {

    @Autowired
    private ClientesRepository clientesRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Clientes registrarCliente(Clientes cliente) {
        cliente.setContrasenaCliente(passwordEncoder.encode(cliente.getContrasenaCliente()));
        return clientesRepository.save(cliente);
    }
    public Clientes obtenerPorCorreo(String correo) {
        return clientesRepository.findByCorreo(correo);
    }

    public Clientes actualizarContrasena(Clientes cliente, String nuevaContrasena) {
        cliente.setContrasenaCliente(passwordEncoder.encode(nuevaContrasena));
        return clientesRepository.save(cliente);
    }


    public Clientes loginCliente(String usuarioCliente, String contrasenaCliente) {
        Clientes cliente = clientesRepository.findByUsuarioCliente(usuarioCliente);
        if (cliente != null && passwordEncoder.matches(contrasenaCliente, cliente.getContrasenaCliente())) {
            return cliente;
        }
        return null;
    }
    public List<Clientes> obtenerTodos() {
        return clientesRepository.findAll();
    }

}