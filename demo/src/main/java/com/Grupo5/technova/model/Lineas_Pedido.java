package com.Grupo5.technova.model;

public class Lineas_Pedido {
    private Integer id;
    private Integer id_pedido;
    private Integer id_producto;
    private Integer cantidad;
    private Double precio_unitario_momento;

    
    public Lineas_Pedido() {}

    public Lineas_Pedido(Integer id_pedido, Integer id_producto, Integer cantidad, Double precio_unitario_momento) {
        this.id_pedido = id_pedido;
        this.id_producto = id_producto;
        this.cantidad = cantidad;
        this.precio_unitario_momento = precio_unitario_momento;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getId_pedido() { return id_pedido; }
    public void setId_pedido(Integer id_pedido) { this.id_pedido = id_pedido; }

    public Integer getId_producto() { return id_producto; }
    public void setId_producto(Integer id_producto) { this.id_producto = id_producto; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Double getPrecio_unitario_momento() { return precio_unitario_momento; }
    public void setPrecio_unitario_momento(Double precio_unitario_momento) { 
        this.precio_unitario_momento = precio_unitario_momento; 
    }
}