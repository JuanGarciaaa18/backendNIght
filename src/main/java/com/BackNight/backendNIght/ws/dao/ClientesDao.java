package com.BackNight.backendNIght.ws.dao;

import com.BackNight.backendNIght.ws.entity.Clientes; // Usamos 'Cliente' singular para la entidad
import com.BackNight.backendNIght.ws.entity.Clientes;
import com.BackNight.backendNIght.ws.repository.ClientesRepository; // Usamos 'ClienteRepository' singular
import com.BackNight.backendNIght.ws.repository.ClientesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional; // Importar Optional

@Service
public class ClientesDao {

    @Autowired
    private ClientesRepository clienteRepository; // Inyectar ClienteRepository
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * Registra un nuevo cliente cifrando su contraseña.
     * @param cliente El objeto Cliente a registrar.
     * @return El cliente registrado.
     */
    public Clientes registrarCliente(Clientes cliente) {
        cliente.setContrasenaCliente(passwordEncoder.encode(cliente.getContrasenaCliente()));
        return clienteRepository.save(cliente);
    }

    /**
     * Busca un cliente por su correo electrónico.
     * @param correo El correo electrónico del cliente.
     * @return El cliente encontrado o null si no existe.
     */
    public Clientes obtenerPorCorreo(String correo) {
        return clienteRepository.findByCorreo(correo);
    }

    /**
     * Busca un cliente por su nombre de usuario.
     * @param usuarioCliente El nombre de usuario del cliente.
     * @return El cliente encontrado o null si no existe.
     */
    public Clientes obtenerPorUsuario(String usuarioCliente) {
        return clienteRepository.findByUsuarioCliente(usuarioCliente);
    }

    /**
     * Actualiza la contraseña de un cliente específico.
     * @param cliente El cliente al que se le actualizará la contraseña (debe tener el ID).
     * @param nuevaContrasena La nueva contraseña sin cifrar.
     * @return El cliente con la contraseña actualizada.
     */
    public Clientes actualizarContrasena(Clientes cliente, String nuevaContrasena) {
        // Asegúrate de que el cliente tiene un ID válido antes de guardar
        if (cliente.getIdCliente() == null || !clienteRepository.existsById(cliente.getIdCliente())) {
            throw new IllegalArgumentException("Cliente inválido para actualizar la contraseña: ID no encontrado.");
        }
        cliente.setContrasenaCliente(passwordEncoder.encode(nuevaContrasena));
        return clienteRepository.save(cliente);
    }

    /**
     * Intenta autenticar un cliente.
     * @param usuarioCliente Nombre de usuario del cliente.
     * @param contrasenaCliente Contraseña sin cifrar del cliente.
     * @return El objeto Cliente si las credenciales son correctas, null en caso contrario.
     */
    public Clientes loginClientes(String usuarioCliente, String contrasenaCliente) { // Nombre consistente con ClientesService
        Clientes cliente = clienteRepository.findByUsuarioCliente(usuarioCliente);
        if (cliente != null && passwordEncoder.matches(contrasenaCliente, cliente.getContrasenaCliente())) {
            return cliente;
        }
        return null;
    }

    /**
     * Obtiene una lista de todos los clientes.
     * @return Una lista de objetos Cliente.
     */
    public List<Clientes> obtenerTodos() {
        return clienteRepository.findAll();
    }

    /**
     * Actualiza un cliente existente.
     * Nota: Este método espera que la contraseña ya esté cifrada si se modificó,
     * o que se mantenga la existente si no se proporciona una nueva.
     * @param cliente El cliente con los datos a actualizar.
     * @return El cliente actualizado.
     */
    public Clientes actualizarCliente(Clientes cliente) {
        // Solo guardamos si el cliente tiene un ID y existe en la base de datos
        if (cliente.getIdCliente() != null && clienteRepository.existsById(cliente.getIdCliente())) {
            return clienteRepository.save(cliente);
        }
        return null; // No se puede actualizar si el cliente no existe
    }

    /**
     * Busca un cliente por su ID.
     * @param idCliente El ID del cliente.
     * @return Un Optional que contiene el Cliente si se encuentra, o vacío si no.
     */
    public Optional<Clientes> findById(Integer idCliente) {
        return clienteRepository.findById(idCliente);
    }

    /**
     * Elimina un cliente por su ID.
     * @param idCliente El ID del cliente a eliminar.
     */
    public void eliminarCliente(Integer idCliente) {
        clienteRepository.deleteById(idCliente);
    }
}