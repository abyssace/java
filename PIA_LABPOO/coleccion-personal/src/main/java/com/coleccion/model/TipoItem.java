package com.coleccion.model;

public enum TipoItem {
    LIBRO("Libro"),
    PELICULA("Película"),
    VIDEOJUEGO("Videojuego"),
    CARTA("Carta");

    private final String descripcion;

    TipoItem(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
