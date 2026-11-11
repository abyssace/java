package com.coleccion.model;

import java.time.LocalDate;

public record Prestamo(
        Long id,
        long itemId,
        String amigo,
        LocalDate fechaPrestamo,
        LocalDate fechaLimite,
        LocalDate fechaDevolucion) {

    public EstadoPrestamo estadoEn(LocalDate hoy) {
        if (fechaDevolucion != null) {
            return EstadoPrestamo.DEVUELTO;
        }
        return fechaLimite.isBefore(hoy) ? EstadoPrestamo.VENCIDO : EstadoPrestamo.ACTIVO;
    }

    public Prestamo conDevolucion(LocalDate fecha) {
        return new Prestamo(id, itemId, amigo, fechaPrestamo, fechaLimite, fecha);
    }
}
