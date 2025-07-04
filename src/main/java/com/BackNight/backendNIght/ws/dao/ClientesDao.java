package com.BackNight.backendNIght.ws.dao;

import com.BackNight.backendNIght.ws.entity.Clientes;
import com.BackNight.backendNIght.ws.repository.ClientesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientesDao {

    @Autowired
    private ClientesRepository clienteRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * Registra un nuevo cliente cifrando su contraseña.
     * @param cliente El objeto Cliente a registrar.
     * @return El cliente registrado.
     */
    public Clientes registrarCliente(Clientes cliente) {
        // Correcto: Cifra la contraseña antes de guardarla.
        cliente.setContrasenaCliente(passwordEncoder.encode(cliente.getContrasenaCliente()));
        return clienteRepository.save(cliente);
    }

    /**
     * Busca un cliente por su correo electrónico.
     * @param correo El correo electrónico del cliente.
     * @return El cliente encontrado o null si no existe.
     */
    public Clientes obtenerPorCorreo(String correo) {
        // Considera cambiar el tipo de retorno a Optional<Clientes> en el repositorio
        // y aquí, para un manejo más robusto de casos donde el cliente no existe.
        return clienteRepository.findByCorreo(correo);
    }

    /**
     * Busca un cliente por su nombre de usuario.
     * @param usuarioCliente El nombre de usuario del cliente.
     * @return El cliente encontrado o null si no existe.
     */
    public Clientes obtenerPorUsuario(String usuarioCliente) {
        // Igual que en obtenerPorCorreo, considera Optional<Clientes>.
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
            // Podrías lanzar una excepción personalizada aquí para mejor manejo en el controlador.
            throw new IllegalArgumentException("Cliente inválido para actualizar la contraseña: ID no encontrado.");
        }
        cliente.setContrasenaCliente(passwordEncoder.encode(nuevaContrasena)); // Correcto: Cifra la nueva contraseña.
        return clienteRepository.save(cliente);
    }

    /**
     * Intenta autenticar un cliente.
     * @param usuarioCliente Nombre de usuario del cliente.
     * @param contrasenaCliente Contraseña sin cifrar del cliente.
     * @return El objeto Cliente si las credenciales son correctas, null en caso contrario.
     */
    public Clientes loginClientes(String usuarioCliente, String contrasenaCliente) {
        // Aquí obtienes el cliente por usuario (puede ser Optional si modificas el repo).
        Clientes cliente = clienteRepository.findByUsuarioCliente(usuarioCliente);
        // Verifica si el cliente existe y si la contraseña plana coincide con la cifrada.
        if (cliente != null && passwordEncoder.matches(contrasenaCliente, cliente.getContrasenaCliente())) {
            return cliente;
        }
        return null; // Si no existe o las credenciales son incorrectas.
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
     * Nota: Este método asume que la contraseña ya ha sido manejada (cifrada si es nueva o se mantuvo la existente)
     * antes de que el objeto 'cliente' llegue aquí.
     * @param cliente El cliente con los datos a actualizar.
     * @return El cliente actualizado, o null si no se puede actualizar (cliente no existe o ID nulo).
     */
    public Clientes actualizarCliente(Clientes cliente) {
        // Solo guardamos si el cliente tiene un ID y existe en la base de datos
        if (cliente.getIdCliente() != null && clienteRepository.existsById(cliente.getIdCliente())) {
            return clienteRepository.save(cliente);
        }
        return null; // No se puede actualizar si el cliente no existe o no tiene ID
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