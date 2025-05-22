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
            return discotecaRepository.findAll();
        } catch (Exception e) {
            System.out.println("Error al obtener la lista de discotecas: " + e.getMessage());
            e.printStackTrace(); // Esto te mostrará el error en consola
            throw e;  // Re-lanza para que el controlador también se entere
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
