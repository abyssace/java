package com.coleccion.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public record Item(
        Long id,
        String titulo,
        TipoItem tipo,
        Integer anio,
        String notas,
        LocalDate fechaAdquisicion,
        BigDecimal precioPagado,
        String portadaRuta,
        EstadoItem estado,
        Integer calificacion,
        String resena,
        DetalleItem detalle,
        Set<String> etiquetas) {

    public Item {
        etiquetas = etiquetas == null ? Set.of() : Set.copyOf(etiquetas);
    }

    public Item conId(Long nuevoId) {
        return new Item(nuevoId, titulo, tipo, anio, notas, fechaAdquisicion, precioPagado,
                portadaRuta, estado, calificacion, resena, detalle, etiquetas);
    }
}
