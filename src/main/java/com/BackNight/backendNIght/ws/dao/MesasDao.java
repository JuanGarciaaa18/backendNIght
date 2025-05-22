package com.BackNight.backendNIght.ws.dao;

import com.BackNight.backendNIght.ws.entity.Mesa;
import com.BackNight.backendNIght.ws.rest.MesaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mesas")
@CrossOrigin("*")
public class MesasDao {

    private final MesaService mesaService;

    public MesasDao(MesaService mesaService) {
        this.mesaService = mesaService;
    }

    @GetMapping
    public List<Mesa> obtenerMesas() {
        return mesaService.obtenerTodasMesas();
    }
}