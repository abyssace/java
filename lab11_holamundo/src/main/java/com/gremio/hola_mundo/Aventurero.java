package com.gremio.hola_mundo;

public class Aventurero {

    private String nombre;
    private String clase;
    private int nivel;

    public Aventurero(String nombre, String clase, int nivel) {
        this.nombre = nombre;
        this.clase = clase;
        this.nivel = nivel;
    }

    public String getNombre() {
        return nombre;
    }

    public String getClase() {
        return clase;
    }

    public int getNivel() {
        return nivel;
    }
}
