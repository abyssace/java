package com.coleccion;

import com.coleccion.model.DetalleItem;
import com.coleccion.model.EstadoItem;
import com.coleccion.model.Item;
import com.coleccion.model.TipoItem;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public final class ItemFixtures {
    private ItemFixtures() {}

    public static Item deTipo(TipoItem tipo, String titulo) {
        DetalleItem detalle = switch (tipo) {
            case LIBRO -> new DetalleItem.Libro("Autor", null, "Editorial");
            case PELICULA -> new DetalleItem.Pelicula("Directora", 120);
            case VIDEOJUEGO -> new DetalleItem.Videojuego("PC", "Estudio");
            case CARTA -> new DetalleItem.Carta("Juego", "Rara", "Buena");
        };
        return new Item(null, titulo, tipo, 2020, "Notas", LocalDate.of(2026, 1, 10),
                new BigDecimal("19.99"), null, EstadoItem.PENDIENTE, 7, "Reseña",
                detalle, Set.of("favorito"));
    }

    public static Item libro(String titulo) {
        return deTipo(TipoItem.LIBRO, titulo);
    }
}
