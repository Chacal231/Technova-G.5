package com.Grupo5.technova.DTO;

/**
 * DTO utilizado para estructurar las respuestas de error de la API.
 * Incluye un código de error y un mensaje descriptivo para el cliente.
 */
public class ErrorResponse {

    /** Código o tipo de error producido. */
    private String error;

    /** Mensaje explicativo del error para el cliente. */
    private String mensaje;

    /**
     * Constructor con todos los campos.
     * @param error   Código o tipo de error
     * @param mensaje Descripción del error
     */
    public ErrorResponse(String error, String mensaje) {
        this.error = error;
        this.mensaje = mensaje;
    }

    /**
     * Obtiene el código de error.
     * @return Código de error
     */
    public String getError() {
        return error;
    }

    /**
     * Obtiene el mensaje descriptivo del error.
     * @return Mensaje de error
     */
    public String getMensaje() {
        return mensaje;
    }
}