package com.BackNight.backendNIght.ws.dao;

import com.BackNight.backendNIght.ws.entity.Zona;
import com.BackNight.backendNIght.ws.rest.ZonaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zonas")
@CrossOrigin("*")
public class ZonasDao {

    private final ZonaService zonaService;

    public ZonasDao(ZonaService zonaService) {
        this.zonaService = zonaService;
    }

    @GetMapping
    public List<Zona> obtenerZonas() {
        return zonaService.obtenerTodasZonas();
    }
}