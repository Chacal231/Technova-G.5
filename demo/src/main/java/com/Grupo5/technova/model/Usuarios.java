package com.Grupo5.technova.model;

import com.google.gson.JsonObject;

/**
 * Modelo que representa la entidad de Usuario en el sistema.
 * Mapea los datos de la tabla 'Usuarios' de la base de datos.
 */
public class Usuarios {

    // Identificador único del usuario
    private Integer id;
    // Correo electrónico utilizado como credencial de acceso
    private String email;
    // Nombre visible del usuario
    private String nombre;
    // Contraseña del usuario 
    private String password;
    // Rol asignado para control de acceso
    private String rol;

    // Constructor vacío requerido por frameworks y serialización
    public Usuarios() {}

    // Constructor completo para instanciar el modelo con datos de la BD (sin nombre)
    public Usuarios(Integer id, String email, String password, String rol) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }

    // Constructor completo para instanciar el modelo con datos de la BD (con nombre)
    public Usuarios(Integer id, String email, String nombre, String password, String rol) {
        this.id = id;
        this.email = email;
        this.nombre = nombre;
        this.password = password;
        this.rol = rol;
    }

    // Métodos Getter y Setter para el acceso a las propiedades privadas
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    /**
     * Convierte el objeto Java a un formato JSON compatible con Google Gson.
     * Utilizado para enviar respuestas estructuradas en el proceso de Login.
     * @return JsonObject con la información del usuario (excluyendo la contraseña por seguridad).
     */
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        json.addProperty("status", "ok");
        json.addProperty("id", id);        
        json.addProperty("email", email);       
        json.addProperty("nombre", nombre);
        json.addProperty("rol", rol);
        return json;
    }
}