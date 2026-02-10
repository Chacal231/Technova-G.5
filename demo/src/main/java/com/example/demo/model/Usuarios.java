package com.example.demo.model;


public class Usuarios {

    private Integer id;
    private String email;
    private String password;
    private Rol rol;
    public enum Rol {
        CLIENTE, OFICINA, ADMIN
    }

    public Usuarios() {}

    public Usuarios(Integer id, String email, String password, Rol rol) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }
    public Integer getId() { return id; }
    public String getEmail() { return email; }
    public String getPassword() { return password;}
    public Rol getRol() { return rol;}
   
}