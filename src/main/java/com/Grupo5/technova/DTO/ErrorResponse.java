package com.Grupo5.technova.DTO;
public class ErrorResponse {    
    private String error;
    private String mensaje;

    public ErrorResponse(String error, String mensaje) {
        this.error = error;
        this.mensaje = mensaje;
    }
    public String getError() {
        return error;
    }
}