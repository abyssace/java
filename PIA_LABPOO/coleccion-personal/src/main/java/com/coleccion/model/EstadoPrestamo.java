package com.coleccion.model;

public enum EstadoPrestamo {
    ACTIVO("Activo"),
    DEVUELTO("Devuelto"),
    VENCIDO("Vencido");

    private final String descripcion;

    EstadoPrestamo(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
