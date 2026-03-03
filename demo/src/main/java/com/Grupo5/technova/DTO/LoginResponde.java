package com.Grupo5.technova.DTO;

public class LoginResponde {
    private String status;
    private Long id;
    private String nombre;
    private String rol;

    public LoginResponde(String status, Long id, String nombre, String rol) {
        this.status = "Ok";
        this.id = id;
        this.nombre = nombre;
        this.rol = rol;
    }
    //Getters y Setters
    public String getStatus() {
        return status;
    }
    public Long getId() {
        return id;
    }
    public String getNombre() {
        return nombre;
    }
    public String getRol() {
        return rol;
    }
}
