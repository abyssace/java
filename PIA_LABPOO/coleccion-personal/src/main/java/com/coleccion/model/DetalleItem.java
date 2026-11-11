package com.coleccion.model;

public sealed interface DetalleItem {
    record Libro(String autor, String isbn, String editorial) implements DetalleItem {}
    record Pelicula(String director, Integer duracionMinutos) implements DetalleItem {}
    record Videojuego(String plataforma, String desarrollador) implements DetalleItem {}
    record Carta(String juego, String rareza, String condicion) implements DetalleItem {}
}
