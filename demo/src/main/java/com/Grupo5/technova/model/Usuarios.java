package com.Grupo5.technova.model;
import com.google.gson.JsonObject;

public class Usuarios {

    private Integer id;
    private String email;
    private String password;
    private String rol;
   

    public Usuarios() {}

    public Usuarios(Integer id, String email, String password, String rol) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }
    public Integer getId() { return id; }
    public String getEmail() { return email; }
    public String getPassword() { return password;}
    public String getRol() { return rol;}

    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        json.addProperty("id", id);        
        json.addProperty("email", email);       
        json.addProperty("password", password);
        json.addProperty("rol", rol);
        return json;
    }
   
}