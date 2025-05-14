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

    public Discoteca consultarDiscotecaIndividual(String nit) {
        return discotecaRepository.findById(nit).orElse(null);
    }

    public List<Discoteca> obtenerListaDiscotecas() {
        return discotecaRepository.findAll();
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

    public void eliminarDiscoteca(String nit) {
        if (discotecaRepository.existsById(nit)) {
            discotecaRepository.deleteById(nit);
        }
    }
}