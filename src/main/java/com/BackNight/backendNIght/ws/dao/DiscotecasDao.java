package com.BackNight.backendNIght.ws.dao;

import com.BackNight.backendNIght.ws.entity.Administradores;
import com.BackNight.backendNIght.ws.entity.Discoteca;
import com.BackNight.backendNIght.ws.repository.AdministradoresRepository;
import com.BackNight.backendNIght.ws.repository.DiscotecaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DiscotecasDao {

    @Autowired
    private DiscotecaRepository discotecaRepository;

    @Autowired
    private AdministradoresRepository administradoresRepository;

    /**
     * Consulta una discoteca individual por su NIT.
     * @param nit El NIT de la discoteca.
     * @return La discoteca encontrada o null si no existe.
     */
    public Discoteca consultarDiscotecaIndividual(Integer nit) {
        return discotecaRepository.findById(nit).orElse(null);
    }

    /**
     * Obtiene una lista de discotecas filtradas por el ID del administrador.
     * Se limpia la referencia al administrador en cada discoteca para evitar problemas de serialización JSON.
     * @param idAdmin El ID del administrador para filtrar.
     * @return Una lista de discotecas que pertenecen al administrador.
     */
    public List<Discoteca> obtenerListaDiscotecasPorAdmin(Integer idAdmin) {
        List<Discoteca> discotecas = discotecaRepository.findByAdministrador_IdAdmin(idAdmin);

        // Romper ciclos de referencias y limpiar referencias innecesarias para evitar errores JSON
        for (Discoteca d : discotecas) {
            d.setAdministrador(null); // No enviar el administrador completo para evitar recursión
            if (d.getZonas() != null) {
                d.getZonas().forEach(z -> {
                    z.setDiscoteca(null); // Limpiar referencia a discoteca en zona
                    if (z.getMesas() != null) {
                        z.getMesas().forEach(m -> m.setZona(null)); // Limpiar referencia a zona en mesa
                    }
                });
            }
        }
        return discotecas;
    }

    /**
     * Obtiene una lista de *todas* las discotecas disponibles.
     * Limpia referencias para evitar problemas de serialización JSON.
     * @return Una lista de todas las discotecas.
     */
    public List<Discoteca> obtenerTodasDiscotecas() {
        List<Discoteca> discotecas = discotecaRepository.findAll();
        // Romper ciclos de referencias y limpiar referencias innecesarias para evitar errores JSON
        for (Discoteca d : discotecas) {
            d.setAdministrador(null); // No enviar el administrador completo en la lista pública
            if (d.getZonas() != null) {
                d.getZonas().forEach(z -> {
                    z.setDiscoteca(null);
                    if (z.getMesas() != null) {
                        z.getMesas().forEach(m -> m.setZona(null));
                    }
                });
            }
        }
        return discotecas;
    }


    /**
     * Registra una nueva discoteca, asociándola con un administrador.
     * @param discoteca La discoteca a registrar.
     * @param idAdmin El ID del administrador que registra la discoteca.
     * @return La discoteca registrada.
     * @throws RuntimeException Si el administrador no es encontrado.
     */
    public Discoteca registrarDiscoteca(Discoteca discoteca, Integer idAdmin) {
        Administradores admin = administradoresRepository.findById(idAdmin)
                .orElseThrow(() -> new RuntimeException("Administrador no encontrado con ID: " + idAdmin));
        discoteca.setAdministrador(admin); // Asigna el administrador
        return discotecaRepository.save(discoteca);
    }

    /**
     * Actualiza una discoteca existente, validando la propiedad del administrador.
     * @param discoteca La discoteca con los datos actualizados.
     * @param idAdmin El ID del administrador que intenta actualizar.
     * @return La discoteca actualizada o null si no existe o no pertenece al administrador.
     */
    public Discoteca actualizarDiscoteca(Discoteca discoteca, Integer idAdmin) {
        Optional<Discoteca> existingDiscotecaOpt = discotecaRepository.findById(discoteca.getNit());
        if (existingDiscotecaOpt.isPresent()) {
            Discoteca existingDiscoteca = existingDiscotecaOpt.get();
            // Asegurarse de que el administrador de la discoteca coincida con el que intenta actualizar
            if (existingDiscoteca.getAdministrador().getIdAdmin().equals(idAdmin)) {
                // Mantener el mismo administrador para la actualización
                discoteca.setAdministrador(existingDiscoteca.getAdministrador());
                return discotecaRepository.save(discoteca);
            }
        }
        return null; // No encontrada o no autorizado
    }

    /**
     * Elimina una discoteca, validando la propiedad del administrador.
     * @param nit El NIT de la discoteca a eliminar.
     * @param idAdmin El ID del administrador que intenta eliminar.
     * @return true si la discoteca fue eliminada, false en caso contrario.
     */
    public boolean eliminarDiscoteca(Integer nit, Integer idAdmin) {
        Optional<Discoteca> discotecaOpt = discotecaRepository.findById(nit);
        if (discotecaOpt.isPresent()) {
            Discoteca discoteca = discotecaOpt.get();
            if (discoteca.getAdministrador().getIdAdmin().equals(idAdmin)) {
                discotecaRepository.deleteById(nit);
                return true;
            }
        }
        return false; // No encontrada o no autorizado
    }
}
