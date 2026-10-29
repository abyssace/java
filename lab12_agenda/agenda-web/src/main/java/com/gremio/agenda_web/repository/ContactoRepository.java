package com.gremio.agenda_web.repository;

import com.gremio.agenda_web.model.Contacto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactoRepository extends JpaRepository<Contacto, Long> {
    // No necesitas escribir nada aquí
    // Spring Data JPA genera save(), findAll(), findById(), deleteById() automáticamente
}