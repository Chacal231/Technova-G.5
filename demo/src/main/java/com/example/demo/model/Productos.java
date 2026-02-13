package com.example.demo.model;
public class Productos {

    private Integer id;
    private String sku; 
    private String nombre;
    private String descripcion;
    private double precio;
    private Integer stock; 
    private String categoria; 
    private String imagen;
    public enum categoria {
        Componentes, Periféricos, Redes, Software
    }

    public Productos() {}

    public Productos(Integer id, String sku, String nombre, String descripcion, double precio, Integer stock, String categoria, String imagen) {
        this.id = id;
        this.sku = sku;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.categoria = categoria;
        this.imagen = imagen;
    }
    public Integer getId() { return id; }
    public String getSku() { return sku; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public double getPrecio() { return precio; }
    public Integer getStock() { return stock; }
    public String getCategoria() { return categoria; }
    public String getImagen() { return imagen; }
}

