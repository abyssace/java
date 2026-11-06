package com.gremio.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Contacto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String apellido;
    private String email;
    private String telefono;

    // Constructor vacío (requerido por JPA)
    public Contacto() {}

    // Constructor con campos
    public Contacto(String nombre, String apellido, String email, String telefono) {
        this.nombre   = nombre;
        this.apellido = apellido;
        this.email    = email;
        this.telefono = telefono;
    }

    // Getters y Setters
    public Long   getId()        { return id; }
    public String getNombre()    { return nombre; }
    public String getApellido()  { return apellido; }
    public String getEmail()     { return email; }
    public String getTelefono()  { return telefono; }

    public void setId(Long id)             { this.id = id; }
    public void setNombre(String nombre)   { this.nombre = nombre; }
    public void setApellido(String a)      { this.apellido = a; }
    public void setEmail(String email)     { this.email = email; }
    public void setTelefono(String t)      { this.telefono = t; }
}
