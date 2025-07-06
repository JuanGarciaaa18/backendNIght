package com.BackNight.backendNIght.ws.dao;

import com.BackNight.backendNIght.ws.entity.Clientes;
import com.BackNight.backendNIght.ws.repository.ClientesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importar

import java.util.List;
import java.util.Optional;

@Service
public class ClientesDao {

    @Autowired
    private ClientesRepository clienteRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional // Añadido para operaciones de escritura
    public Clientes registrarCliente(Clientes cliente) {
        cliente.setContrasenaCliente(passwordEncoder.encode(cliente.getContrasenaCliente()));
        return clienteRepository.save(cliente);
    }

    @Transactional(readOnly = true) // Añadido para operaciones de lectura
    public Clientes obtenerPorCorreo(String correo) {
        return clienteRepository.findByCorreo(correo);
    }

    @Transactional(readOnly = true) // Añadido para operaciones de lectura
    public Clientes obtenerPorUsuario(String usuarioCliente) {
        return clienteRepository.findByUsuarioCliente(usuarioCliente);
    }

    @Transactional // Añadido para operaciones de escritura
    public Clientes actualizarContrasena(Clientes cliente, String nuevaContrasena) {
        if (cliente.getIdCliente() == null || !clienteRepository.existsById(cliente.getIdCliente())) {
            throw new IllegalArgumentException("Cliente inválido para actualizar la contraseña: ID no encontrado.");
        }
        cliente.setContrasenaCliente(passwordEncoder.encode(nuevaContrasena));
        return clienteRepository.save(cliente);
    }

    @Transactional(readOnly = true) // Añadido para operaciones de lectura
    public Clientes loginClientes(String usuarioCliente, String contrasenaCliente) {
        Clientes cliente = clienteRepository.findByUsuarioCliente(usuarioCliente);
        if (cliente != null && passwordEncoder.matches(contrasenaCliente, cliente.getContrasenaCliente())) {
            return cliente;
        }
        return null;
    }

    @Transactional(readOnly = true) // Añadido para operaciones de lectura
    public List<Clientes> obtenerTodos() {
        return clienteRepository.findAll();
    }

    @Transactional // Añadido para operaciones de escritura
    public Clientes actualizarCliente(Clientes cliente) {
        if (cliente.getIdCliente() != null && clienteRepository.existsById(cliente.getIdCliente())) {
            return clienteRepository.save(cliente);
        }
        return null;
    }

    @Transactional(readOnly = true) // Añadido para operaciones de lectura
    public Optional<Clientes> findById(Integer idCliente) {
        return clienteRepository.findById(idCliente);
    }

    @Transactional // Añadido para operaciones de escritura
    public void eliminarCliente(Integer idCliente) {
        clienteRepository.deleteById(idCliente);
    }
}
