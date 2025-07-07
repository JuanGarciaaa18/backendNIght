package com.BackNight.backendNIght.ws.dao;

import com.BackNight.backendNIght.ws.entity.Clientes;
import com.BackNight.backendNIght.ws.repository.ClientesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ClientesDao {

    @Autowired
    private ClientesRepository clientesRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public Clientes registrarCliente(Clientes cliente) {
        if (clientesRepository.findByCorreo(cliente.getCorreo()) != null) {
            throw new RuntimeException("El correo ya está registrado.");
        }
        if (clientesRepository.findByUsuarioCliente(cliente.getUsuarioCliente()) != null) {
            throw new RuntimeException("El nombre de usuario ya está en uso.");
        }
        cliente.setContrasenaCliente(passwordEncoder.encode(cliente.getContrasenaCliente()));
        return clientesRepository.save(cliente);
    }

    @Transactional
    public Clientes actualizarCliente(Clientes cliente) {
        return clientesRepository.save(cliente);
    }

    @Transactional(readOnly = true)
    public Clientes obtenerPorCorreo(String correo) {
        return clientesRepository.findByCorreo(correo);
    }

    @Transactional
    public void actualizarContrasena(Clientes cliente, String nuevaContrasena) {
        cliente.setContrasenaCliente(passwordEncoder.encode(nuevaContrasena));
        clientesRepository.save(cliente);
    }

    @Transactional(readOnly = true)
    public Optional<Clientes> findById(Integer idCliente) {
        return clientesRepository.findById(idCliente);
    }

    // --- MÉTODO CLAVE PARA EL LOGIN ---
    @Transactional(readOnly = true)
    public Clientes loginClientes(String usuarioCliente, String contrasena) {
        // Busca al cliente por su nombre de usuario
        Clientes cliente = clientesRepository.findByUsuarioCliente(usuarioCliente);

        if (cliente != null) {
            // Compara la contraseña proporcionada con la contraseña cifrada
            if (passwordEncoder.matches(contrasena, cliente.getContrasenaCliente())) {
                return cliente; // Credenciales correctas
            }
        }
        return null; // Usuario no encontrado o contraseña incorrecta
    }

    // ... (otros métodos si los tienes) ...
}
