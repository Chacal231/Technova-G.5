package com.Grupo5.technova.model;
import java.util.List;

import com.google.gson.JsonObject;


public class Pedidos {
    private Integer id;
    private Integer id_usuario; 
    private String fecha;
    private Double total_pedido;
    private String estado;
    private List<Lineas_Pedido> lineas;

    public Pedidos() {}

    public Pedidos(Integer id, Integer id_usuario, String fecha, Double total_pedido, String estado) {
        this.id = id;
        this.id_usuario = id_usuario;
        this.fecha = fecha;
        this.total_pedido = total_pedido;
        this.estado = estado;
    }

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