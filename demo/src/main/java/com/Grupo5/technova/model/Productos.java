package com.Grupo5.technova.model;

import com.google.gson.JsonObject;

/**
 * Modelo que representa un producto del catálogo.
 * Mapea los campos definidos en la tabla 'Productos'.
 */
public class Productos {

    // Identificador único autoincremental en la base de datos
    private Integer id;
    // Código de referencia único para el control de inventario
    private String sku; 
    // Nombre comercial del producto
    private String nombre;
    // Detalle de las características del producto
    private String descripcion;
    // Precio del producto
    private Double precio;
    // Cantidad de unidades disponibles en almacén
    private Integer stock; 
    // Clasificación del producto (Componentes, Periféricos, etc.)
    private String categoria; 
    // Ruta o nombre del archivo de imagen para la interfaz
    private String imagen;

    // Constructor vacío requerido por frameworks y serialización
    public Productos () {}

    // Constructor completo utilizado por el repositorio para mapear los resultados de los procedimientos
    public Productos(Integer id, String sku, String nombre, String descripcion, Double precio, Integer stock, String categoria, String imagen) {
        this.id = id;
        this.sku = sku;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.categoria = categoria;
        this.imagen = imagen;
    }

    // Métodos Getter y Setter para el acceso a las propiedades privadas
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

    /**
     * Serializa el objeto a un formato JSON para la API.
     * Facilita la construcción manual de la respuesta solicitada en el controlador.
     * @return JsonObject con todos los atributos del producto.
     */
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
