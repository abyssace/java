package com.coleccion.service;

import com.coleccion.dao.PrestamoDao;
import com.coleccion.model.Prestamo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public final class PrestamoService {
    private final PrestamoDao dao;

    public PrestamoService(PrestamoDao dao) {
        this.dao = dao;
    }

    public Prestamo prestar(long itemId, String amigo, LocalDate fechaPrestamo, LocalDate fechaLimite) {
        if (itemId <= 0) {
            throw new ValidacionException("Selecciona un ítem válido.");
        }
        if (amigo == null || amigo.isBlank()) {
            throw new ValidacionException("El nombre de la persona es obligatorio.");
        }
        if (fechaPrestamo == null || fechaLimite == null) {
            throw new ValidacionException("Las fechas de préstamo y límite son obligatorias.");
        }
        if (fechaLimite.isBefore(fechaPrestamo)) {
            throw new ValidacionException("La fecha límite no puede ser anterior al préstamo.");
        }
        if (dao.activoPorItem(itemId).isPresent()) {
            throw new ValidacionException("Este ítem ya está prestado. Registra su devolución antes de prestarlo otra vez.");
        }
        return dao.prestar(itemId, amigo.trim(), fechaPrestamo, fechaLimite);
    }

    public Prestamo devolver(long prestamoId, LocalDate fechaDevolucion) {
        Prestamo prestamo = dao.buscar(prestamoId)
                .orElseThrow(() -> new ValidacionException("El préstamo ya no existe."));
        if (prestamo.fechaDevolucion() != null) {
            throw new ValidacionException("Este préstamo ya fue devuelto.");
        }
        if (fechaDevolucion == null || fechaDevolucion.isBefore(prestamo.fechaPrestamo())) {
            throw new ValidacionException("La devolución no puede ser anterior al préstamo.");
        }
        return dao.devolver(prestamo, fechaDevolucion);
    }

    public Optional<Prestamo> activoPorItem(long itemId) {
        return dao.activoPorItem(itemId);
    }

    public List<Prestamo> historial(long itemId) {
        return dao.historial(itemId);
    }

    public List<Prestamo> activos() {
        return dao.activos();
    }
}
