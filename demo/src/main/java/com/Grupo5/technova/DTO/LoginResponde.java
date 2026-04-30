package com.Grupo5.technova.DTO;

/**
 * DTO que representa la respuesta del servidor tras un login exitoso.
 * Devuelve los datos del usuario autenticado junto con su rol en el sistema.
 *
 * Nota: el nombre de la clase conserva el nombre original del proyecto (LoginResponde).
 */
public class LoginResponde {

    /** Estado de la operación. Siempre "Ok" en caso de login correcto. */
    private String status;

    /** Identificador único del usuario autenticado. */
    private Long id;

    /** Nombre visible del usuario. */
    private String nombre;

    /** Rol asignado al usuario (ADMIN, OFICINA, CLIENTE). */
    private String rol;

    /**
     * Constructor con todos los campos.
     * @param status  Estado de la respuesta
     * @param id      ID del usuario
     * @param nombre  Nombre del usuario
     * @param rol     Rol del usuario en el sistema
     */
    public LoginResponde(String status, Long id, String nombre, String rol) {
        this.status = "Ok";
        this.id = id;
        this.nombre = nombre;
        this.rol = rol;
    }

    /**
     * Obtiene el estado de la respuesta.
     * @return "Ok" si el login fue exitoso
     */
    public String getStatus() {
        return status;
    }

    /**
     * Obtiene el identificador del usuario.
     * @return ID del usuario autenticado
     */
    public Long getId() {
        return id;
    }

    /**
     * Obtiene el nombre del usuario.
     * @return Nombre visible del usuario
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Obtiene el rol del usuario.
     * @return Rol asignado (ADMIN, OFICINA o CLIENTE)
     */
    public String getRol() {
        return rol;
    }
}
