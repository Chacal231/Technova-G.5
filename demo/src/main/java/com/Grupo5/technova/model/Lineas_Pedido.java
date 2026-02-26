package com.Grupo5.technova.model;

/**
 * Modelo que representa el detalle individual de cada producto dentro de un pedido.
 * Mapea los campos definidos en la tabla 'Lineas_Pedido'.
 */
public class Lineas_Pedido {

    // Identificador único de la línea de pedido
    private Integer id;
    // Referencia al pedido principal (cabecera) al que pertenece esta línea
    private Integer id_pedido;
    // Referencia al producto adquirido
    private Integer id_producto;
    // Cantidad de unidades compradas del producto
    private Integer cantidad;
    // Precio del producto en el momento de la compra
    private Double precio_unitario;

    // Constructor vacío requerido por frameworks y serialización
    public Lineas_Pedido() {}

    // Constructor completo para instanciar el detalle con datos de la BD o del carrito
    public Lineas_Pedido(Integer id_pedido, Integer id_producto, Integer cantidad, Double precio_unitario) {
        this.id_pedido = id_pedido;
        this.id_producto = id_producto;
        this.cantidad = cantidad;
        this.precio_unitario = precio_unitario;
    }

    // Métodos Getter y Setter para el acceso a las propiedades privadas
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getId_pedido() { return id_pedido; }
    public void setId_pedido(Integer id_pedido) { this.id_pedido = id_pedido; }

    public Integer getId_producto() { return id_producto; }
    public void setId_producto(Integer id_producto) { this.id_producto = id_producto; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Double getPrecio_unitario() { return precio_unitario; }
    public void setPrecio_unitario(Double precio_unitario) { this.precio_unitario = precio_unitario; }
}