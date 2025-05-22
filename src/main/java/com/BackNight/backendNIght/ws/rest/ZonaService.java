package com.BackNight.backendNIght.ws.rest;
import com.BackNight.backendNIght.ws.entity.Mesa;
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
        List<Zona> zonas = zonaRepository.findAll();
        for (Zona zona : zonas) {
            zona.setDiscoteca(null); // opcional si tienes la relación
            if (zona.getMesas() != null) {
                for (Mesa mesa : zona.getMesas()) {
                    mesa.setZona(null); // rompe el ciclo para test
                }
            }
        }
        return zonas;
    }}
