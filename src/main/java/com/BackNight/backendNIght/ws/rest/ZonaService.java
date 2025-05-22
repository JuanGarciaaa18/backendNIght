package com.BackNight.backendNIght.ws.rest;
import com.BackNight.backendNIght.ws.entity.Zona;
import com.BackNight.backendNIght.ws.repository.ZonaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ZonaService {
    private final ZonaRepository zonaRepository;

    public ZonaService(ZonaRepository zonaRepository) {
        this.zonaRepository = zonaRepository;
    }

    public List<Zona> obtenerTodasZonas() {
        return zonaRepository.findAll();
    }
}