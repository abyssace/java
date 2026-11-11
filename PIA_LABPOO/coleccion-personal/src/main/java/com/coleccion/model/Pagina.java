package com.coleccion.model;

import java.util.List;

public record Pagina<T>(List<T> elementos, long total, int pagina, int totalPaginas) {
    public Pagina {
        elementos = List.copyOf(elementos);
    }
}
