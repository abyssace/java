package com.coleccion.model;

public enum EstadoItem {
    PENDIENTE("Pendiente"),
    EN_PROGRESO("En progreso"),
    COMPLETADO("Completado"),
    ABANDONADO("Abandonado");

    private final String descripcion;

    EstadoItem(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
