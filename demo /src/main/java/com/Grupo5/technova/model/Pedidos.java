package com.Grupo5.technova.model;

import java.util.List;
import com.google.gson.JsonObject;

/**
 * Modelo que representa la cabecera de un pedido.
 * Mapea los campos definidos en la tabla 'Pedidos'.
 */
public class Pedidos {

    // Identificador único del pedido
    private Integer id;
    // Referencia al usuario que realiza la compra
    private Integer id_usuario; 
    // Fecha y hora del registro del pedido
    private String fecha;
    // Importe total acumulado del pedido
    private Double total_pedido;
    // Estado actual de la orden (Pendiente, Enviado, etc.)
    private String estado;
    // Lista de productos detallados asociados a este pedido
    private List<Lineas_Pedido> lineas;

    // Constructor vacío requerido por frameworks y serialización
    public Pedidos() {}

    // Constructor completo para instanciar el modelo con datos de la BD
    public Pedidos(Integer id, Integer id_usuario, String fecha, Double total_pedido, String estado) {
        this.id = id;
        this.id_usuario = id_usuario;
        this.fecha = fecha;
        this.total_pedido = total_pedido;
        this.estado = estado;
    }

    // Métodos Getter y Setter para el acceso a las propiedades privadas
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getId_usuario() { return id_usuario; }
    public void setId_usuario(int id_usuario) { this.id_usuario = id_usuario; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public double getTotal_pedido() { return total_pedido; }
    public void setTotal_pedido(double total_pedido) { this.total_pedido = total_pedido; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public List<Lineas_Pedido> getLineas() { return lineas; }
    public void setLineas(List<Lineas_Pedido> lineas) { this.lineas = lineas; }

    /**
     * Convierte el objeto Java a un formato JSON compatible con Google Gson.
     * Utilizado para enviar respuestas estructuradas desde el controlador.
     * @return JsonObject con la información de la cabecera del pedido.
     */
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        json.addProperty("id", id);
        json.addProperty("id_usuario", id_usuario);
        json.addProperty("fecha", fecha);
        json.addProperty("total_pedido", total_pedido);
        json.addProperty("estado", estado);
        return json;
    }
}