package com.BackNight.backendNIght.ws.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Clase para almacenar temporalmente códigos de verificación.
 * Los códigos expiran después de un tiempo.
 */
@Component
public class CodigoVerificacionStore {

    // Almacena el correo electrónico y un objeto que contiene el código y su tiempo de expiración
    private final Map<String, CodigoEntry> store = new ConcurrentHashMap<>();
    private static final long EXPIRATION_MINUTES = 5; // Los códigos expiran en 5 minutos

    /**
     * Guarda un código de verificación para un correo electrónico dado.
     * @param email El correo electrónico del usuario.
     * @param codigo El código de verificación a guardar.
     */
    public void guardar(String email, String codigo) {
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES);
        store.put(email, new CodigoEntry(codigo, expiryTime));
        // Opcional: Implementar una limpieza periódica para eliminar códigos expirados
    }

    /**
     * Verifica si un código dado es válido para un correo electrónico.
     * Si es válido y no ha expirado, lo elimina del almacén para un solo uso.
     * @param email El correo electrónico del usuario.
     * @param codigo El código a verificar.
     * @return true si el código es válido y no ha expirado, false en caso contrario.
     */
    public boolean verificar(String email, String codigo) {
        CodigoEntry entry = store.get(email);
        if (entry != null && entry.getCodigo().equals(codigo) && LocalDateTime.now().isBefore(entry.getExpiryTime())) {
            store.remove(email); // Eliminar después de un uso exitoso
            return true;
        }
        return false;
    }

    /**
     * Obtiene un código de verificación sin verificar su validez o eliminarlo.
     * Usado principalmente para comparación sin consumir el código.
     * @param email El correo electrónico del usuario.
     * @return El código de verificación guardado, o null si no se encuentra o ha expirado.
     */
    public String obtener(String email) {
        CodigoEntry entry = store.get(email);
        if (entry != null && LocalDateTime.now().isBefore(entry.getExpiryTime())) {
            return entry.getCodigo();
        }
        return null; // Código no encontrado o expirado
    }

    /**
     * Clase interna para almacenar el código y su tiempo de expiración.
     */
    private static class CodigoEntry {
        private final String codigo;
        private final LocalDateTime expiryTime;

        public CodigoEntry(String codigo, LocalDateTime expiryTime) {
            this.codigo = codigo;
            this.expiryTime = expiryTime;
        }

        public String getCodigo() {
            return codigo;
        }

        public LocalDateTime getExpiryTime() {
            return expiryTime;
        }
    }
}
