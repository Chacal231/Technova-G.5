package com.Grupo5.technova.DTO;

/**
 * DTO que representa el cuerpo de la petición de login.
 * Contiene las credenciales del usuario: email y contraseña.
 */
public class LoginRequest {

    /** Correo electrónico del usuario. */
    private String email;

    /** Contraseña del usuario en texto plano (se verifica contra el hash en el servidor). */
    private String password;

    /** Constructor vacío necesario para la deserialización de Spring Boot. */
    public LoginRequest() {}

    /**
     * Obtiene el email del usuario.
     * @return Email introducido en el formulario de login
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece el email del usuario.
     * @param email Correo electrónico del usuario
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obtiene la contraseña del usuario.
     * @return Contraseña en texto plano
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