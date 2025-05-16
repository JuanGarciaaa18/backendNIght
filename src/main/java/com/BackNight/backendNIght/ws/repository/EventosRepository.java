package com.BackNight.backendNIght.ws.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.BackNight.backendNIght.ws.entity.Eventos;

public interface EventosRepository extends JpaRepository<Eventos, String> {

}