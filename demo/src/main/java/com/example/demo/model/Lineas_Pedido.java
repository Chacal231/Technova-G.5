package com.example.demo.model;
public class Lineas_Pedido {

    private Integer id_pedido;
    private String id_producto; 
    private Integer cantidad;
    private String nombre;
    private double precio_unitario_momento;
 
    public Lineas_Pedido() {}

    public Lineas_Pedido(Integer id_pedido, String id_producto, Integer cantidad, String nombre, double precio_unitario_momento) {
        this.id_pedido = id_pedido;
        this.id_producto = id_producto;
        this.cantidad = cantidad;
        this.nombre = nombre;
        this.precio_unitario_momento = precio_unitario_momento;
    }
    public Integer getId_pedido() { return id_pedido; }
    public String getId_producto() { return id_producto; }
    public Integer getCantidad() { return cantidad; }
    public String getNombre() { return nombre; }
    public double getPrecio_unitario_momento() { return precio_unitario_momento; }
}