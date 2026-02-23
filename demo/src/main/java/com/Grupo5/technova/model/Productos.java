package com.Grupo5.technova.model;
import com.google.gson.JsonObject;

public class Productos {

    private Integer id;
    private String sku; 
    private String nombre;
    private String descripcion;
    private double precio;
    private Integer stock; 
    private String categoria; 
    private String imagen;

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
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }


    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        json.addProperty("id", id);
        json.addProperty("sku", sku);
        json.addProperty("nombre", nombre);
        json.addProperty("descripcion", descripcion);
        json.addProperty("precio", precio);
        json.addProperty("stock", stock);
        json.addProperty("categoria", categoria);
        json.addProperty("imagen", imagen);
        return json;
    }
}

