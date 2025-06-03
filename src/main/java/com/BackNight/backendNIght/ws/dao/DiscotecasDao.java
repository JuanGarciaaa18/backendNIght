package com.BackNight.backendNIght.ws.dao;

import com.BackNight.backendNIght.ws.entity.Discoteca;
import com.BackNight.backendNIght.ws.repository.DiscotecaRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiscotecasDao {

    @Autowired
    private DiscotecaRepository discotecaRepository;

    public Discoteca consultarDiscotecaIndividual(Integer nit) {
        return discotecaRepository.findById(nit).orElse(null);
    }

    public List<Discoteca> obtenerListaDiscotecas() {
        try {
            List<Discoteca> discotecas = discotecaRepository.findAll();

            // Romper ciclos de referencias para evitar errores JSON
            for (Discoteca d : discotecas) {
                if (d.getZonas() != null) {
                    d.getZonas().forEach(z -> {
                        z.setDiscoteca(null); // evita ciclo con discoteca
                        if (z.getMesas() != null) {
                            z.getMesas().forEach(m -> m.setZona(null)); // evita ciclo con zona
                        }
                    });
                }
            }

            return discotecas;
        } catch (Exception e) {
            System.out.println("Error al obtener la lista de discotecas: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }



    public Discoteca registrarDiscoteca(Discoteca discoteca) {
        return discotecaRepository.save(discoteca);
    }

    public Discoteca actualizarDiscoteca(Discoteca discoteca) {
        if (discotecaRepository.existsById(discoteca.getNit())) {
            return discotecaRepository.save(discoteca);
        }
        return null;
    }

    public void eliminarDiscoteca(Integer nit) {
        if (discotecaRepository.existsById(nit)) {
            discotecaRepository.deleteById(nit);
        }
    }
}