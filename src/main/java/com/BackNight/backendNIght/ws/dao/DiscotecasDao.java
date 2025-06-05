package com.BackNight.backendNIght.ws.dao;

import com.BackNight.backendNIght.ws.entity.Discoteca;
import com.BackNight.backendNIght.ws.repository.DiscotecaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscotecasDao {

    @Autowired
    private DiscotecaRepository discotecaRepository;

    public Discoteca consultarDiscotecaIndividual(Integer nit) {
        return discotecaRepository.findById(nit).orElse(null);
    }

    public List<Discoteca> obtenerListaDiscotecas() {
        List<Discoteca> discotecas = discotecaRepository.findAll();

        // Romper ciclos de referencias para evitar errores JSON
        for (Discoteca d : discotecas) {
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
