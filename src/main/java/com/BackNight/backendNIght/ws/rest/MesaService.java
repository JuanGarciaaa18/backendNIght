package com.BackNight.backendNIght.ws.rest;

import com.BackNight.backendNIght.ws.entity.Mesa;
import com.BackNight.backendNIght.ws.repository.MesaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MesaService {
    private final MesaRepository mesaRepository;

    public MesaService(MesaRepository mesaRepository) {
        this.mesaRepository = mesaRepository;
    }

    public List<Mesa> obtenerTodasMesas() {
        return mesaRepository.findAll();
    }
}