package com.mcu.alkewalletinterface.model;

public class Usuario {

    private int id;
    private String nombre;

    private String email;
    private String contrasenia;
    private int edad;

    public Usuario( int id, String nombre, String email, String contrasenia, int edad) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.contrasenia = contrasenia;
        setEdad(edad);
    }

    public int obtenerId() {return id;}
    public String obtenerNombre() {return nombre;}

    public String obtenerEmail() {return email;}
    public String obtenerContraseña() {return contrasenia;}
    public int obtenerEdad() {return edad;}

    public void setId(int id) {this.id = id;}
    //Implementaciones futuras para editar perfil
    public void setNombre(String nombre) {this.nombre = nombre;}
    public void setContraseña(String contraseña) {this.contrasenia = contraseña;}
    public void setEdad(int edad) {
        if (edad < 0 || edad > 100) {
            throw new IllegalArgumentException("La edad debe estar entre 0 y 100.");
        }
        this.edad = edad;}

}
