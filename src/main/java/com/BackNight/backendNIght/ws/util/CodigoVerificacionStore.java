package com.BackNight.backendNIght.ws.util;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CodigoVerificacionStore {
    private static final Map<String, String> codigos = new HashMap<>();

    public void guardar(String correo, String codigo) {
        codigos.put(correo, codigo);
    }

    public boolean verificar(String correo, String codigo) {
        return codigos.containsKey(correo) && codigos.get(correo).equals(codigo);
    }
}
