package com.Grupo5.technova.DTO;

/**
 * DTO que representa el cuerpo de la petición de registro de un nuevo usuario.
 * Contiene los datos básicos necesarios para crear una cuenta: nombre, email y contraseña.
 */
public class RegisterRequest {

    /** Nombre visible del nuevo usuario. */
    private String nombre;

    /** Correo electrónico que actuará como identificador único de la cuenta. */
    private String email;

    /** Contraseña en texto plano (se hashea con BCrypt antes de almacenarse). */
    private String password;

    /**
     * Obtiene el nombre del usuario.
     * @return Nombre introducido en el formulario de registro
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del usuario.
     * @param nombre Nombre del nuevo usuario
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el email del usuario.
     * @return Correo electrónico introducido en el formulario de registro
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece el email del usuario.
     * @param email Correo electrónico del nuevo usuario
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obtiene la contraseña del usuario.
     * @return Contraseña en texto plano antes de ser hasheada
     */
    public String getPassword() {
        return password;
    }

    /**
     * Establece la contraseña del usuario.
     * @param password Contraseña en texto plano
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
