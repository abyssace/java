package com.coleccion.model;

import java.util.Map;

public record Estadisticas(
        long total,
        Map<TipoItem, Long> porTipo,
        double promedioCalificacion,
        Map<EstadoItem, Long> porEstado,
        long prestamosActivos) {
    public Estadisticas {
        porTipo = Map.copyOf(porTipo);
        porEstado = Map.copyOf(porEstado);
    }
}
